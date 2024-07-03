
package com.inspirage.ilct.dto.here.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mappedPosition",
    "linkId",
    "spot",
    "shapeIndex",
    "label",
    "type",
    "sideOfStreet",
    "mappedRoadName",
    "originalPosition"
})
public class Start {

    @JsonProperty("mappedPosition")
    public MappedPosition mappedPosition;
    @JsonProperty("linkId")
    public String linkId;
    @JsonProperty("spot")
    public Double spot;
    @JsonProperty("shapeIndex")
    public Integer shapeIndex;
    @JsonProperty("label")
    public String label;
    @JsonProperty("type")
    public String type;
    @JsonProperty("sideOfStreet")
    public String sideOfStreet;
    @JsonProperty("mappedRoadName")
    public String mappedRoadName;
    @JsonProperty("originalPosition")
    public MappedPosition originalPosition;

}
