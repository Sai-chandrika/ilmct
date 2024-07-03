package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
/*@JsonPropertyOrder({
        "GLogXMLElement"
})*/
public class TransmissionBody {
    @JsonProperty("GLogXMLElement")
    public GLogXMLElement gLogXMLElement;

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
