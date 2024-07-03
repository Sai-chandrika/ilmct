package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;

public interface OtmService {
    ApiResponse getCurrentRoute(String loadId);
}