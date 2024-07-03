package com.inspirage.ilct.controller;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.service.MonitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kpi/monitors/")
public class KPIController {
    @Autowired
    MonitorService monitorService;

    @GetMapping("/get-monitor-by-user")
    public ApiResponse getMonitorsBasedOnUser(@RequestParam String userId, HttpServletRequest request){
        return new ApiResponse(HttpStatus.OK,monitorService.getMonitorsBasedOnUser(userId,request));
    }
}
