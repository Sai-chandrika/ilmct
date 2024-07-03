package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspirage.ilct.documents.Gid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Gid"})
public class OtmShipUnitLineRefnumQualifierGid {
	
	@JsonProperty("Gid")
	public Gid gid;

}
