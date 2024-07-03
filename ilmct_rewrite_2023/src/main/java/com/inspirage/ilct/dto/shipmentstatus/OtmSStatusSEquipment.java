
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SEquipmentGid",
    "EquipmentInitialNumber",
    "EquipmentInitial",
    "EquipmentNumber",
    "EquipmentGid"
})
public class OtmSStatusSEquipment {

    @JsonProperty("SEquipmentGid")
    public GidHolder otmSEquipmentGid;
    @JsonProperty("EquipmentInitialNumber")
    public String otmEquipmentInitialNumber;
    @JsonProperty("EquipmentInitial")
    public String otmEquipmentInitial;
    @JsonProperty("EquipmentNumber")
    public Integer otmEquipmentNumber;
    @JsonProperty("EquipmentGid")
    public GidHolder otmEquipmentGid;
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
