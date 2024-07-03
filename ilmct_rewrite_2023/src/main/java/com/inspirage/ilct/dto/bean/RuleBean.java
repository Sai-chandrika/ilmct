package com.inspirage.ilct.dto.bean;

import com.inspirage.ilct.util.Utility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RuleBean {

    private int delayMinutes;
    private int shortDelayMinutes;
    private int avgDelayMinutes;
    private int longDelayMinutes;
    private float temperatureAlert;
    private float speedAlert;
    private float fuelAlert;
    private String userId;

    //Fields for KPI enhancement rules
    private int outBoundInTransitDelayInHours;
    private int outBoundRiskDemurageInHours;
    private int inBoundDelayInHours;
    private int inBoundRiskDemurageInDays;
    private int lastSeenHours;

    public boolean validate() {
        return !Utility.isEmpty(userId) || delayMinutes > 0;
    }
}
