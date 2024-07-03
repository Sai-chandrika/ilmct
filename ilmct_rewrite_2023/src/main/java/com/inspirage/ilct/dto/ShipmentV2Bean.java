package com.inspirage.ilct.dto;

import com.inspirage.ilct.documents.ProgressBar;
import com.inspirage.ilct.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
public class ShipmentV2Bean {
    @Indexed
    private String loadID;
    private String truckNo;
    private String shipper;
    private String consignee;
    private StatusEnum status;
    private Double totalDistance;
    private Double distanceTravelledInKms;
    private String travelledDistancePercentage;
}
