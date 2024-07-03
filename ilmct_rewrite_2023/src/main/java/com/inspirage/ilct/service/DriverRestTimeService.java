package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.request.DriverRestTimeBean;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
public interface DriverRestTimeService {
    ApiResponse saveDriveAndRestTimings(DriverRestTimeBean timing);


    ApiResponse findAllDriverAndRestTimings();

    ApiResponse deleteDriveAndRestTimingById(String id);

    ApiResponse deleteAllDriveAndRestTimings();

    ApiResponse findDriverCountries();
}
