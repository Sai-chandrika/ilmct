
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "travelTime",
    "distance",
    "trafficTime",
    "flags",
    "_type",
    "text",
    "baseTime"
})
@Getter
public class Summary {

	// Total travel time
    @JsonProperty("travelTime")
    public Integer travelTime;
    @JsonProperty("distance")
    public Integer distance;
    // Total travel time with traffic cosideration
    @JsonProperty("trafficTime")
    public Integer trafficTime;
    @JsonProperty("flags")
    public List<String> flags = null;
    @JsonProperty("_type")
    public String type;
    @JsonProperty("text")
    public String text;
    @JsonProperty("baseTime")
    public Integer baseTime;

}
