package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderV2 {

    private String orderId;

    private String globalId;

    private String bn;

    private String shipFromLocationId;

    private String shipToLocationId;

    private String ttUrl;

}
