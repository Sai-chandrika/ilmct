
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Perspective"
})
public class OtmShipmentHeader2 {

    @JsonProperty("Perspective")
    public String otmPerspective;
    
    @JsonProperty("WeightUtil")
    public double otmWeightUtil;
    
    @JsonProperty("VolumeUtil")
    public double otmVolumeUtil;
    
    @JsonProperty("EquipRefUnitUtil")
    public double otmEquipRefUnitUtil;
    
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
