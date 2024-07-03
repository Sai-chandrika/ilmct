package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.PageRedirectionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;


import java.util.concurrent.ExecutionException;

public interface OTMGetServiceV3 {

    ApiResponse getShipmentTrackingBeans(Boolean isPagination, int pageIndex, int numberOfRecord, String containerId, String loadId, String carrierId, String customerId, String status, Boolean hazardous, HttpServletRequest request) throws ApplicationSettingsNotFoundException;

    ApiResponse getLegShipmentsV3(Boolean isPagination, int pageIndex, int numberOfRecord, String containerId, String loadId, String carrierId, String customerId, String status, Boolean hazardous, HttpServletRequest request, String globalId, String deleveryOrderNo, String bookingRefNo, String domainName, String fromPage) throws ExecutionException, InterruptedException;

    ApiResponse getStops(String loadId, Boolean isItemVisibility, HttpServletRequest request);

    ApiResponse getEvents(String loadId, Integer seqNo, HttpServletRequest request, boolean isList, boolean isFromParcelVisibility);

    ApiResponse searchContainerAndShipments(String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String redirectPage, String globalId, String bookingRefNum, String deliveryNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String linerNumber, String logisticsContact, String sourceCountryCode, String destinationCountryCode, String atdFrom, String atdTo, String ataFrom, String ataTo, String etdFrom, String etdTo, String etaFrom, String etaTo, int pageIndex, int numberOfRecord, boolean lastMileDelivery,HttpServletRequest request) throws ApplicationSettingsNotFoundException, PageRedirectionException;

    ApiResponse getOrdersV3(String loadId, HttpServletRequest request);

    ApiResponse getIntransitShipmentsV3(int pageIndex, int numberOfRecord, String materialId, HttpServletRequest request, Authentication authentication, String globalId, String deleveryOrderNo, String bookingRefNo, String domainName, String containerId, String loadId, String carrierId, String customerId, Boolean hazardous, Boolean isPagination) throws ApplicationSettingsNotFoundException;

    ApiResponse getCompletedShipments(int tab, int pageIndex, int numberOfRecord, String containerId, String loadId, String carrierId, String customerId, Boolean hazardous, HttpServletRequest request, String globalId, String deleveryOrderNo, String bookingRefNo, int days, String domainName);

    ApiResponse getStatusCount(HttpServletRequest request);

}