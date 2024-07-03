package com.inspirage.ilct.dto.bean;

import lombok.Data;

@Data
public class ApplicationStatus {

    private Integer onTimeMinThreshold;
    private Integer onTimeMaxThreshold;
    private Integer arrivingEarlyMinThreshold;
    private Integer arrivingEarlyMaxThreshold;
    private Integer delayedMinThreshold;
    private Integer delayedMaxThreshold;
    private Integer slightlyDelayedMinThreshold;
    private Integer slightlyDelayedMaxThreshold;

}
