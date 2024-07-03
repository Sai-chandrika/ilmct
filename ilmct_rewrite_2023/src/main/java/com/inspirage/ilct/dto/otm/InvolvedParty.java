package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"InvolvedPartyQualifierGid",
"InvolvedPartyLocationRef",
"ContactRef",
"ComMethodGid"
})
public class InvolvedParty {

    @JsonProperty("InvolvedPartyQualifierGid")
    public GidHolder involvedPartyQualifierGid;
    @JsonProperty("InvolvedPartyLocationRef")
    public InvolvedPartyLocationRef involvedPartyLocationRef;
    @JsonProperty("ContactRef")
    public ContactRef contactRef;
    @JsonProperty("ComMethodGid")
    public GidHolder comMethodGid;
    @JsonIgnore
    private  Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
