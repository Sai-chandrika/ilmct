package com.inspirage.ilct.service.impl;
import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.ApplicationStatus;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.repo.ApplicationSettingsRepository;
import com.inspirage.ilct.service.ApplicationSettingsService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ApplicationSettingsServiceImpl implements ApplicationSettingsService {

    @Autowired
    private ApplicationSettingsRepository settingsRepository;


    @Override
    public ApiResponse saveApplicationSettings(ApplicationSettings applicationSettings, HttpServletRequest request) {
        List<ApplicationSettings> settingsList = settingsRepository.findAll();
        if (settingsList.isEmpty()) {
            if (!Utility.isEmpty(applicationSettings.getAutoSchedularTime()) && !CronExpression.isValidExpression(applicationSettings.getAutoSchedularTime())) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Incorrect scheduler time expression");
            }
            if (!Utility.isEmpty(applicationSettings.getPurgeDataSchedularTime()) && !CronExpression.isValidExpression(applicationSettings.getPurgeDataSchedularTime())) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Incorrect purge scheduler time expression");
            }
            applicationSettings = settingsRepository.save(applicationSettings);
        } else
            return updateApplicationSettings(applicationSettings,request);
        return new ApiResponse(HttpStatus.OK.value(), applicationSettings);
    }

    @Override
    public ApiResponse updateApplicationSettings(ApplicationSettings applicationSettings, HttpServletRequest request) {
        if (applicationSettings.getId() == null)
            return new ApiResponse.ApiResponseBuilder().setMessage("Application Settings Id Not provided").setStatus(HttpStatus.BAD_REQUEST).build();
        Optional<ApplicationSettings> dbApplicationSettingsOptional = settingsRepository.findById(applicationSettings.getId());
        if (dbApplicationSettingsOptional.isPresent() && dbApplicationSettingsOptional.get().getIsActive()) {
            if (!Utility.isEmpty(applicationSettings.getAutoSchedularTime()) && !CronExpression.isValidExpression(applicationSettings.getAutoSchedularTime())) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Incorrect scheduler time expression");
            }
            if (!Utility.isEmpty(applicationSettings.getPurgeDataSchedularTime()) && !CronExpression.isValidExpression(applicationSettings.getPurgeDataSchedularTime())) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Incorrect purge scheduler time expression");
            }
            applicationSettings = settingsRepository.save(applicationSettings);
            return new ApiResponse(HttpStatus.OK.value(),"application setting updated successfully",applicationSettings);
        } else {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"No Application Settings Found with given Id");
        }
    }

    @Override
    public ApplicationSettings getApplicationSetting() throws ApplicationSettingsNotFoundException {
        List<ApplicationSettings> settingsList = settingsRepository.findAll();
        if (!settingsList.isEmpty()) {
            return settingsList.get(0);
        } else {
            throw new ApplicationSettingsNotFoundException("No application settings found");
        }
    }

    @Override
    public ApplicationSettings getApplicationSettings() {
        List<ApplicationSettings> list = settingsRepository.findAll();
        if (!list.isEmpty()) {
            ApplicationSettings applicationSettings = list.get(0);
            return applicationSettings;
        } else {
            ApplicationSettings settings = new ApplicationSettings();
            settings.setApplicationStatus(new ApplicationStatus());
            return  settings;
        }
    }
}
