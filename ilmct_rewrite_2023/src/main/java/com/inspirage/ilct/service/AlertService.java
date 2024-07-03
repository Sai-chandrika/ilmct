package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 05-12-2023
 */
public interface AlertService {
    ApiResponse saveAlerts(List<AlertsBean> alertsBeans, HttpServletRequest request) throws ApplicationSettingsNotFoundException;

}
