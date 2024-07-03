package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.documents.FileDocument;
import com.inspirage.ilct.documents.Log;
import com.inspirage.ilct.enums.ActionEnum;
import com.inspirage.ilct.enums.MessageTypeEnum;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.FileDocumentNotFoundException;
import com.inspirage.ilct.exceptions.ImproperDataException;
import com.inspirage.ilct.repo.FileManagementRepository;
import com.inspirage.ilct.service.ApplicationSettingsService;
import com.inspirage.ilct.service.FileManagementService;
import com.inspirage.ilct.service.LoggerService;
import com.inspirage.ilct.util.Constants;
import com.inspirage.ilct.util.Utility;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class FileManagementServiceImpl implements FileManagementService {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Autowired
    GridFsTemplate gridFsOperations;


    @Autowired
    TokenUtilService tokenUtilService;

    @Autowired
    FileManagementRepository fileManagementRepository;

    @Autowired
    ApplicationSettingsService settingsService;


    @Autowired
    LoggerService loggerService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDocument uploadFile(MultipartFile multipartFile, HttpServletRequest request, String comment) throws IOException {
        validateFile(multipartFile, request);
        Date date = new Date();
        String userId = tokenUtilService.getUserId(request);
        DBObject metaData = new BasicDBObject();
        metaData.put("type", multipartFile.getContentType());
        metaData.put("title", multipartFile.getOriginalFilename());
        metaData.put("uploadedBy", userId);
        metaData.put("uploadDate", date);

        ObjectId store = gridFsOperations.store(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), metaData);

        FileDocument fileDocument = new FileDocument();
        fileDocument.setFileName(multipartFile.getOriginalFilename());
        fileDocument.setFileType(multipartFile.getContentType());
        fileDocument.setUploadedBy(userId);
        fileDocument.setComment(comment);
        fileDocument.setCreatedDate(date);
        fileDocument.setFileId(store.toString());

        loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_FILE).message(Constants.FILE_SAVED).type(MessageTypeEnum.INFO).localDateTime(LocalDateTime.now()).build(), userId);
        logger.info("File uploaded successfully");
        return fileManagementRepository.save(fileDocument);
    }

    private void validateFile(MultipartFile multipartFile, HttpServletRequest request) throws InputMismatchException {
        if (multipartFile.isEmpty() || Utility.isEmpty(multipartFile.getOriginalFilename()) || Utility.isEmpty(multipartFile.getContentType())) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_FILE).message(Constants.FILE_INVALID).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new InputMismatchException("Uploaded File is not valid");
        }
    }


    @Override
    public FileDocument downloadFile(String id, HttpServletRequest request) throws FileDocumentNotFoundException {
        //GridFSDBFile file = gridFsOperations.findOne(new Query(Criteria.where("_id").is(id)));

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        // Access the database
        MongoDatabase database = mongoClient.getDatabase("ilct_dev_V2");

        // Create a GridFSBucket object
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);

        GridFSFile file = gridFsOperations.findOne(new Query(Criteria.where("_id").is(id)));

        FileDocument fileDocument = fileManagementRepository.findByFileId(id);

        if (file == null || fileDocument == null) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.DOWNLOADING_FILE).message(Constants.FILE_ID_INVALID).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new FileDocumentNotFoundException("No file document found with given file id");
        }

        ObjectId fileId = file.getObjectId();

        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId);
        fileDocument.setFile(downloadStream);

        logger.info("returning file for id" + id);
        return fileDocument;
    }

    @Override
    public void deleteFileById(String fileId, HttpServletRequest request, String loadId) throws FileDocumentNotFoundException {
        GridFSFile file = gridFsOperations.findOne(new Query(Criteria.where("_id").is(fileId)));
        FileDocument fileDocument = fileManagementRepository.findByFileId(fileId);
        if (file == null || fileDocument == null) {
            if (!Utility.isEmpty(loadId)) {
                loggerService.saveLog(Log.builder().actionEnum(ActionEnum.DOWNLOADING_FILE).message(Constants.FILE_NOT_FOUND).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).loadId(loadId).build(), request);
            } else
                loggerService.saveLog(Log.builder().actionEnum(ActionEnum.DOWNLOADING_FILE).message(Constants.FILE_NOT_FOUND).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new FileDocumentNotFoundException("No file document found with given file id");
        }
        gridFsOperations.delete(new Query(Criteria.where("_id").is(fileId)));
        fileManagementRepository.delete(fileDocument);
        logger.info("delete file by id" + fileId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileDocument> uploadMultipleFiles(List<MultipartFile> files, HttpServletRequest request, String comment) throws InputMismatchException, IOException {
        files.forEach(f -> validateFile(f, request));
        List<FileDocument> uploadFileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadFileIds.add(uploadFile(file, request, comment));
        }
        return uploadFileIds;
    }


    @Override
    public String uploadFileToLocation(MultipartFile multipartFile, String fileName, HttpServletRequest request) throws IOException, ApplicationSettingsNotFoundException, ImproperDataException {
        validateFile(multipartFile, request);

        ApplicationSettings applicationSettings = checkFileStoreLocation(request);

        String fileExt = Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf("."));

        Path path = Paths.get(applicationSettings.getLocaltionToStoreFile() + fileName + fileExt);

        Path uploadPath = Files.write(path, multipartFile.getBytes());

        return uploadPath.getFileName().toString();
    }

    private ApplicationSettings checkFileStoreLocation(HttpServletRequest request) throws ApplicationSettingsNotFoundException, ImproperDataException {
        ApplicationSettings applicationSettings = settingsService.getApplicationSetting();

        if (!ObjectUtils.isEmpty(applicationSettings.getLocaltionToStoreFile())) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.UPLOADING_FILE).message(Constants.FILE_LOCATION_NOT_FOUND).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new ImproperDataException("Location to store file is not available");
        }
        return applicationSettings;
    }

    @Override
    public byte[] readFile(String fileName, HttpServletRequest request) throws IOException, FileDocumentNotFoundException, ApplicationSettingsNotFoundException, ImproperDataException {
        if (!ObjectUtils.isEmpty(fileName)) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.DOWNLOADING_FILE).message(Constants.FILE_NAME_INVALID).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new FileDocumentNotFoundException("Invalid file name ");
        }
        ApplicationSettings applicationSettings = checkFileStoreLocation(request);
        Path path = Paths.get(applicationSettings.getLocaltionToStoreFile() + fileName);
        byte[] bytes = Files.readAllBytes(path);

        if (bytes.length == 0) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.DOWNLOADING_FILE).message(Constants.FILE_NOT_FOUND).type(MessageTypeEnum.ERROR).localDateTime(LocalDateTime.now()).build(), request);
            throw new FileDocumentNotFoundException("No File Found With Given File Name");
        }

        return bytes;
    }

}






