package com.inspirage.ilct.controller;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.exceptions.PageRedirectionException;
import com.inspirage.ilct.service.LoggerService;
import com.inspirage.ilct.service.OTMGetServiceV3;
import com.inspirage.ilct.util.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v3/otm-get/")
@CrossOrigin
public class OTMGetControllerV3 {
    @Autowired
    OTMGetServiceV3 otmGetServiceV3;

    @Autowired
    LoggerService loggerService;
    @Autowired
    TokenUtilService tokenUtilService;
    
    @GetMapping(value = "shipments")
    public ResponseEntity<ApiResponse> getShipmentInfo(
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "loadId", required = false) String loadId,
            @RequestParam(value = "carrierId", required = false) String carrierId,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "hazardous", required = false) Boolean hazardous,
            @RequestParam(value = "isPagination", required = false) Boolean isPagination,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = 100
                    + "") int numberOfRecord,
            HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        ApiResponse response = otmGetServiceV3.getShipmentTrackingBeans(isPagination, pageIndex, numberOfRecord, containerId, loadId, carrierId,
                customerId, status, hazardous, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "get-leg-shipments-v3")
    public ResponseEntity<ApiResponse> getLegShipmentsV3(
            @RequestParam(value = "domainName", required = false) String domainName,
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "loadId", required = false) String loadId,
            @RequestParam(value = "carrierId", required = false) String carrierId,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "hazardous", required = false) Boolean hazardous,
            @RequestParam(value = "globalId", required = false) String globalId,
            @RequestParam(value = "deleveryOrderNo", required = false) String deleveryOrderNo,
            @RequestParam(value = "fromPage", required = false) String fromPage,
            @RequestParam(value = "bookingRefNo", required = false) String bookingRefNo,
            @RequestParam(value = "isPagination", required = false) Boolean isPagination,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE
                    + "") int numberOfRecord,
            HttpServletRequest request) throws ExecutionException, InterruptedException {

        ApiResponse response = otmGetServiceV3.getLegShipmentsV3(isPagination, pageIndex, numberOfRecord, containerId, loadId,
                carrierId, customerId, status, hazardous, request, globalId, deleveryOrderNo, bookingRefNo, domainName, fromPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "getShipmentStops")
    public ResponseEntity<ApiResponse> getShipmentStops(@RequestParam(value = "loadId") String loadId, @RequestParam(value = "isItemVisibility") Boolean isItemVisibility, HttpServletRequest request) {
        return ResponseEntity.ok(otmGetServiceV3.getStops(loadId, isItemVisibility, request));
    }


    @GetMapping(value = "getEventsForShipment")
    public ResponseEntity<ApiResponse> getShipmentEvents(@RequestParam(value = "loadId") String loadId,
                                                         @RequestParam(value = "stopSequenceNo", required = false) Integer seqNo, HttpServletRequest request,
                                                         @RequestParam(value = "isList", required = false, defaultValue = "false") boolean isList,
                                                         @RequestParam(value = "isFromParcelVisibility", required = false, defaultValue = "false") boolean isFromParcelVisibility) throws ParseException, InvalidUserTokenException {
        return ResponseEntity.ok(otmGetServiceV3.getEvents(loadId, seqNo, request, isList, isFromParcelVisibility));
    }

    @GetMapping(value = "get-orders")
    public ResponseEntity<ApiResponse> getOrders(@RequestParam(value = "loadId") String loadId, HttpServletRequest request) {
        ApiResponse response = otmGetServiceV3.getOrdersV3(loadId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "searchShipment")
    public ResponseEntity<ApiResponse> searchShipment(
            @RequestParam(value = "domainName", required = false) String domainName,
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "loadId", required = false) String loadId,
            @RequestParam(value = "carrierName", required = false) String carrierName,
            @RequestParam(value = "hazardous", required = false) Boolean hazardous,
            @RequestParam(value = "redirectPage", required = false) String redirectPage,
            @RequestParam(value = "globalId", required = false) String globalId,
            @RequestParam(value = "bookingRef", required = false) String bookingRefNum,
            @RequestParam(value = "deliveryNo", required = false) String deliveryNum,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "shipper", required = false) String shipper,
            @RequestParam(value = "originCity", required = false) String originCity,
            @RequestParam(value = "originPort", required = false) String originPort,
            @RequestParam(value = "destinationPort", required = false) String destinationPort,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "shipTo", required = false) String shipTo,
            @RequestParam(value = "shipFrom", required = false) String shipFrom,
            @RequestParam(value = "truckNumber", required = false) String truckNumber,
            @RequestParam(value = "trailerNumber", required = false) String trailerNumber,
            @RequestParam(value = "linerNumber", required = false) String linerNumber,
            @RequestParam(value = "LogisticsContact", required = false) String logisticsContact,
            @RequestParam(value = "sourceCountryCode", required = false) String sourceCountryCode,
            @RequestParam(value = "destinationCountryCode", required = false) String destinationCountryCode,
            @RequestParam(value = "atdFrom", required = false) String atdFrom,
            @RequestParam(value = "atdTo", required = false) String atdTo,
            @RequestParam(value = "ataFrom", required = false) String ataFrom,
            @RequestParam(value = "ataTo", required = false) String ataTo,
            @RequestParam(value = "etdFrom", required = false) String etdFrom,
            @RequestParam(value = "etdTo", required = false) String etdTo,
            @RequestParam(value = "etaFrom", required = false) String etaFrom,
            @RequestParam(value = "etaTo", required = false) String etaTo,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "lastMileDelivery", required = false, defaultValue = "0") boolean lastMileDelivery,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE
                    + "") int numberOfRecord,
            HttpServletRequest request) throws ApplicationSettingsNotFoundException, PageRedirectionException {
        ApiResponse response = otmGetServiceV3.searchContainerAndShipments(domainName, containerId, loadId, carrierName, hazardous, redirectPage, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, pageIndex, numberOfRecord, lastMileDelivery, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "intranit")
    public ResponseEntity<ApiResponse> getIntransitShipmentInfo(
            @RequestParam(value = "domainName", required = false) String domainName,
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "loadId", required = false) String loadId,
            @RequestParam(value = "carrierId", required = false) String carrierId,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "hazardous", required = false) Boolean hazardous,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE
                    + "") int numberOfRecord,
            @RequestParam(value = "globalId", required = false) String globalId,
            @RequestParam(value = "deleveryOrderNo", required = false) String deleveryOrderNo,
            @RequestParam(value = "bookingRefNo", required = false) String bookingRefNo,
            @RequestParam(value = "isPagination", required = false) Boolean isPagination,
            @RequestParam(value = "materialId", required = false) String materialId, HttpServletRequest request,
            Authentication authentication) throws ApplicationSettingsNotFoundException {
        ApiResponse response = otmGetServiceV3.getIntransitShipmentsV3(pageIndex, numberOfRecord, materialId, request,
                authentication, globalId, deleveryOrderNo, bookingRefNo, domainName, containerId, loadId, carrierId,
                customerId, hazardous, isPagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "logging")
    public ApiResponse fetchLoggers(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> fromDate,
                                    @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<java.time.LocalDate> toDate,
                                    @RequestParam("search") Optional<String> search, @RequestParam(value = "index", required = false) Integer index,
                                    @RequestParam(value = "numberOfRecords", required = false) Integer numberOfRecords) {
        return loggerService.fetchLog(toDate, fromDate, index, numberOfRecords, search);
    }

    @GetMapping(value = "completedShipments")
    public ResponseEntity<ApiResponse> getCompletedClosedShipments(
            @RequestParam(value = "domainName", required = false) String domainName,
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "loadId", required = false) String loadId,
            @RequestParam(value = "carrierId", required = false) String carrierId,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "hazardous", required = false) Boolean hazardous,

            @RequestParam(value = "globalId", required = false) String globalId,
            @RequestParam(value = "deleveryOrderNo", required = false) String deleveryOrderNo,
            @RequestParam(value = "bookingRefNo", required = false) String bookingRefNo,
            @RequestParam(value = "days", required = false, defaultValue = "0") int days,

            @RequestParam(value = "tab", required = false, defaultValue = "0") int tab,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE
                    + "") int numberOfRecord,
            HttpServletRequest request) {
        ApiResponse response = otmGetServiceV3.getCompletedShipments(tab, pageIndex, numberOfRecord, containerId,
                loadId, carrierId, customerId, hazardous, request, globalId, deleveryOrderNo, bookingRefNo, days,
                domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "getShipmentStatusCount")
    public ResponseEntity<ApiResponse> getShipmentStatusCounts(HttpServletRequest request) {
        return ResponseEntity.ok(otmGetServiceV3.getStatusCount(request));
    }

}