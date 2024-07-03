package com.inspirage.ilct.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterTransportLeg {
    private String loadId;
    private String globalId;
    private String liner;
    private String originPort;
    private String DestinationPort;
    private String status;
    private String vesselName;
    private String BL;
    private String FF;
}
