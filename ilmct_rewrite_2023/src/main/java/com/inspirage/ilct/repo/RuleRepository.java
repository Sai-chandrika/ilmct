package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.RuleDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RuleRepository extends MongoRepository<RuleDoc,String>{

    Optional<RuleDoc> findOneByUserId(String userId);

    Optional<RuleDoc> findOneByUserIdIgnoreCase(String userId);
}
