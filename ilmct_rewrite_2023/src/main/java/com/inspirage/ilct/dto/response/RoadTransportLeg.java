package com.inspirage.ilct.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoadTransportLeg {
    private String loadId;
    private String globalId;
    private String containerName;
    private String pickup;
    private String delivery;
    private String truckNumber;
    private String status;
}
