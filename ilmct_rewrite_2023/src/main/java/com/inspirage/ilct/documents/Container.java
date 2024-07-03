package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.otm.OtmSEquipment;
import com.inspirage.ilct.enums.StatusEnum;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "container")
@CompoundIndex(def = "{'status':1,'deliveryPta':-1}")
@EqualsAndHashCode(callSuper = false)
public class Container extends BaseDoc {
    private String domainName;
    private String shipContainerId;
    private String containerInitial;
    @Indexed
    private String containerNumber;
    private String containerRef;
    private List<Integer> sequence;
    private List<Shipment> oldShipments;
    private List<String> shipmentId;
    private List<String> startDate;
    private List<String> endDate;
    private List<String> mode;
    private List<String> carriageType;
    private String vesselNumber;
    private String MMSI;
    private String imoNumber;
    private Set<String> sealSequence;
    private Set<String> sealNumber;
    private StatusEnum status;
    private OtmSEquipment otmSEquipment;
    private String containerStatus;
    private List<ShipmentV2> shipmentsV2;
    private String deliveryPta;
}
