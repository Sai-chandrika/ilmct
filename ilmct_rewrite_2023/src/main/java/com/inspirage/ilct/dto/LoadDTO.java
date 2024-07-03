package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inspirage.ilct.dto.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIgnoreProperties
public class LoadDTO {
    private String Partition;

    private DestinationDTO Destination;

    private String CarrierName;

    private String TrackingProvider;

    private String Mode;

    private String LoadName;

    private String CallBackRequired;

    private ShipmentDateTimeDTO EndDate;

    private PalletsDTO Pallets;

    private SourceDTO Source;

    private ForwarderDTO Forwarder;

    private ShipmentDateTimeDTO StartDate;

    private String ContainerTracking;

    private ConsigneeDTO Consignee;

    private String CarrierID;

    private ContainerDTO Container;

    private MasterLoadReferenceDTO LoadReferences;

    private SpecialServicesDTO SpecialServices;

    private String LanguagePreference;

    private ShipperDTO Shipper;

    private StopsDTO Stops;

    private OrdersDTO Orders;

    private String LoadID;

    private String ShipType;

    private LoadMeasureDTO LoadMeasure;

}
