package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inspirage.ilct.dto.RoutePointBean;
import com.inspirage.ilct.util.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TimeEstimationBean {
	private String estimatedDateTime;
	private LocalDateTime estimatedDate;
	private Boolean withTraffic;
	private int provider;
	private String text;
	private double distance;
	private double distanceTravelled;
	private double distanceToTravel;
	private String distanceTravelledStr;
	private String distanceToTravelStr;
	@JsonIgnore
	private List<RoutePointBean> routePoints = new ArrayList<>();

	public TimeEstimationBean(RoutePointBean routePointBean, LocalDateTime lastEventDate) {
		super();

		LocalDateTime etaDateTime = (lastEventDate != null ? lastEventDate : LocalDateTime.now())
				.plusSeconds(routePointBean.estimateTime);

		this.setEstimatedDate(etaDateTime);
		this.setEstimatedDateTime(
				etaDateTime != null ? etaDateTime.format(DateTimeFormatter.ofPattern(DateUtil.dateTimeFormatter1)) : null);
		this.setDistance(routePointBean.getDistance());
	}

	public TimeEstimationBean(Long activityTime, LocalDateTime esDateTime) {
		super();
		if (esDateTime != null) {
			LocalDateTime unloadTime = esDateTime.plusMinutes(activityTime);
			this.setEstimatedDate(unloadTime);
			this.setEstimatedDateTime(
					unloadTime != null ? unloadTime.format(DateTimeFormatter.ofPattern(DateUtil.dateTimeFormatter1)) : null);
		}
	}
}
