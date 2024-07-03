package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.otm.location.OtmFixedStopTime;
import com.inspirage.ilct.dto.otm.location.OtmVariableStopTime;
import com.inspirage.ilct.dto.otm.shipment.GidHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "location")
@Getter
@Setter
@ToString
public class LocationDoc extends BaseDoc {
    @Indexed
    private String siteId;
    private String siteName;
    private String city;
    private String province;
    private String provinceCode;
    private String postalCode;
    private String countryCode;
    private String zone1;
    private String zone2;
    private String zone3;
    private String zone4;
    private LatLng location;
    private String locationDocId;
    private String siteRole;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private List<GidHolder> activityDefinition = new ArrayList<>();
    private List<OtmFixedStopTime> activityFixedStopTime = new ArrayList<>();
    private List<OtmVariableStopTime> activityVariableStopTime = new ArrayList<>();
    private String siteRefnumQualifierId;
    private String siteRefnumValue;
    private String siteResourceId;
    private String siteResourceName;
    private String resourceType;
    private String reservedAppointment;
    private String availableSlots;
    private String calendar;
    private String appointmentActivity;
    private String contactId;
}