
package com.inspirage.ilct.dto.flight;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fs",
    "iata",
    "icao",
    "name",
    "city",
    "cityCode",
    "countryCode",
    "countryName",
    "regionName",
    "timeZoneRegionName",
    "localTime",
    "utcOffsetHours",
    "latitude",
    "longitude",
    "elevationFeet",
    "classification",
    "active",
    "delayIndexUrl",
    "weatherUrl"
})
public class Airport {

    @JsonProperty("fs")
    private String fs;

    @JsonProperty("iata")
    private String iata;

    @JsonProperty("icao")
    private String icao;

    @JsonProperty("name")
    private String name;

    @JsonProperty("city")
    private String city;

    @JsonProperty("cityCode")
    private String cityCode;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("regionName")
    private String regionName;

    @JsonProperty("timeZoneRegionName")
    private String timeZoneRegionName;

    @JsonProperty("localTime")
    private String localTime;

    @JsonProperty("utcOffsetHours")
    private Double utcOffsetHours;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("elevationFeet")
    private Integer elevationFeet;

    @JsonProperty("classification")
    private Integer classification;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("delayIndexUrl")
    private String delayIndexUrl;

    @JsonProperty("weatherUrl")
    private String weatherUrl;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
