package com.inspirage.ilct.documents;

import lombok.Data;

@Data
public class ShipmentDateTime {

    private String dateTime;
    private String tZId;

    public ShipmentDateTime(String dateTime, String tZId){
        super();
        this.dateTime = dateTime;
        this.tZId = tZId;
    }

}
