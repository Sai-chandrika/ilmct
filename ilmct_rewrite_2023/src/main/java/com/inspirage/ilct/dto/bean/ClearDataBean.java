package com.inspirage.ilct.dto.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClearDataBean {

    //Shipment status
    public String truckStatusClearTill;
    public long numberOfTruckStatus;

    //Shipment transmission logs
    public String transmissionLogClearTill;
    public long numberOfTransmissionLog;

    //Shipments
    public String shipmentClearTill;
    public long numberOfShipmentClear;

    //Flight status
    public String flightStatusClearTill;
    public long numberOfFlightStatus;

}
