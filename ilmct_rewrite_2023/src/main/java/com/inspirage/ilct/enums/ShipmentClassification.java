package com.inspirage.ilct.enums;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
public enum ShipmentClassification {
    INBOUND("Inbound"),OUTBOUND("Outbound");

    ShipmentClassification(String val){
        this.val=val;
    }
    private final String val;
}
