package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.LocationDoc;

import java.util.Map;
import java.util.Set;


public interface CacheService {

    LocationDoc findLocationBySiteId(String siteId);

    Map<String, LocationDoc> findLocationBySiteIdIn(Set<String> set1);

    Map<String, Object>  getLocations(String countryCode, String stateCode, String city);

    Map<String, Object> getLocations(String countryCode, String stateCode);

    Map<String, Object> getLocations(String countryCode);

}
