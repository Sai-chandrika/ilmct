package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GLogXMLElement {
    @JsonProperty("TransactionHeader")
    public OtmTransactionHeader otmTransactionHeader;
    @JsonProperty("ShipmentStatus")
    public OtmShipmentStatus otmShipmentStatus;
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
