
package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.geo.Distance;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Distance"
})
public class DistFromPrevStop {

    @JsonProperty("Distance")
    public Distance distance;

}
