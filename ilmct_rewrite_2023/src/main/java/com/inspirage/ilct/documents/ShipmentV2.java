package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.here.weather.Alerts;
import com.inspirage.ilct.enums.StatusEnum;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
@Document(collection = "ShipmentV2")
@CompoundIndex(def = "{'mode':1,'status':1,'lastUpdated':-1}", name = "shipment_collection_1")
@EqualsAndHashCode(callSuper = false)
public class ShipmentV2 extends BaseDoc {

    private FeedReference feedReference;
    private String name;
    @Indexed
    private String loadID;
    private String loadType;
    private String callBackRequired;
    private String languagePreference;
    private String trackingProvider;
    private String mode;
    private List<LoadReference> loadReferences = new ArrayList<>();
    private ShipmentLocation source;
    private ShipmentLocation destination;
    private ShipmentDateTime startDate;
    private ShipmentDateTime endDate;
    private LoadResponsibleParty shipper;
    private LoadResponsibleParty forwarder;
    private LoadResponsibleParty consignee;
    private LoadMeasure loadMeasure;
    private ShipmentContainer container;
    private List<ShipmentStopV2> stops = new ArrayList<>();
    private List<String> orders = new ArrayList<>();
    private Boolean isChinaShipment = Boolean.FALSE;
    private LocalDateTime lastGVITEventTriggeredOn;
    private String ttURL;
    @Indexed
    private StatusEnum status;
    private String loadClosedOn;
    private String closedBy;
    private Double sToDDirectionAngle;
    private Double distanceTravelledInKms;
    private Double distancePendingInKms;
    private String carrierName;
    private String carrierID;
    private String containerTracking;
    private String partition;
    private Alerts weatherAlert;
    private List<String> uploadDocuments = new ArrayList<>();
    private String currentSpeed;
    private Integer currentFuelInLtr;
    private String currentFuel;
    private String currentTemp;
    private int avgSpeed;
    private String speedUnit;
    private StatusEnum typeOfDelivery;
    private LatLng current;
    private Double weightPercentage;
    private Double volumePercentage;
    private List<FileDocument> fileDocuments = new ArrayList<>();
    private List<String> specialServices = new ArrayList<>();
    private Boolean isTranslated;
}
