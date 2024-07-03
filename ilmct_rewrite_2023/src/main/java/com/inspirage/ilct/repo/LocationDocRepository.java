package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.LocationDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LocationDocRepository extends MongoRepository<LocationDoc,String> {
    LocationDoc findLocationBySiteId(String locationID);

    Optional<LocationDoc> findOneBySiteId(String siteId);

    List<LocationDoc> findBySiteId(String siteId);

    List<LocationDoc> findBySiteIdIn(Set<String> siteIds);
    List<LocationDoc> findBySiteIdIn(List<String> siteIds);


    List<LocationDoc> findByCity(String trim);

    List<LocationDoc> findByProvince(String trim);

    List<LocationDoc> findByCountryCode(String trim);

    @Query(value="{countryCode : ?0, province : ?1, city : ?2}", fields="{siteId : 1, siteName : 2, _id : 0}")
    List<LocationDoc> findSiteIdAndSiteNameByCountryCodeAndProvinceAndCity(String countryCode, String provinceCode, String city);

    @Query(value="{countryCode : ?0}", fields="{siteId : 1, siteName : 2, _id : 0}")
    List<LocationDoc> findSiteIdAndSiteNameByCountryCode(String countryCode);
    @Query(value="{countryCode : ?0, province : ?1}", fields="{siteId : 1, siteName : 2, _id : 0}")
    List<LocationDoc> findSiteIdAndSiteNameByCountryCodeAndProvince(String countryCode, String provinceCode);

}
