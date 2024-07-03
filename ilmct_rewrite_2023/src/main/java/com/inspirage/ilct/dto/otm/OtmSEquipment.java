
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "SEquipmentGid", "Equipment", "EquipmentInitial", "EquipmentGroupGid", "EquipmentTypeGid",
		"EquipmentNumber", "EquipmentInitialNumber", "SEquipmentSeal" })
public class OtmSEquipment {

	@JsonProperty("SEquipmentGid")
	public GidHolder otmSEquipmentGid;
	@JsonProperty("Equipment")
	public OtmEquipment otmEquipment;
	@JsonProperty("EquipmentInitial")
	public String otmEquipmentInitial;
	@JsonProperty("EquipmentGroupGid")
	public GidHolder otmEquipmentGroupGid;
	@JsonProperty("EquipmentTypeGid")
	public GidHolder otmEquipmentTypeGid;
	@JsonProperty("EquipmentNumber")
	public String otmEquipmentNumber;

	// adding new property
	@JsonProperty("EquipmentInitialNumber")
	public String otmEquipmentInitialNumber;

	// adding new property
	@JsonProperty("SEquipmentSeal")
	public OtmSEquipmentSeal otmEquipmentSeal;

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
