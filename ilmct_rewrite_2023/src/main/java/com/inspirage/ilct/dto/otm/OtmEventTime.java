
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "PlannedTime",
    "IsPlannedTimeFixed",
    "EstimatedTime"
})
public class OtmEventTime {

    @JsonProperty("PlannedTime")
    public OtmEstimatedTime otmPlannedTime;
    @JsonProperty("IsPlannedTimeFixed")
    public String otmIsPlannedTimeFixed;
    @JsonProperty("EstimatedTime")
    public OtmEstimatedTime otmEstimatedTime;
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
