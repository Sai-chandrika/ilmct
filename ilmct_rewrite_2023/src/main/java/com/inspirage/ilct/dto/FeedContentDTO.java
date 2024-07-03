package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "Load"
})
@Setter
@Getter
public class FeedContentDTO {
    private LoadDTO Load;
    @Override
    public String toString()
    {
        return "ClassPojo [Load = "+Load+"]";
    }
}
