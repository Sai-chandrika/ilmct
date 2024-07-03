
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "LateDeliveryDt",
    "PickupIsAppt",
    "DeliveryIsAppt"
})
public class OtmTimeWindow {

    @JsonProperty("LateDeliveryDt")
    public OtmLateDeliveryDt otmLateDeliveryDt;
    @JsonProperty("PickupIsAppt")
    public String otmPickupIsAppt;
    @JsonProperty("DeliveryIsAppt")
    public String otmDeliveryIsAppt;
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
