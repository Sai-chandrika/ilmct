package com.inspirage.ilct.dto.shipmentstatus;


import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transmission {
    @JsonProperty("xmlns")
    public String xmlns;

    @JsonProperty("TransmissionBody")
    public TransmissionBody transmissionBody;

    @JsonProperty("TransmissionHeader")
    public OtmTransmissionHeader otmTransmissionHeader;

    @JsonIgnore
    private Map<String,Object> additionalProperties=new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
