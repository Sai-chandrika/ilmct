package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.Alerts;
import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.service.AlertService;
import com.inspirage.ilct.service.ApplicationSettingsService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 05-12-2023
 */
@Service
public class AlertServiceImpl implements AlertService {
    @Autowired
    ApplicationSettingsService applicationSettingsService;
    @Override
    public ApiResponse saveAlerts(List<AlertsBean> alertsBeans, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        for (AlertsBean alertsBean : alertsBeans) {
            String validate = validateAlerts(alertsBean);
            if(!Utility.isEmpty(validate)){
                return new ApiResponse.ApiResponseBuilder().setStatus(HttpStatus.BAD_REQUEST).setMessage(validate).build();
            }
        }
        ApplicationSettings applicationSettings = applicationSettingsService.getApplicationSetting();
        List<Alerts> alertList = alertsBeans.stream().map(this::parseAlertsBean).toList();
        applicationSettings.setAlerts(new ArrayList<>());
        for (Alerts alert : alertList) {
            alert.setId(UUID.randomUUID().toString().replaceAll("_", "").substring(0, 25));
            applicationSettings.getAlerts().add(parseAlerts(alert));
        }
        applicationSettingsService.saveApplicationSettings(applicationSettings,request);
        return new ApiResponse.ApiResponseBuilder().setData(alertList).setStatus(HttpStatus.OK).setMessage("alert save successfully").build();
    }

//    public  ApiResponse saveAlerts(List<AlertsBean> alertBeans, HttpServletRequest request){
//        Alerts alert=new Alerts();
//        List<Alerts> alertsList=new ArrayList<>();
//        for(AlertsBean alertsBean:alertBeans){
//            String validate=validateAlerts(alertsBean);
//            if(!Utility.isEmpty(validate)){
//                return new ApiResponse(HttpStatus.BAD_REQUEST, validate);
//            }
//            alert.setAlertName(alertsBean.getAlertName());
//            alert.setAlertIconUpload(alertsBean.getAlertIconUpload());
//            alertsList.add(alert);
//        }
//        return new ApiResponse(HttpStatus.OK.value(), "alert save successfully", alertsList.stream().map(this::parseAlerts).toList());
//    }

    public  Alerts parseAlertsBean(AlertsBean alertsBean){
        Alerts alerts = new Alerts();
        alerts.setAlertName(alertsBean.getAlertName());
        alerts.setAlertIconUpload(alertsBean.getAlertIconUpload());
        return alerts;
    }

    public AlertsBean parseAlerts(Alerts alerts){
        AlertsBean alertsBean = new AlertsBean();
        alertsBean.setAlertName(alerts.getAlertName());
        alertsBean.setAlertIconUpload(alerts.getAlertIconUpload());
        alertsBean.setId(alerts.getId());
        alertsBean.setIsActive(alerts.getIsActive());
        return alertsBean;
    }


    private String validateAlerts(AlertsBean alertsBean) {
        if(Utility.isEmpty(alertsBean.getAlertName())){
            return "Please provide valid alert name";
        }
        if(Utility.isEmpty(alertsBean.getAlertIconUpload())){
            return "Please provide valid alert icon ";
        }
        return null;
    }
}
