package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import org.springframework.security.core.Authentication;

public interface DataPurgingService {

    ApiResponse checkDataCanBeCleared(Authentication authentication);

    ApiResponse clearData();
}
