
package com.inspirage.ilct.dto.flight;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dateLocal",
    "dateUtc"
})
public class DepartureDate {

    @JsonProperty("dateLocal")
    private String dateLocal;

    @JsonProperty("dateUtc")
    private String dateUtc;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
