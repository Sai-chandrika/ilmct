package com.inspirage.ilct.documents;

import com.inspirage.ilct.enums.FencingType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Document(collection = "Geofence")
public class GeofenceDoc extends BaseDoc {
	private FencingType fencingType;
	private LatLng location;
	private List<LatLng> xyCordsList = new ArrayList<>();
	private float radius;
	private String locationDocId;
	private String lastUpdateBy;
}
