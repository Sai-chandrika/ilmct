
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "alerts",
    "feedCreation",
    "metric"
})
public class WeatherAlertDTO {

    @JsonProperty("observations")
    private Observations observations;

    @JsonProperty("alerts")
    private Alerts alerts;

    @JsonProperty("feedCreation")
    private String feedCreation;

    @JsonProperty("metric")
    private Boolean metric;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
