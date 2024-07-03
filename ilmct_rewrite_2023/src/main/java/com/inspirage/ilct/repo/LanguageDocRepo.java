package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.LanguageDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 16-11-2023
 */
@Repository
public interface LanguageDocRepo extends MongoRepository<LanguageDoc,String> {
}
