
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "metaInfo",
    "route",
    "language"
})
@Getter
@Setter
public class Response {

    @JsonProperty("metaInfo")
    public MetaInfo metaInfo;
    @JsonProperty("route")
    public List<Route> route = null;
    @JsonProperty("language")
    public String language;

}
