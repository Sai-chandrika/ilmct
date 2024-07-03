
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SSLocation",
    "SSStopSequenceNum"
})
public class OtmSSStop {

    @JsonProperty("SSLocation")
    public OtmSSLocation otmSSLocation;
    @JsonProperty("SSStopSequenceNum")
    public Integer otmSSStopSequenceNum;
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
