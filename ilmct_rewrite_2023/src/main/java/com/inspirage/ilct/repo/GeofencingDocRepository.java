package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.GeofenceDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GeofencingDocRepository extends MongoRepository<GeofenceDoc, String>{

	Optional<GeofenceDoc> findByLocationDocId(String locationDocId);

    List<GeofenceDoc> findByLocationDocIdIn(List<String> locationDocIds);
}
