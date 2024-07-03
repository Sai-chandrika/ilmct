
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "EquipmentInitialNumber",
    "EquipmentOwner",
    "FlexFieldStrings",
    "OwnershipCode",
    "EquipmentName",
    "OwnerType",
    "DateBuilt",
    "EquipmentTypeGid",
    "EquipmentNumber",
    "EquipmentGid",
    "EquipmentInitial",
    "TransactionCode",
    "IsContainer",
    "FlexFieldNumbers",
    "FlexFieldDates",
    "TareWeight"
})
public class OtmEquipment {

    @JsonProperty("EquipmentInitialNumber")
    public String otmEquipmentInitialNumber;
    @JsonProperty("EquipmentOwner")
    public String otmEquipmentOwner;
    @JsonProperty("FlexFieldStrings")
    public String otmFlexFieldStrings;
    @JsonProperty("OwnershipCode")
    public String otmOwnershipCode;
    @JsonProperty("EquipmentName")
    public String otmEquipmentName;
    @JsonProperty("OwnerType")
    public String otmOwnerType;
    @JsonProperty("DateBuilt")
    public OtmDateBuilt otmDateBuilt;
    @JsonProperty("EquipmentTypeGid")
    public GidHolder otmEquipmentTypeGid;
    @JsonProperty("EquipmentNumber")
    public Integer otmEquipmentNumber;
    @JsonProperty("EquipmentGid")
    public GidHolder otmEquipmentGid;
    @JsonProperty("EquipmentInitial")
    public String otmEquipmentInitial;
    @JsonProperty("TransactionCode")
    public String otmTransactionCode;
    @JsonProperty("IsContainer")
    public String otmIsContainer;
    @JsonProperty("FlexFieldNumbers")
    public String otmFlexFieldNumbers;
    @JsonProperty("FlexFieldDates")
    public String otmFlexFieldDates;
    @JsonProperty("TareWeight")
    public OtmTareWeight otmTareWeight;
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
