
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ServiceProviderAliasQualifierGid",
    "ServiceProviderAliasValue"
})
public class OtmServiceProviderAlias {

    @JsonProperty("ServiceProviderAliasQualifierGid")
    public GidHolder otmServiceProviderAliasQualifierGid;
    @JsonProperty("ServiceProviderAliasValue")
    public String otmServiceProviderAliasValue;
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
