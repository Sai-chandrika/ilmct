package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.LocationDoc;
import com.inspirage.ilct.repo.LocationDocRepository;
import com.inspirage.ilct.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Set;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private LocationDocRepository locationDocRepository;

    @Cacheable(value = "locations")
    @Override
    public LocationDoc findLocationBySiteId(String siteId) {
        try {
            return locationDocRepository.findOneBySiteId(siteId).orElse(new LocationDoc());
        } catch (IncorrectResultSizeDataAccessException e) {
            return locationDocRepository.findBySiteId(siteId).get(0);
        }
    }

    @Override
    public Map<String, LocationDoc> findLocationBySiteIdIn(Set<String> siteIds) {

        return locationDocRepository.findBySiteIdIn(siteIds)
                .stream().filter(l -> !ObjectUtils.isEmpty(l.getSiteId())  && !ObjectUtils.isEmpty(l.getSiteName())).collect(Collectors.toMap(LocationDoc::getSiteId, Function.identity(), (existing, replacement) -> existing));
    }

    @Override
    public Map<String, Object> getLocations(String countryCode, String stateCode, String city) {
        return locationDocRepository.findSiteIdAndSiteNameByCountryCodeAndProvinceAndCity(countryCode, stateCode, city).stream().collect(Collectors.toMap(LocationDoc::getSiteId, LocationDoc::getSiteName));
    }

    @Override
    public Map<String, Object> getLocations(String countryCode) {
        return locationDocRepository.findSiteIdAndSiteNameByCountryCode(countryCode).stream()
                .collect(Collectors.toMap(LocationDoc::getSiteId, LocationDoc::getSiteName));
    }

    @Override
    public Map<String, Object> getLocations(String countryCode, String stateCode) {
        return locationDocRepository.findSiteIdAndSiteNameByCountryCodeAndProvince(countryCode, stateCode).stream()
                .collect(Collectors.toMap(LocationDoc::getSiteId, LocationDoc::getSiteName));
    }

}
