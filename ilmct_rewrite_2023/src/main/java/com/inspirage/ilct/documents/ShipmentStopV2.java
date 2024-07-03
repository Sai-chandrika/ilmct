package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.PalletsDTO;
import com.inspirage.ilct.dto.StopDTO;
import com.inspirage.ilct.service.CacheService;
import com.inspirage.ilct.util.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentStopV2 {
    private Integer stopNumber;
    private String stopType;
    private ShipmentLocation location;
    private ShipmentDateTime plannedArrival;
    private ShipmentDateTime estimatedArrival;
    private ShipmentDateTime actualArrival;
    private ShipmentDateTime plannedDeparture;
    private ShipmentDateTime estimatedDeparture;
    private ShipmentDateTime actualDeparture;
    private ShipmentDateTime calculatedETA;
    private ShipmentDateTime calculatedETD;
    private String gateInEventOn;
    private String gateOutEventOn;
    private boolean isDelivered;
    private float distanceInKms;
    private ShipmentStopContent stopContent;
    @Value("0.0")
    private double totalDistanceInKmsPercentage;

    public static ShipmentStopV2 toShipmentStopV2(StopDTO stopDTO, CacheService cacheService, Boolean isIlmctShipment, PalletsDTO palletsDTO) {
        ShipmentStopV2 shipmentStopV2 = new ShipmentStopV2();
        shipmentStopV2.stopNumber = stopDTO.getStopNum();
        shipmentStopV2.stopType = stopDTO.getStopType();
        shipmentStopV2.location = ShipmentLocation.toShipmentLocation(cacheService.findLocationBySiteId(stopDTO.getStopLocation()));
        if(stopDTO.getPlannedArrival() != null && stopDTO.getPlannedArrival().getDateTime() != null && stopDTO.getPlannedArrival() != null ) {
            shipmentStopV2.plannedArrival = new ShipmentDateTime(stopDTO.getPlannedArrival().getDateTime(), stopDTO.getPlannedArrival().getTZId());
        }
        if(stopDTO.getEstimatedArrival() != null && ObjectUtils.isNotEmpty(stopDTO.getEstimatedArrival().getDateTime())) {
            shipmentStopV2.estimatedArrival = new ShipmentDateTime(stopDTO.getEstimatedArrival().getDateTime(), stopDTO.getEstimatedArrival().getTZId());
        }
        if(stopDTO.getPlannedDeparture() != null && ObjectUtils.isNotEmpty(stopDTO.getPlannedDeparture().getDateTime())) {
            shipmentStopV2.plannedDeparture = new ShipmentDateTime(stopDTO.getPlannedDeparture().getDateTime(), stopDTO.getPlannedDeparture().getTZId());
        }
        if(stopDTO.getEstimatedDeparture()!=null && ObjectUtils.isNotEmpty(stopDTO.getEstimatedDeparture().getDateTime())) {
            shipmentStopV2.estimatedDeparture = new ShipmentDateTime(stopDTO.getEstimatedDeparture().getDateTime(), stopDTO.getEstimatedDeparture().getTZId());
        }
        if (!isIlmctShipment) {
            LocalDateTime localDateTime = DateUtil.convertDate(shipmentStopV2.getEstimatedArrival().getDateTime(),
                    DateUtil.dateTimeFormatter2, shipmentStopV2.getEstimatedArrival().getTZId(), null);
            shipmentStopV2.calculatedETA = new ShipmentDateTime(DateUtil.formatDate(localDateTime, DateUtil.dateTimeFormatter2),null);
        }
        if(stopDTO.getActualArrival() != null && ObjectUtils.isNotEmpty(stopDTO.getActualArrival().getDateTime())) {
            shipmentStopV2.actualArrival = new ShipmentDateTime(stopDTO.getActualArrival().getDateTime(), stopDTO.getActualArrival().getTZId());
        }
        if(stopDTO.getActualDeparture() != null && ObjectUtils.isNotEmpty(stopDTO.getActualDeparture().getDateTime())) {
            shipmentStopV2.actualDeparture = new ShipmentDateTime(stopDTO.getActualDeparture().getDateTime(), stopDTO.getActualDeparture().getTZId());
        }
        if(stopDTO.getStopContent() != null) {
            shipmentStopV2.stopContent = ShipmentStopContent.toShipmentStopContent(stopDTO.getStopContent(), cacheService, palletsDTO);
        }
        return shipmentStopV2;
    }
}
