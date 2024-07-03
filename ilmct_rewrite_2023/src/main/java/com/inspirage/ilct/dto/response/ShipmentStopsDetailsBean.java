package com.inspirage.ilct.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShipmentStopsDetailsBean {
    private String globalId;
    private String driverName;
    private String driverContractNo;
    private String alerts;
    private Double distanceTravelledInKms;
    private Double distancePendingInKms;
    private Double totalDistanceInKms;
    private String travelledDistancePercentage;
    private List<ShipmentStopBean> shipmentStopBeanList = new ArrayList<>();
}
