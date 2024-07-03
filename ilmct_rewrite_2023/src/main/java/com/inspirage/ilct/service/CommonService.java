package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 08-11-2023
 */
public interface CommonService {
    ApiResponse getAllTimeZones();

    ApiResponse getCityAndTimeZone(String countryCode);

    ApiResponse getCountries();

    ApiResponse getLanguages();

    ApiResponse getLocationCountry();

    ApiResponse getAppConstants();
}
