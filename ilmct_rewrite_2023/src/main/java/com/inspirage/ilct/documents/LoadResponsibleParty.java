package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoadResponsibleParty {
    private String name;
    private String email;
    private String phone;
    private ShipmentLocation location;
}
