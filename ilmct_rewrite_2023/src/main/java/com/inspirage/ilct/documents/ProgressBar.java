package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProgressBar {
    private List<ShipmentStopV2> stops;
    private Double DistanceTravelledPercentage;
}
