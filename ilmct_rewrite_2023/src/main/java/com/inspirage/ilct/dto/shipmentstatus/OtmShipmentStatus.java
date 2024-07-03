
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;
import com.inspirage.ilct.dto.otm.OtmShipmentRefnum;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*@JsonPropertyOrder({
        "FlexFieldStrings",
        "SStatusSEquipment",
        "SSStop",
        "StatusCodeGid",
        "PowerUnitGid",
        "xmlns:otm",
        "TimeZoneGid",
        "ResponsiblePartyGid",
        "ShipmentGid",
        "xmlns:gtm",
        "StatusGroup",
        "SendReason",
        "FlexFieldNumbers",
        "FlexFieldDates",
        "EventRecdDate",
        "DriverGid",
        "EventDt",
        "RailInfo",
        "EventGroup"
})*/
@Setter
@Getter
public class OtmShipmentStatus {

    @JsonProperty("FlexFieldStrings")
    public String otmFlexFieldStrings;
    @JsonProperty("SStatusSEquipment")
    public OtmSStatusSEquipment otmSStatusSEquipment;
    @JsonProperty("SSStop")
    public OtmSSStop otmSSStop;
    @JsonProperty("StatusCodeGid")
    public GidHolder otmStatusCodeGid;
    @JsonProperty("PowerUnitGid")
    public GidHolder otmPowerUnitGid;
    @JsonProperty("xmlns:otm")
    public String xmlnsOtm;
    @JsonProperty("TimeZoneGid")
    public GidHolder otmTimeZoneGid;
    @JsonProperty("ResponsiblePartyGid")
    public GidHolder otmResponsiblePartyGid;
    @JsonProperty("ShipmentGid")
    public GidHolder otmShipmentGid;
    @JsonProperty("xmlns:gtm")
    public String xmlnsGtm;
    @JsonProperty("StatusGroup")
    public OtmStatusGroup otmStatusGroup;
    @JsonProperty("SendReason")
    public OtmSendReason otmSendReason;
    @JsonProperty("FlexFieldNumbers")
    public String otmFlexFieldNumbers;
    @JsonProperty("FlexFieldDates")
    public OtmFlexFieldDates otmFlexFieldDates;

    @JsonProperty("EventRecdDate")
    public OtmEventRecdDate otmEventRecdDate;
    @JsonProperty("DriverGid")
    public GidHolder otmDriverGid;
    @JsonProperty("EventDt")
    public OtmEventDt otmEventDt;
    @JsonProperty("RailInfo")
    public OtmRailInfo otmRailInfo;
    @JsonProperty("EventGroup")
    public OtmEventGroup otmEventGroup;
    @JsonProperty("ShipmentRefnum")
    public List<OtmShipmentRefnum> otmShipmentRefnum = null;
    @JsonProperty("StatusReasonCodeGid")
    public GidHolder otmStatusReasonCodeGid;

    @JsonProperty("StatusLevel")
    public String statusLevel;

    @JsonProperty("SSRemarks")
    public String ssRemarks;

    @JsonProperty("QuickCodeGid")
    public GidHolder quickCodeGid;

    @JsonProperty("TrackingNumber")
    public String trackingNumber;


    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
