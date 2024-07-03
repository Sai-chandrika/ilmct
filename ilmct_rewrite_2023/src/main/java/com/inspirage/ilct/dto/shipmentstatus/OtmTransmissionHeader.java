package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*@JsonPropertyOrder({
        "xmlns:otm",
        "GLogXMLElementName",
        "Version",
        "NotifyInfo",
        "SenderSystemID",
        "xmlns:gtm",
        "ReferenceTransmissionNo",
        "SenderHostName",
        "TransmissionCreateDt",
        "TransactionCount",
        "SenderTransmissionNo"
})*/
public class OtmTransmissionHeader {

    @JsonProperty("xmlns:otm")
    public String xmlnsOtm;
    @JsonProperty("GLogXMLElementName")
    public String otmGLogXMLElementName;
    @JsonProperty("Version")
    public String otmVersion;
    @JsonProperty("NotifyInfo")
    public OtmNotifyInfo otmNotifyInfo;
    @JsonProperty("SenderSystemID")
    public String otmSenderSystemID;
    @JsonProperty("xmlns:gtm")
    public String xmlnsGtm;
    @JsonProperty("ReferenceTransmissionNo")
    public Integer otmReferenceTransmissionNo;
    @JsonProperty("SenderHostName")
    public String otmSenderHostName;
    @JsonProperty("TransmissionCreateDt")
    public OtmTransmissionCreateDt otmTransmissionCreateDt;
    @JsonProperty("TransactionCount")
    public Integer otmTransactionCount;
    @JsonProperty("SenderTransmissionNo")
    public Long otmSenderTransmissionNo;

    @JsonProperty("AckSpec")
    public com.inspirage.ilct.dto.shipmentstatus.AckSpec AckSpec;

    @JsonProperty("IsProcessInSequence")
    public String IsProcessInSequence;

    @JsonProperty("StopProcessOnError")
    public String StopProcessOnError;

    @JsonProperty("Refnum")
    public ParcelRefNumDto Refnum;
    @JsonProperty("LocationName")
    public String LocationName;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
