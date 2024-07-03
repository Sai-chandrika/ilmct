package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.D1ProcessedDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface D1ProcessedEventRepository extends MongoRepository<D1ProcessedDoc, String> {
}
