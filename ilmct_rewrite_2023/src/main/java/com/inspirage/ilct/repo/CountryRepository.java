package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.CountryDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends MongoRepository<CountryDoc,String> {
    Optional<CountryDoc> findUniqueByIso2CodeOrIso3Code(String countryCode, String countryCode1);
}
