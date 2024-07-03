package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.CityTimeZoneInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 03-11-2023
 */
public interface CityTimeZoneInfoRepo extends MongoRepository<CityTimeZoneInfo, String> {
    List<CityTimeZoneInfo> findByTimeZoneOrderByCreatedDateAsc(String timeZoneId);

    List<CityTimeZoneInfo> findByCountryCode(String countryCode);

    List<CityTimeZoneInfo> findByCountryCodeContainingIgnoreCase(String countryCode);
}
