package com.inspirage.ilct.dto.otm.location;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.otm.shipment.OtmDuration;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Duration"
})
public class OtmFixedStopTime {

    @JsonProperty("Duration")
    public OtmDuration otmDuration;
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
