package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.CountryDoc;
import com.inspirage.ilct.documents.DriverRestTimeDoc;
import com.inspirage.ilct.documents.RuleDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRestTimeRepository extends MongoRepository<DriverRestTimeDoc,String> {
    DriverRestTimeDoc findByCountryAndStatus(CountryDoc countryDoc, boolean b);

    DriverRestTimeDoc findByCountry(CountryDoc country);

    List<DriverRestTimeDoc> findAllByStatus(boolean b);

    Optional<DriverRestTimeDoc> findOneBycountryName(String country);

    Optional<DriverRestTimeDoc> findByCountryId(String country);

    DriverRestTimeDoc findByIdAndStatus(String id, boolean b);
}
