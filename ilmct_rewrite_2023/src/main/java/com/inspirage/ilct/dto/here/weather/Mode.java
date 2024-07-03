
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "transportModes",
    "feature",
    "trafficMode",
    "type"
})
public class Mode {

    @JsonProperty("transportModes")
    public List<String> transportModes = null;
    @JsonProperty("feature")
    public List<Object> feature = null;
    @JsonProperty("trafficMode")
    public String trafficMode;
    @JsonProperty("type")
    public String type;

}
