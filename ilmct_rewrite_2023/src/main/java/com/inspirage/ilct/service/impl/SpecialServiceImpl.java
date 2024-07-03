package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.documents.SpecialServiceDocument;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.service.ApplicationSettingsService;
import com.inspirage.ilct.service.SpecialService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 06-12-2023
 */
@Service
public class SpecialServiceImpl implements SpecialService {
    @Autowired
    ApplicationSettingsService applicationSettingsService;


    @Override
    public ApiResponse saveSpecialServices(List<SpecialServicesBean> servicesBeans, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        for (SpecialServicesBean servicesBean : servicesBeans) {
            String validate = validateSpecialServices(servicesBean);
            if (!Utility.isEmpty(validate)) {
                return new ApiResponse.ApiResponseBuilder().setStatus(HttpStatus.BAD_REQUEST).setMessage(validate).build();
            }
        }
        ApplicationSettings applicationSettings = applicationSettingsService.getApplicationSetting();
        List<SpecialServiceDocument> servicesList = new ArrayList<>();
        for (SpecialServicesBean servicesBean : servicesBeans) {
            servicesList.add(parseSpecialServicesBean(servicesBean));
        }
        applicationSettings.setSpecialServices(new ArrayList<>());
        for (SpecialServiceDocument service : servicesList) {
            applicationSettings.getSpecialServices().add(parseSpecialServices(service));
        }
        applicationSettingsService.saveApplicationSettings(applicationSettings, request);
        return new ApiResponse(HttpStatus.OK.value(), "SpecialServices Saved Successfully",servicesList);
    }


    public SpecialServiceDocument parseSpecialServicesBean(SpecialServicesBean servicesBean) {
        SpecialServiceDocument specialServices = new SpecialServiceDocument();
        specialServices.setSpecialServiceName(servicesBean.getSpecialServiceName());
        specialServices.setSpecialServiceIconUpload(servicesBean.getSpecialServiceIconUpload());
        return specialServices;
    }

    public  SpecialServicesBean parseSpecialServices(SpecialServiceDocument services) {
        SpecialServicesBean servicesBean = new SpecialServicesBean();
        servicesBean.setSpecialServiceName(services.getSpecialServiceName());
        servicesBean.setSpecialServiceIconUpload(services.getSpecialServiceIconUpload());
        servicesBean.setId(services.getId());
        servicesBean.setIsActive(services.getIsActive());
        return servicesBean;
    }

    private String validateSpecialServices(SpecialServicesBean servicesBean) {
        if (Utility.isEmpty(servicesBean.getSpecialServiceName())) {
            return "Please provide valid service name";
        }
        if (Utility.isEmpty(servicesBean.getSpecialServiceIconUpload())) {
            return "Please provide valid service icon ";
        }
        return null;
    }

}
