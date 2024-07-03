
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "travelTime",
    "start",
    "length",
    "end",
    "maneuver"
})
public class Leg {

    @JsonProperty("travelTime")
    public Integer travelTime;
    @JsonProperty("start")
    public Start start;
    @JsonProperty("length")
    public Double length;
    @JsonProperty("end")
    public End end;
    @JsonProperty("maneuver")
    public List<Maneuver> maneuver = null;

}
