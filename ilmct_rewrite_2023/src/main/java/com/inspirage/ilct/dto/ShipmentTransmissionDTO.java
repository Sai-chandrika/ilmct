package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author hari
 * @ProjectName ilct
 * @since 31-10-2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Feed"
})
@Setter
@Getter
@ToString
public class ShipmentTransmissionDTO {
    private FeedDTO Feed;
}
