
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspirage.ilct.dto.GidHolder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DurationUOMGid",
    "DurationValue"
})
public class Duration {

    @JsonProperty("DurationUOMGid")
    public GidHolder durationUOMGid;
    @JsonProperty("DurationValue")
    public Integer durationValue;

}
