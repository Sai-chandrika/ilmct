package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.CityTimeZoneInfo;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.repo.CityTimeZoneInfoRepo;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.CityTimeZoneDtO;
import com.inspirage.ilct.repo.CityTimeZoneInfoRepo;
import com.inspirage.ilct.repo.CountryRepository;
import com.inspirage.ilct.repo.LanguageDocRepo;
import com.inspirage.ilct.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    LanguageDocRepo languageDocRepo;
    @Autowired
    CityTimeZoneInfoRepo cityTimeZoneInfoRepo;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ApiResponse getCityAndTimeZone(String countryCode) {
         List<CityTimeZoneInfo> cityTimeZoneInfo=cityTimeZoneInfoRepo.findByCountryCodeContainingIgnoreCase(countryCode);
        return new ApiResponse(HttpStatus.OK.value(), cityTimeZoneInfo.stream().map(this::cityTimeZoneDTO).toList());
    }

    @Override
    public ApiResponse getCountries() {
        List<CountryDoc> countryDocList=countryRepository.findAll(Sort.by("name"));
        List<CountryDoc> countryDocs=new ArrayList<>();
        for(CountryDoc countryDoc:countryDocList){
            if(countryDoc.getName()!=null){
                countryDocs.add(countryDoc);
            }
        }
        return new ApiResponse(HttpStatus.OK.value(), countryDocs);
    }




    @Override
    public ApiResponse getLanguages() {
        List<LanguageDoc> languageDocs=languageDocRepo.findAll();
        return new ApiResponse(HttpStatus.OK.value(), languageDocs);
    }

    @Autowired
    CityTimeZoneInfoRepo cityTimeZoneInfoRepository;
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getAllTimeZones() {
        List<String> timeZones = cityTimeZoneInfoRepository.findAll().stream().map(CityTimeZoneInfo::getTimeZone).distinct().collect(Collectors.toList());
        return new ApiResponse(HttpStatus.OK,"retreived all time zones",timeZones);
    }
    @Override
    public ApiResponse getLocationCountry() {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryCode").exists(true));
        List<String> locationDocList = mongoTemplate.query(LocationDoc.class)
                .distinct("countryCode")
                .matching(query)
                .as(String.class)
                .all();
        return new ApiResponse(HttpStatus.OK.value(),locationDocList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(a -> a, a -> a)));
    }


    @Override
    public ApiResponse getAppConstants() {
        Map<String, Object> map = new HashMap<>();
        map.put("TemperatureUnit", Arrays.stream(Units.TemperatureUnit.values()).collect(Collectors.toMap(Units.TemperatureUnit::name, Units.TemperatureUnit::getName)));
        map.put("DistanceUnit", Arrays.stream(Units.DistanceUnit.values()).collect(Collectors.toMap(Units.DistanceUnit::name, Units.DistanceUnit::getName)));
        return new ApiResponse(HttpStatus.OK, HttpStatus.OK.toString(), map);
    }

    private CityTimeZoneDtO cityTimeZoneDTO(CityTimeZoneInfo cityTimeZoneInfo) {
        CityTimeZoneDtO cityTimeZoneDtO = new CityTimeZoneDtO();
        cityTimeZoneDtO.setId(cityTimeZoneInfo.getId());
        cityTimeZoneDtO.setCountryCode(cityTimeZoneInfo.getCountryCode());
        cityTimeZoneDtO.setCityName(cityTimeZoneInfo.getCityName());
        cityTimeZoneDtO.setProvince(cityTimeZoneInfo.getProvince());
        cityTimeZoneDtO.setTimeZone(cityTimeZoneInfo.getTimeZone());
        try {
            cityTimeZoneDtO.setOffset(ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.of(cityTimeZoneDtO.getTimeZone())).getOffset().getId());
        } catch (Exception e) {

        }
        return cityTimeZoneDtO;
    }
}
