
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;
import com.inspirage.ilct.dto.bean.TimeEstimationBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ArrivalTime",
    "StopSequence",
    "DepartureTime",
    "LocationRef",
    "ShipmentStopDetail",
        "FlexFieldDates",
        "FlexFieldNumbers",
        "IsFixedDistance",
        "IsMotherVessel",
        "DistFromPrevStop",
        "AccessorialTime",
        "StopDuration",
        "LocationRoleGid",
        "IsDepot",
        "IsPermanent",
        "IsAppointment",
        "RushHourTime",
        "FlexFieldStrings",
        "StopReason"
})
public class OtmShipmentStop {

    public Boolean isDelivered = false;
    public LocalDateTime actualTimeOfArrival;
    public TimeEstimationBean calculatedETA;
    public TimeEstimationBean calculatedETD;
    public String siteName;
    public String siteId;
    public String countryCode;
    public LocalDateTime actualTimeOfDeparture;


    @JsonProperty("ArrivalTime")
    public OtmArrivalTime otmArrivalTime;
    @JsonProperty("StopSequence")
    public Integer otmStopSequence;
    @JsonProperty("DepartureTime")
    public OtmDepartureTime otmDepartureTime;
    @JsonProperty("LocationRef")
    public OtmLocationRef otmLocationRef;
    @JsonProperty("ShipmentStopDetail")
    public List<OtmShipmentStopDetail> otmShipmentStopDetail = null;

    @JsonProperty("StopType")
    public String otmStopType;



        @JsonProperty("FlexFieldDates")
	public Object flexFieldDates;

        @JsonProperty("FlexFieldNumbers")
        public String flexFieldNumbers;
        @JsonProperty("IsFixedDistance")
        public String isFixedDistance;
        @JsonProperty("IsMotherVessel")
        public String isMotherVessel;
        @JsonProperty("DistFromPrevStop")
        public DistFromPrevStop distFromPrevStop;
        @JsonProperty("AccessorialTime")
        public AccessorialTime accessorialTime;
        @JsonProperty("StopDuration")
        public StopDuration stopDuration;
        @JsonProperty("LocationRoleGid")
        public GidHolder locationRoleGid;
        @JsonProperty("IsDepot")
        public String isDepot;
        @JsonProperty("IsPermanent")
        public String isPermanent;
        @JsonProperty("IsAppointment")
        public String isAppointment;
        @JsonProperty("RushHourTime")
        public RushHourTime rushHourTime;
        @JsonProperty("FlexFieldStrings")
        public String flexFieldStrings;
        @JsonProperty("StopReason")
        public String stopReason;

        public LocalDateTime gateInEventOccuredOn;

        public LocalDateTime gateOutEventOccuredOn;

    public Integer getOtmStopSequence() {
        return otmStopSequence;
    }

    @JsonIgnore
    private   Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
