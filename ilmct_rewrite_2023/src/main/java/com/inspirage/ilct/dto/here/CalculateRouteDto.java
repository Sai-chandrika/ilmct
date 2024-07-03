
package com.inspirage.ilct.dto.here;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspirage.ilct.dto.here.weather.Response;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "response"
})
@Getter
@Setter
public class CalculateRouteDto {

    @JsonProperty("response")
    public Response response;
}
