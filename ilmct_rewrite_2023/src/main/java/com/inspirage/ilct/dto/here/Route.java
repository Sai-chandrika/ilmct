
package com.inspirage.ilct.dto.here;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspirage.ilct.dto.here.weather.Leg;
import com.inspirage.ilct.dto.here.weather.Mode;
import com.inspirage.ilct.dto.here.weather.Summary;
import com.inspirage.ilct.dto.here.weather.Waypoint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "waypoint",
    "mode",
    "summary",
    "leg"
})
@Getter
@Setter
public class Route {

    @JsonProperty("waypoint")
    public List<Waypoint> waypoint = null;
    @JsonProperty("mode")
    public Mode mode;
    @JsonProperty("summary")
    public Summary summary;
    @JsonProperty("leg")
    public List<Leg> leg = null;
    @JsonProperty("shape")
    public List<String> shape = null;
}
