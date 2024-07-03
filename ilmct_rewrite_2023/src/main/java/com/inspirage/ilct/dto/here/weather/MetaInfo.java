
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "moduleVersion",
    "availableMapVersion",
    "mapVersion",
    "interfaceVersion",
    "timestamp"
})
public class MetaInfo {

    @JsonProperty("moduleVersion")
    public String moduleVersion;
    @JsonProperty("availableMapVersion")
    public List<String> availableMapVersion = null;
    @JsonProperty("mapVersion")
    public String mapVersion;
    @JsonProperty("interfaceVersion")
    public String interfaceVersion;
    @JsonProperty("timestamp")
    public String timestamp;

}
