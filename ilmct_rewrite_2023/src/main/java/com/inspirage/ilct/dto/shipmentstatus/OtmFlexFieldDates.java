
package com.inspirage.ilct.dto.shipmentstatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "AttributeDate2" })
public class OtmFlexFieldDates {

	@JsonProperty("AttributeDate2")
	public AttributeDate2 AttributeDate2;

}
