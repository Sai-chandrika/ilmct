package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.FileDocument;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.FileDocumentNotFoundException;
import com.inspirage.ilct.exceptions.ImproperDataException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileManagementService {
    FileDocument uploadFile(MultipartFile multipartFile, HttpServletRequest request, String comment) throws IOException;

    FileDocument downloadFile(String id, HttpServletRequest request) throws IOException, FileDocumentNotFoundException;

    void deleteFileById(String id, HttpServletRequest request, String loadId) throws IOException, FileDocumentNotFoundException;

    String uploadFileToLocation(MultipartFile multipartFile, String fileName, HttpServletRequest request) throws IOException, ImproperDataException, ApplicationSettingsNotFoundException;

    byte[] readFile(String fileName, HttpServletRequest request) throws IOException, FileDocumentNotFoundException, ApplicationSettingsNotFoundException, ImproperDataException;

    List<FileDocument> uploadMultipleFiles(List<MultipartFile> files, HttpServletRequest request, String comment) throws IOException;
}
