package com.inspirage.ilct.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerShipmentBean {
    private String loadId;
    private String containerNo;
    private String orderNo;
    private String liner;
    private Integer noOfLegs = 0;
    private String PTA,ETA;
    private Integer noOfOrders;
    private Double weightPercentage;
    private String source;
    private String destination;
    private String originPort;
    private String destinationPort;
    private String currentStatus;
    private List<LegShipmentsBean> legShipmentsList = new ArrayList<>();

}
