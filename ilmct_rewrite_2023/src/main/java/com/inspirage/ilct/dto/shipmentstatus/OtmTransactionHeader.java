package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "xmlns:otm",
    "SenderTransactionId",
    "xmlns:gtm",
    "xsi:type",
    "SendReason",
    "xmlns:xsi"
})
public class OtmTransactionHeader {

    @JsonProperty("xmlns:otm")
    public String xmlnsOtm;
    @JsonProperty("SenderTransactionId")
    public Integer otmSenderTransactionId;
    @JsonProperty("xmlns:gtm")
    public String xmlnsGtm;
    @JsonProperty("xsi:type")
    public String xsiType;
    @JsonProperty("SendReason")
    public OtmSendReason otmSendReason;
    @JsonProperty("xmlns:xsi")
    public String xmlnsXsi;
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
