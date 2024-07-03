package com.inspirage.ilct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShipmentTrackingBeanV2 {
    private String loadId;
    private String truckNo;
    private String trailerNo;
    private String orderNo;
    private String truckType;
    private String containerNo;
    private Double weightPercentage;
    private String carrierName;
    private String source;
    private String destination;
    private List<SpecialServicesBean> specialServices = new ArrayList<>();
    private Integer deliveryStops = 0;
    private String deliveryPTA;
    private String deliveryETA;
    private String weatherAlerts;
    private List<AlertsBean> alerts = new ArrayList<>();
    private String highTemperature;
    private boolean isHighTemperatureAlert = false;
    private boolean isHighSpeedAlert = false;
    private boolean isLowFuelAlert = false;
    private String currentSpeed;
    private String currentFuel;
    private String lowTemperature;
    private String humidity;
    private Boolean isWatchlistAdded = Boolean.FALSE;
    private String url;
    private String status;
    private String lastSeen;
    private String lastSeenColorCode;
}
