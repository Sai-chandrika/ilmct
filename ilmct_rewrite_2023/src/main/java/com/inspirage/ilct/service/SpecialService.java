package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 06-12-2023
 */
public interface SpecialService {
    ApiResponse saveSpecialServices(List<SpecialServicesBean> servicesBeans, HttpServletRequest request) throws ApplicationSettingsNotFoundException, ApplicationSettingsNotFoundException;
}
