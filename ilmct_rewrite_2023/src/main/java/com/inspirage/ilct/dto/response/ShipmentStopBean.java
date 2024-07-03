package com.inspirage.ilct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShipmentStopBean {
    private int stopSequence;
    private String stopType;
    private String locationName;
    private String addressDetails;
    private String pta;
    private String eta;
    private String calculatedEta;
    private String calculatedEtd;
    private String ata;
    private String atd;
    private String orderDetails;
    private Float distance;
    private List<OrdersDetailsSummaryBean> ordersDetailsSummaryBeanList = new ArrayList<>();
}