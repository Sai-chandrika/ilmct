
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "daylight",
    "description",
    "skyInfo",
    "skyDescription",
    "temperature",
    "temperatureDesc",
    "comfort",
    "highTemperature",
    "lowTemperature",
    "humidity",
    "dewPoint",
    "precipitation1H",
    "precipitation3H",
    "precipitation6H",
    "precipitation12H",
    "precipitation24H",
    "precipitationDesc",
    "airInfo",
    "airDescription",
    "windSpeed",
    "windDirection",
    "windDesc",
    "windDescShort",
    "barometerPressure",
    "barometerTrend",
    "visibility",
    "snowCover",
    "icon",
    "iconName",
    "iconLink",
    "ageMinutes",
    "activeAlerts",
    "country",
    "state",
    "city",
    "latitude",
    "longitude",
    "distance",
    "elevation",
    "utcTime"
})
public class Observation {

    @JsonProperty("daylight")
    private String daylight;

    @JsonProperty("description")
    private String description;

    @JsonProperty("skyInfo")
    private String skyInfo;

    @JsonProperty("skyDescription")
    private String skyDescription;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("temperatureDesc")
    private String temperatureDesc;

    @JsonProperty("comfort")
    private String comfort;

    @JsonProperty("highTemperature")
    private String highTemperature;

    @JsonProperty("lowTemperature")
    private String lowTemperature;

    @JsonProperty("humidity")
    private String humidity;

    @JsonProperty("dewPoint")
    private String dewPoint;

    @JsonIgnore
    @JsonProperty("precipitation1H")
    private String precipitation1H;

    @JsonIgnore
    @JsonProperty("precipitation3H")
    private String precipitation3H;

    @JsonIgnore
    @JsonProperty("precipitation6H")
    private String precipitation6H;

    @JsonIgnore
    @JsonProperty("precipitation12H")
    private String precipitation12H;

    @JsonIgnore
    @JsonProperty("precipitation24H")
    private String precipitation24H;

    @JsonIgnore
    @JsonProperty("precipitationDesc")
    private String precipitationDesc;

    @JsonProperty("airInfo")
    private String airInfo;

    @JsonProperty("airDescription")
    private String airDescription;

    @JsonProperty("windSpeed")
    private String windSpeed;

    @JsonProperty("windDirection")
    private String windDirection;

    @JsonProperty("windDesc")
    private String windDesc;

    @JsonProperty("windDescShort")
    private String windDescShort;

    @JsonProperty("barometerPressure")
    private String barometerPressure;

    @JsonProperty("barometerTrend")
    private String barometerTrend;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("snowCover")
    private String snowCover;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("iconName")
    private String iconName;

    @JsonProperty("iconLink")
    private String iconLink;

    @JsonProperty("ageMinutes")
    private String ageMinutes;

    @JsonProperty("activeAlerts")
    private String activeAlerts;

    @JsonProperty("country")
    private String country;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("latitude")
    private Float latitude;

    @JsonProperty("longitude")
    private Float longitude;

    @JsonIgnore
    @JsonProperty("distance")
    private Float distance;

    @JsonIgnore
    @JsonProperty("elevation")
    private Float elevation;

    @JsonIgnore
    @JsonProperty("utcTime")
    private String utcTime;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
