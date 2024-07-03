package com.inspirage.ilct.controller;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.request.DriverRestTimeBean;
import com.inspirage.ilct.service.DriverRestTimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
@RestController
@RequestMapping("/v2/driver-rest-time/")
public class DriverRestTimeController {
    @Autowired
    DriverRestTimeService driverRestTimeService;

    @PostMapping(value = "saveDriverRestTiming")
    public ResponseEntity<ApiResponse> saveDriverRestTiming(@Valid @RequestBody DriverRestTimeBean timing) {
        ApiResponse response = driverRestTimeService.saveDriveAndRestTimings(timing);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "driverRestTimings")
    public ResponseEntity<ApiResponse> driverRestTimings() {
        ApiResponse response = driverRestTimeService.findAllDriverAndRestTimings();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "deleteAllDriverRestTimings")
    public ResponseEntity<ApiResponse> deleteAllDriverRestTimings() {
        ApiResponse response = driverRestTimeService.deleteAllDriveAndRestTimings();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping(value = "deleteDriverRestTiming/{id}")
    public ResponseEntity<ApiResponse> deleteDriverRestTimingById(@PathVariable String id) {
        ApiResponse response = driverRestTimeService.deleteDriveAndRestTimingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "driverCountries")
    public ResponseEntity<ApiResponse> driverCountries() {
        ApiResponse response = driverRestTimeService.findDriverCountries();
        return ResponseEntity.ok(response);
    }

}
