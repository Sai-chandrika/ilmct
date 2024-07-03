package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ShipUnitLineRefnumQualifierGid","ShipUnitLineRefnumValue"})
public class OtmShipUnitLineRefnum {
	
	@JsonProperty("ShipUnitLineRefnumQualifierGid")
	public OtmShipUnitLineRefnumQualifierGid ShipUnitLineRefnumQualifierGid;
	
	@JsonProperty("ShipUnitLineRefnumValue")
	public String  shipUnitLineRefnumValue;
	

}
