package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParcelRefNumDto {

    @JsonProperty("RefnumQualifierGid")
    public ParcelRefNumQualifierGidDto refNumQualifierGid;

    @JsonProperty("RefnumValue")
    public String refNumValue;
}
