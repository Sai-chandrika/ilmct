package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "rule")
public class RuleDoc extends BaseDoc {

	private String userId;
	@Deprecated
	private int delayMinutes;
	private int shortDelayMinutes;
	private int avgDelayMinutes;
	private int longDelayMinutes;
	// INFO We need to have multiple types of delays
	private float temperatureAlert;
	private float speedAlert;
	private float fuelAlert;
	private int lastSeenHours;
	// Fields for KPI enhancement rules
	private int outBoundInTransitDelayInHours;
	private int outBoundRiskDemurageInHours;
	private int inBoundDelayInHours;
	private int inBoundRiskDemurageInDays;

}
