package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "FeedReferenceType",
        "content"

})
@Setter
@Getter
@ToString
public class FeedHeaderDTO {
    private String FeedSource;

    private String FeedGenDtTime;

    private FeedReferenceDTO FeedReference;
}
