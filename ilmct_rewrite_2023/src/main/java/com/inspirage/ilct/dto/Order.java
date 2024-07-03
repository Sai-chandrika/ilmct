package com.inspirage.ilct.dto;

public class Order {

    private String OrderID;

    private String GlobalID;

    private String BN;

    private LocationDTO ShipFromLocationRef;

    private LocationDTO ShipToLocationRef;

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getGlobalID() {
        return GlobalID;
    }

    public void setGlobalID(String globalID) {
        GlobalID = globalID;
    }

    public String getBN() {
        return BN;
    }

    public void setBN(String BN) {
        this.BN = BN;
    }

    public LocationDTO getShipFromLocationRef() {
        return ShipFromLocationRef;
    }

    public void setShipFromLocationRef(LocationDTO shipFromLocationRef) {
        ShipFromLocationRef = shipFromLocationRef;
    }

    public LocationDTO getShipToLocationRef() {
        return ShipToLocationRef;
    }

    public void setShipToLocationRef(LocationDTO shipToLocationRef) {
        ShipToLocationRef = shipToLocationRef;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OrderID='" + OrderID + '\'' +
                ", GlobalID='" + GlobalID + '\'' +
                ", BN='" + BN + '\'' +
                ", ShipFromLocationRef=" + ShipFromLocationRef +
                ", ShipToLocationRef=" + ShipToLocationRef +
                '}';
    }
}
