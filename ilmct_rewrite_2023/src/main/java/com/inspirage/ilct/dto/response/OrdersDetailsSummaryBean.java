package com.inspirage.ilct.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersDetailsSummaryBean {
    private String id;
    private String palletId;
    private String name;
    private String quantity;
    private String weight;
    private String volume;
    private String measurement;
    private String volumeMetric;
    private String customerItemNumber;
    private String commodity;
    private String shipToLocation;
}
