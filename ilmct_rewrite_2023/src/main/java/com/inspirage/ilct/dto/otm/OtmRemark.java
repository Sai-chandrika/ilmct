
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "RemarkQualifierGid",
    "RemarkText",
    "RemarkSequence"
})
public class OtmRemark {

    @JsonProperty("RemarkQualifierGid")
    public GidHolder otmRemarkQualifierGid;
    @JsonProperty("RemarkText")
    public String otmRemarkText;
    @JsonProperty("RemarkSequence")
    public Integer otmRemarkSequence;
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
