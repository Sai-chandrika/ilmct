package com.inspirage.ilct.dto;

import com.inspirage.ilct.documents.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageShipmentBean {

    private String loadID;
    private Double weightPercentage;

    private Double distanceTravelledInKms;

    private Double distancePendingInKms;

    private String status;
    private ShipmentContainer container;

    private List<LoadReference> loadReferences = new ArrayList<>();

    private List<FileDocument> fileDocuments = new ArrayList<>();

    private List<ShipmentStopV2> stops = new ArrayList<>();
    private String ttURL;

    private ShipmentLocation source;
    //
    private ShipmentLocation destination;

    private String mode;

    private List<OrderV2> orderV2s = new ArrayList<>();
}
