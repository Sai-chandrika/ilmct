package com.inspirage.ilct.documents;

//import com.inspirage.ilct.dto.otm.OtmEventDt;
//import com.inspirage.ilct.dto.OtmEventRecdDate;
import com.inspirage.ilct.dto.bean.TimeEstimationBean;
import com.inspirage.ilct.dto.here.weather.Alerts;
import com.inspirage.ilct.dto.otm.OtmShipmentRefnum;
import com.inspirage.ilct.dto.shipmentstatus.OtmEventDt;
import com.inspirage.ilct.dto.shipmentstatus.OtmEventRecdDate;
import com.inspirage.ilct.dto.shipmentstatus.OtmFlexFieldDates;
import com.inspirage.ilct.util.DateUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document(collection = "shipment_status")
@EqualsAndHashCode(callSuper = false)
@CompoundIndex(def = "{'loadId':1,'statusCodeId':1,'createdDate':-1,'lastUpdated':-1}", name = "status_doc_index_1")
public class ShipmentStatusDoc extends BaseDoc implements Comparable<ShipmentStatusDoc> {

    private String trackingId;
    @Indexed
    private String loadId;
    private OtmEventDt eventDate;
    private String eventDescription;
    private String eventLocation;
    private String statusCodeId;
    private String statusCodeDescription;
    private String isLoaded;
    private Integer temperature = 0;
    private String tempUnit;
    private Integer speed = 0;
    private String speedUnit;
    private Integer fuel = 0;
    private String fuelUnit;
    private LatLng location;
    private String sensors;
    private String remarks;
    private String carrierId;
    private String driverId;
    private String powerUnitId;
    private OtmEventRecdDate eventReceivedDate;
    private String eventType;
    private List<OtmShipmentRefnum> shipmentRefnum;
    private String truckNo;
    private int stopSequence;
    private String engStat;
    private String doorStat;

    private TimeEstimationBean etaToDestination;
    private Alerts weatherAlert;
    private Double truckDirectionAngle = 0d;

    private String quickCodeGid = "";
    private String parcelRefNum = "";
    private GeoFenceEventDoc geoFenceEventDoc;
    @Transient
    public OtmFlexFieldDates otmflexFieldDates;
    private String address;
    private String statusReasonCodeGid;
    private String locationName;
    private Map<String, Object> additionalProperties;

    @Override
    public void setCreatedDate(Date createdDate) {
        super.setCreatedDate(createdDate);
    }

    public LocalDateTime getEventDateAsLocalDate(String userTimeZone) {
        LocalDateTime dateTime = DateUtil.convertDate(
                this.getEventDate().otmGLogDate,
                DateUtil.dateTimeFormatter2,
                this.getEventDate().otmTZId,
                userTimeZone
        );
        if (dateTime == null) {
            dateTime = DateUtil.convertDate(
                    this.getEventDate().otmGLogDate,
                    DateUtil.dateTimeFormatter2,
                    null,
                    userTimeZone
            );
        }
        return dateTime;
    }
    @Override
    public int compareTo(ShipmentStatusDoc o) {
        String zone = ZoneId.systemDefault().getId();
        return o.getEventDateAsLocalDate(zone).compareTo(getEventDateAsLocalDate(zone));
    }
}
