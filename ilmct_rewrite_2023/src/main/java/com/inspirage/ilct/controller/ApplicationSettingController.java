package com.inspirage.ilct.controller;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.dto.here.weather.Alert;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.service.AlertService;
import com.inspirage.ilct.service.ApplicationSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 16-11-2023
 */
@RestController
@RequestMapping("/v3/application-settings/")
public class ApplicationSettingController {

    @Autowired
    ApplicationSettingsService applicationSettingsService;
    @Autowired
    AlertService alertService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("save-application-settings")
    public ResponseEntity<ApiResponse> saveApplicationSettings(@RequestBody ApplicationSettings applicationSettings, HttpServletRequest request){
        return ResponseEntity.ok(applicationSettingsService.saveApplicationSettings(applicationSettings,request));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("update-application-settings")
    public ResponseEntity<ApiResponse> updateApplicationSettings(@RequestBody ApplicationSettings applicationSettings, HttpServletRequest request) {
        ApiResponse response = applicationSettingsService.updateApplicationSettings(applicationSettings,request);
        if(response.getStatus().equals(HttpStatus.BAD_REQUEST))
        {
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }else{
            return ResponseEntity.ok(response);
        }
    }
    @GetMapping("get-application-settings")
    public ApplicationSettings getApplicationSettings(){
        return applicationSettingsService.getApplicationSettings();
    }

    @PostMapping("save-alerts")
    public ResponseEntity<ApiResponse> saveAlerts(@RequestBody List<AlertsBean> alertsBean, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        ApiResponse response = alertService.saveAlerts(alertsBean,request);
        if(response.getStatus().equals(HttpStatus.BAD_REQUEST))
        {
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }else{
            return ResponseEntity.ok(response);
        }
    }
}
