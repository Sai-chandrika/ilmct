package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.OutBoundEventDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OutBoundEventRepository extends MongoRepository<OutBoundEventDoc, String> {
	OutBoundEventDoc findTopByOrderByCreatedDateDesc();
}
