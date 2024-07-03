package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        "FeedContent",
        "FeedHeader"
})
@Setter
@Getter
public class FeedDTO {
    private FeedContentDTO FeedContent;

    private FeedHeaderDTO FeedHeader;

    public String toString()
    {
        return "ClassPojo [FeedContent = "+FeedContent+", FeedHeader = "+FeedHeader+"]";
    }
}
