package com.inspirage.ilct.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StopDTO {
    private ShipmentDateTimeDTO ActualArrival;
    private Integer StopNum;
    private ShipmentDateTimeDTO PlannedArrival;
    private ShipmentDateTimeDTO EstimatedArrival;
    private ShipmentDateTimeDTO ActualDeparture;
    private String StopLocation;
    private StopContentDTO StopContent;
    private String StopType;
    private ShipmentDateTimeDTO PlannedDeparture;
    private ShipmentDateTimeDTO EstimatedDeparture;
}
