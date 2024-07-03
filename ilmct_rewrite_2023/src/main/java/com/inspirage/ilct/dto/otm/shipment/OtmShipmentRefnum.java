
package com.inspirage.ilct.dto.otm.shipment;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ShipmentRefnumQualifierGid",
    "ShipmentRefnumValue"
})
public class OtmShipmentRefnum {

    @JsonProperty("ShipmentRefnumQualifierGid")
    public GidHolder otmShipmentRefnumQualifierGid;
    @JsonProperty("ShipmentRefnumValue")
    public String otmShipmentRefnumValue;
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
