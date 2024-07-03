package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.documents.OrderV2Doc;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.LoadDTO;
import com.inspirage.ilct.dto.ShipmentTransmissionDTO;
import com.inspirage.ilct.dto.bean.AddEventBean;
import com.inspirage.ilct.dto.shipmentstatus.ShipmentStatusDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface OtmServiceV3 {

    ApiResponse saveShipmentXml(ShipmentTransmissionDTO transmissionDto, HttpServletRequest request);

    ApiResponse saveShipmentV2Mapping(User loginUser, LoadDTO loadDTO);


    ApiResponse addDocumentsToShipment(List<MultipartFile> files, String loadId, HttpServletRequest request, String comment) throws IOException;

    ApiResponse saveShipmentStatus(ShipmentStatusDto shipmentStatusDto, HttpServletRequest request);

    ShipmentV2 calculateLeadTimeValues(ShipmentV2 shipment, ApplicationSettings applicationSettings, Boolean isOrder, Integer leadTime, OrderV2Doc orderV2);

    ApiResponse addEventToShipment(AddEventBean addEventBean, HttpServletRequest request);

    ApiResponse addToWatchList(String loadId, HttpServletRequest request);

    ApiResponse closeShipment(String loadId, String userId);
}
