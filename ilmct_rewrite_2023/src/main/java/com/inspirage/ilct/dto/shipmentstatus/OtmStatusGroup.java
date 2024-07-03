
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "StatusGroupGid",
    "StatusGroupDescription"
})
public class OtmStatusGroup {

    @JsonProperty("StatusGroupGid")
    public GidHolder otmStatusGroupGid;
    @JsonProperty("StatusGroupDescription")
    public String otmStatusGroupDescription;
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
