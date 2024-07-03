
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "travelTime",
    "instruction",
    "length",
    "_type",
    "position",
    "id"
})
public class Maneuver {

    @JsonProperty("travelTime")
    public Integer travelTime;
    @JsonProperty("instruction")
    public String instruction;
    @JsonProperty("length")
    public Integer length;
    @JsonProperty("_type")
    public String type;
    @JsonProperty("position")
    public Position position;
    @JsonProperty("id")
    public String id;
}
