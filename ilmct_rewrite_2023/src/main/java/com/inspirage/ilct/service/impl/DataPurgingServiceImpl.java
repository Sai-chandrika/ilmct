package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.ClearDataBean;
import com.inspirage.ilct.enums.StatusEnum;
import com.inspirage.ilct.repo.*;
import com.inspirage.ilct.service.DataPurgingService;
import com.inspirage.ilct.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.inspirage.ilct.util.Constants.*;

@Service
public class DataPurgingServiceImpl implements DataPurgingService {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Autowired
    ShipmentV2Repository shipmentV2Repository;

    @Autowired
    ShipmentStatusRepository shipmentStatusRepository;

    @Autowired
    ShipmentTransmissionLogRepository shipmentTransmissionLogRepository;

    @Autowired
    FlightShipmentStatusRepository flightShipmentStatusRepository;

    @Autowired
    ApplicationSettingsRepository applicationSettingsRepository;

    @Override
    public ApiResponse checkDataCanBeCleared(Authentication authentication) {
        logger.info("checkDataCanBeCleared");
        ApplicationSettings applicationSettings = applicationSettingsRepository.findAll().get(0);
        ClearDataBean clearDataBean = new ClearDataBean();
        try {
            User user = ((LoginUser) authentication.getDetails()).getUser();
            LocalDateTime now = LocalDateTime.now();

            //Shipment status
            LocalDateTime shipmentStatusClearDate = now.minusDays(applicationSettings.getPurgeShipmentStatus());

            clearDataBean.setTruckStatusClearTill(DateUtil.formatDate(shipmentStatusClearDate, DateUtil.dateTimeFormatter1));
            List<String> modes = new ArrayList<>();
            modes.add(TM_TL);
            modes.add(TM_LTL);
            modes.add(TM_TRUCK);
            modes.add(GROUP_AGE);
            modes.add(INTER_MODEL);
            List<ShipmentV2> shipmentV2List = shipmentV2Repository.findLoadIdByModeInAndStatusInAndCreatedDateLessThan(
                    modes,
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    shipmentStatusClearDate);

            //avoid multiple db hit's

            /*clearDataBean.setNumberOfTruckStatus(
                    shipmentV2List
                            .stream().map(s -> shipmentStatusRepository.countByLoadId(s.getLoadID())).mapToLong(Long::longValue).sum());*/

            List<String> loadIds = shipmentV2List.stream().map(ShipmentV2::getLoadID).toList();
            Map<String,Long> shipmentStatusCounts = shipmentStatusRepository.countByLoadIdIn(loadIds).stream().collect(Collectors.groupingBy(ShipmentStatusDoc::getLoadId, Collectors.counting()));
            clearDataBean.setNumberOfTruckStatus(shipmentV2List.stream()
                    .map(ShipmentV2::getLoadID).filter(shipmentStatusCounts::containsKey).mapToLong(shipmentStatusCounts::get).sum());


            //Shipment transmision logs
            LocalDateTime transmissionLogClearDate = now.minusDays(applicationSettings.getPurgeShipmentTransmissionLog());
            clearDataBean.setTransmissionLogClearTill(DateUtil.formatDate(transmissionLogClearDate, DateUtil.dateTimeFormatter1));
            clearDataBean.setNumberOfTransmissionLog(shipmentTransmissionLogRepository.countByCreatedDateLessThan(transmissionLogClearDate));


            //Shipments
            LocalDateTime shipmentClearDate = now.minusDays(applicationSettings.getPurgeShipments());

            clearDataBean.setShipmentClearTill(DateUtil.formatDate(shipmentClearDate, DateUtil.dateTimeFormatter1));
            clearDataBean.setNumberOfShipmentClear(shipmentV2Repository.countByStatusInAndCreatedDateLessThan(
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    shipmentClearDate
            ));


            //Flight status
            LocalDateTime flightStatusClearDate = now.minusDays(applicationSettings.getPurgeFlightShipmentStatus());

            clearDataBean.setFlightStatusClearTill(DateUtil.formatDate(flightStatusClearDate, DateUtil.dateTimeFormatter1));

            //Avoid multiple db hit's

/*            clearDataBean.setNumberOfFlightStatus(
                    shipmentV2Repository.findLoadIdByModeAndStatusInAndCreatedDateLessThan(
                            TM_AIR,
                            Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                            flightStatusClearDate
                    ).stream().map(s -> flightShipmentStatusRepository.countByLoadId(s.getLoadID())).collect(Collectors.summingLong(Long::longValue)));*/

            List<ShipmentV2> shipmentV2s = shipmentV2Repository.findLoadIdByModeAndStatusInAndCreatedDateLessThan(
                    TM_AIR,
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    flightStatusClearDate
            );

            List<String> shipmentV2loadIds = shipmentV2s.stream().map(ShipmentV2::getLoadID).toList();
            Map<String,Long> flightShipmentStatusDocCounts = flightShipmentStatusRepository.countByLoadIdIn(shipmentV2loadIds);
            clearDataBean.setNumberOfFlightStatus(shipmentV2s.stream().
                    map(ShipmentV2::getLoadID).
                    filter(flightShipmentStatusDocCounts::containsKey)
                    .mapToLong(flightShipmentStatusDocCounts::get).sum());

            return new ApiResponse(HttpStatus.OK, "Success", clearDataBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ApiResponse clearData() {
        logger.info("clearData");
        try {
            ApplicationSettings applicationSettings = applicationSettingsRepository.findAll().get(0);
            LocalDateTime now = LocalDateTime.now();
            List<String> modes = new ArrayList<>();
            modes.add(TM_TL);
            modes.add(TM_LTL);
            modes.add(EXPRESS);
            modes.add(TM_TRUCK);
            modes.add(GROUP_AGE);
            modes.add(INTER_MODEL);
            //Shipment status
            LocalDateTime shipmentStatusClearDate = now.minusDays(applicationSettings.getPurgeShipmentStatus());
            final List<String> ids = new ArrayList<>();
            shipmentV2Repository.findLoadIdByModeInAndStatusInAndCreatedDateLessThan(
                    modes,
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    shipmentStatusClearDate
            ).forEach(s -> {
                ids.add(s.getLoadID());
                shipmentStatusRepository.deleteByLoadId(s.getLoadID());
            });


            //Shipment transmision logs
            LocalDateTime transmissionLogClearDate = now.minusDays(applicationSettings.getPurgeShipmentTransmissionLog());
            shipmentTransmissionLogRepository.deleteByCreatedDateLessThan(transmissionLogClearDate);


            //Shipments
			LocalDateTime shipmentClearDate = now.minusDays(applicationSettings.getPurgeShipments());
            final List<String> ids2 = new ArrayList<>();
            shipmentV2Repository.findLoadIdByStatusInAndCreatedDateLessThan(
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    shipmentClearDate
            ).forEach(s -> {
                ids2.add(s.getLoadID());
                shipmentStatusRepository.deleteByLoadId(s.getLoadID());
                flightShipmentStatusRepository.deleteByLoadId(s.getLoadID());
                shipmentV2Repository.delete(s);
            });


            //Flight status
            LocalDateTime flightStatusClearDate = now.minusDays(applicationSettings.getPurgeFlightShipmentStatus());
            final List<String> ids3 = new ArrayList<>();
            shipmentV2Repository.findLoadIdByModeAndStatusInAndCreatedDateLessThan(
                    TM_AIR,
                    Arrays.asList(StatusEnum.DELIVERED, StatusEnum.COMPLETED, StatusEnum.CLOSED),
                    flightStatusClearDate
            ).forEach(s -> {
                ids3.add(s.getLoadID());
                flightShipmentStatusRepository.deleteByLoadId(s.getLoadID());
            });


            logger.info("Shipment Status Deleted for : " + Arrays.toString(ids.toArray()));
            logger.info("Shipment Transmission Log Deleted before " + transmissionLogClearDate);
            logger.info("Shipment Deleted : " + Arrays.toString(ids2.toArray()));
            logger.info("Flight Status Deleted for : " + Arrays.toString(ids3.toArray()));

            return new ApiResponse(HttpStatus.OK, "Success");
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(HttpStatus.OK, e.getMessage());
        }
    }
}
