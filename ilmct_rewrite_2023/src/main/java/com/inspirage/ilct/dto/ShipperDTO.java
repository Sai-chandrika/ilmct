package com.inspirage.ilct.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShipperDTO {
    private String ShipperName;
    private String ShipperPhone;
    private String LocationID;
    private String ShipperEmail;
}
