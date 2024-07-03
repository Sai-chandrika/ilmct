package com.inspirage.ilct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LegShipmentsBean {
    private WaterTransportLeg waterTransportLeg;
    private RoadTransportLeg roadTransportLeg;
}
