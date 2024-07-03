package com.inspirage.ilct.controller;

import com.inspirage.ilct.documents.FileDocument;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.FileDocumentNotFoundException;
import com.inspirage.ilct.exceptions.ImproperDataException;
import com.inspirage.ilct.service.FileManagementService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v3/files")
//@Api
public class FileManagementController {

    @Autowired
    FileManagementService fileManagementService;

    @PostMapping("/upload-file")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request, @RequestParam(required = false) String comment) throws IOException {
        FileDocument fileId = fileManagementService.uploadFile(file, request, comment);
        return new ApiResponse(HttpStatus.OK, fileId);
    }

    @DeleteMapping("/delete-file-by-id")
    public ApiResponse deleteFileById(@RequestParam String id, HttpServletRequest request) throws IOException, FileDocumentNotFoundException {
        fileManagementService.deleteFileById(id, request, null);
        return new ApiResponse("File Deleted Successfully", HttpStatus.OK);
    }

    @GetMapping("/show-file")
    public void showFile(@RequestParam String id, HttpServletResponse servletResponse, HttpServletRequest request) throws IOException, FileDocumentNotFoundException {
        FileDocument response= fileManagementService.downloadFile(id, request);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if(!Utility.isEmpty(response.getFileType()))
            mediaType= MediaType.valueOf(response.getFileType());

        servletResponse.setContentType(mediaType.getType());
        FileCopyUtils.copy(response.getFile(), servletResponse.getOutputStream());
    }

    @GetMapping("/download-file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String id, HttpServletRequest request) throws IOException, FileDocumentNotFoundException {
        FileDocument response= fileManagementService.downloadFile(id, request);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if(!Utility.isEmpty(response.getFileType()))
            mediaType= MediaType.valueOf(response.getFileType());

        return ResponseEntity.ok().contentType(mediaType)
                .header("fileName", response.getFileName())
                .header("fileId", response.getFileId())
                .header("fileType", response.getFileType())
                .header("uploadedBy", response.getUploadedBy())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .body(StreamUtils.copyToByteArray(response.getFile()));
    }

    @PostMapping("/upload-multiple-files")
    public ApiResponse uploadMultipleFile(@RequestParam("files") List<MultipartFile> files, HttpServletRequest request,@RequestParam(required = false) String comment) throws IOException {
        List<FileDocument> fileIds= fileManagementService.uploadMultipleFiles(files,request, comment);
        return new ApiResponse(HttpStatus.OK, fileIds);
    }

    @PostMapping("/upload-file-to-location")
    public ApiResponse uploadFileToLocation(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName, HttpServletRequest request) throws IOException, ImproperDataException, ApplicationSettingsNotFoundException {
        String fileId= fileManagementService.uploadFileToLocation(file, fileName,request);
        return new ApiResponse(HttpStatus.OK, fileId);
    }

    @GetMapping("/read-file")
    public void readFileFromLocation(@RequestParam String fileName, HttpServletResponse servletResponse, HttpServletRequest request) throws IOException, FileDocumentNotFoundException, ApplicationSettingsNotFoundException, ImproperDataException {
        byte[] response= fileManagementService.readFile(fileName, request);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        servletResponse.setContentType(mediaType.getType());
        FileCopyUtils.copy(response, servletResponse.getOutputStream());
    }

}
