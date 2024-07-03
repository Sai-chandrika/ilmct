
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "alerts",
    "country",
    "state",
    "city",
    "latitude",
    "longitude",
    "timezone"
})
public class Alerts {

    @JsonProperty("alerts")
    private List<Alert> alerts = null;

    @JsonIgnore
    private List<Observation> observation = null;

    @JsonProperty("country")
    private String country;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("timezone")
    private Integer timezone;


    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
