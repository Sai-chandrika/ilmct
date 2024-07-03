
package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "TZOffset",
    "GLogDate",
    "TZId"
})
public class OtmEventRecdDate {

    @JsonProperty("TZOffset")
    public String otmTZOffset;
    @JsonProperty("GLogDate")
    public String otmGLogDate;
    @JsonProperty("TZId")
    public String otmTZId;
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
