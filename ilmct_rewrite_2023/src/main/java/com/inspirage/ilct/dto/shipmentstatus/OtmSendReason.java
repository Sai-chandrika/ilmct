
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;
import com.inspirage.ilct.dto.otm.OtmRemark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Remark",
    "SendReasonGid",
    "ObjectType"
})
public class OtmSendReason {

    @JsonProperty("Remark")
    public List<OtmRemark> otmRemark = null;
    @JsonProperty("SendReasonGid")
    public GidHolder otmSendReasonGid;
    @JsonProperty("ObjectType")
    public String otmObjectType;
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
