package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspirage.ilct.dto.otm.shipment.GidHolder;

public class ParcelRefNumQualifierGidDto {
    @JsonProperty("Gid")
    private GidHolder gid;
}
