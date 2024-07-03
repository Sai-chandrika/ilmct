
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "timeSegment",
    "type",
    "description"
})
public class Alert {

    @JsonProperty("timeSegment")
    private List<TimeSegment> timeSegment = null;

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
