
package com.inspirage.ilct.dto.flight;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fs",
    "iata",
    "icao",
    "name",
    "active"
})
public class Airline {

    @JsonProperty("fs")
    private String fs;

    @JsonProperty("iata")
    private String iata;

    @JsonProperty("icao")
    private String icao;

    @JsonProperty("name")
    private String name;

    @JsonProperty("active")
    private Boolean active;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
