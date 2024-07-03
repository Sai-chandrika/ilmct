package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.Log;
import com.inspirage.ilct.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;


import java.time.LocalDate;
import java.util.Optional;

public interface LoggerService {
    void saveLog(Log log, HttpServletRequest request);

    void saveLog(Log log, String userId);

    void saveLog(Log log);

    ApiResponse fetchLog(Optional<LocalDate> fromDate, Optional<LocalDate> toDate, Integer index, Integer pageNumber, Optional<String> search);

}
