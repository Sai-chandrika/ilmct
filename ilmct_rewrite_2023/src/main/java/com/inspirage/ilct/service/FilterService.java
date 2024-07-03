package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.FilterBean;
import jakarta.servlet.http.HttpServletRequest;

public interface FilterService {
    ApiResponse saveFilter(FilterBean filter, HttpServletRequest request);
    ApiResponse editFiler(FilterBean filter, HttpServletRequest request);
    ApiResponse getFilters(int pageIndex, int numberOfRecord, HttpServletRequest request);
    ApiResponse deleteFilter(String filterId, HttpServletRequest request);
}
