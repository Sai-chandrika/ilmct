package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.MonitorBean;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
public interface MonitorService {
    ApiResponse saveMonitor(MonitorBean monitorBean, HttpServletRequest request);

    ApiResponse updateMonitor(MonitorBean monitorBean, HttpServletRequest request);

    ApiResponse deleteMonitor(String monitorId, HttpServletRequest request);

    ApiResponse getMonitorsBasedOnUser(String userId, HttpServletRequest request);

    ApiResponse getAllMonitors(Integer pageIndex, Integer numberOfRecord, HttpServletRequest request);
}
