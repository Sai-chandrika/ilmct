package com.inspirage.ilct.controller;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.MonitorBean;
import com.inspirage.ilct.service.MonitorService;
import com.inspirage.ilct.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 10-11-2023
 */
@RestController
@RequestMapping("/v3/monitors/")
@CrossOrigin
public class MonitorController {
    @Autowired
    MonitorService monitorService;

    @PostMapping("saveMonitor")
    public ResponseEntity<ApiResponse> saveMonitor(@RequestBody MonitorBean monitorBean, HttpServletRequest request){
        ApiResponse response = monitorService.saveMonitor(monitorBean,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("updateMonitor")
    public ResponseEntity<ApiResponse> updateMonitor(@RequestBody MonitorBean monitorBean, HttpServletRequest request)  {
        ApiResponse response = monitorService.updateMonitor(monitorBean,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get-all-monitors")
    public  ResponseEntity<ApiResponse> getAllMonitors(@RequestParam(required = false, defaultValue = "0") Integer pageIndex , @RequestParam(required = false,defaultValue = Constants.PAGE_SIZE + "") Integer numberOfRecords, HttpServletRequest request) {
        ApiResponse response =monitorService.getAllMonitors(pageIndex, numberOfRecords, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("deleteMonitor")
    public ResponseEntity<ApiResponse> deleteMonitor(@RequestParam String monitorId, HttpServletRequest request) {
        return ResponseEntity.ok(monitorService.deleteMonitor(monitorId,request));
    }
}
