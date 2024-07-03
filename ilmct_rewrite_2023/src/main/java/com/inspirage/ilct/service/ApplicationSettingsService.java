package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

public interface ApplicationSettingsService {

    ApiResponse saveApplicationSettings(ApplicationSettings applicationSettings, HttpServletRequest request);

    ApiResponse updateApplicationSettings(ApplicationSettings applicationSettings, HttpServletRequest request);

    ApplicationSettings getApplicationSetting() throws ApplicationSettingsNotFoundException;

    ApplicationSettings getApplicationSettings();
}
