package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.otm.OtmShipmentStop;
import com.inspirage.ilct.enums.FencingEventType;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "geofence_event")
@CompoundIndex(def = "{'loadId':1,'createdDate':-1}")
@ToString
public class GeoFenceEventDoc extends BaseDoc {
	private String eventId;
	private OtmShipmentStop otmShipmentStop;
	private String loadId;
	private String shipmentId;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date triggeredOn;
	private String timeZone;
	private double latitude;
	private double longitude;
	private FencingEventType fenceEventType;
	private String truckId;
	private String otmGeoFenceEventResponse;
	private String gLogDate;
	private String gLogTimeZone;
	private ShipmentStopV2 shipmentStopV2;
}
