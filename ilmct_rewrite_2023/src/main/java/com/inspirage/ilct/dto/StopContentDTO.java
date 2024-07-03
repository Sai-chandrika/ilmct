package com.inspirage.ilct.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class StopContentDTO {
    private String ActivityType;
    private StopContentPalletDTO Pallet;
}
