package com.inspirage.ilct.dto.otm.shipment;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.otm.shipment.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DurationUOMGid",
    "DurationValue"
})
public class OtmDuration {

    @JsonProperty("DurationUOMGid")
    public GidHolder otmDurationUOMGid;
    @JsonProperty("DurationValue")
    public Integer otmDurationValue;
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
