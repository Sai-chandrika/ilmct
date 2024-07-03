package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.FilterDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;
import java.util.List;

public interface FilterRepository extends MongoRepository<FilterDoc,String> {

    List<FilterDoc> findByUserIdOrderByCreatedDateDesc(String id, Pageable pageable);

    long countByUserId(String id);

    FilterDoc findOneByIdAndUserId(String id, String id1);
}