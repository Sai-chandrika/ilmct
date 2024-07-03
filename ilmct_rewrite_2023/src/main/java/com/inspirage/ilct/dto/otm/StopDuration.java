
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Duration"
})
public class StopDuration {

    @JsonProperty("Duration")
    public Duration duration;

}
