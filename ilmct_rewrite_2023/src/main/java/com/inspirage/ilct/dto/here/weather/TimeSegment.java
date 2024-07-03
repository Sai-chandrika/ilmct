
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value",
    "segment",
    "otherAttributes",
    "day_of_week"
})
public class TimeSegment {

    @JsonProperty("value")
    private String value;

    @JsonProperty("segment")
    private String segment;

    @JsonProperty("otherAttributes")
    private OtherAttributes otherAttributes;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
