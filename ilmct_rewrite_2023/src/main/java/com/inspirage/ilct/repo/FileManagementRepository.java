package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileManagementRepository extends MongoRepository<FileDocument,String> {

    FileDocument findByFileId(String fileId);

}
