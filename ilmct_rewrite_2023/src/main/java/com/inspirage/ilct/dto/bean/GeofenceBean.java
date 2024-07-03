package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inspirage.ilct.documents.LatLng;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class GeofenceBean {
	private String fencingType;
	private Map<Double, Double> xyCords;
	private List<Double> xyCordsList = new ArrayList<>();
	private float radius;
	private LatLng location;
	private String locationDocId;
	private String shipmentId;
}
