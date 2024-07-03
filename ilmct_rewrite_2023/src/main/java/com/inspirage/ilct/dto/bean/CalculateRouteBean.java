package com.inspirage.ilct.dto.bean;

import com.inspirage.ilct.dto.RoutePointBean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CalculateRouteBean {
    private Double totalWeightValue;
    private boolean isHazardous;
    private LocalDateTime startDate;
    private boolean onlySummeryData;
    private RoutePointBean[] pointBeans;
    private double distanceTillLastEvent;

    public static CalculateRouteBean Builder(Double totalWeightValue, boolean isHazardous, LocalDateTime startDate, boolean onlySummeryData, RoutePointBean... wayPoints) {
        CalculateRouteBean calculateRoute = new CalculateRouteBean(totalWeightValue,isHazardous,startDate,onlySummeryData,wayPoints,0.0);
        return calculateRoute;
    }
}
