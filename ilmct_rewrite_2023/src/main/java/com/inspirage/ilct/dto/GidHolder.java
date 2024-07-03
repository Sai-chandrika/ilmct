
package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.otm.shipment.OtmGid;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*@JsonPropertyOrder({
        "Gid"
})*/
@Getter
@Setter
public class GidHolder {

    @JsonProperty("Gid")
    public OtmGid otmGid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
