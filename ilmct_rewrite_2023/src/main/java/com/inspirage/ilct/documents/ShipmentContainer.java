package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.ContainerDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentContainer {
    private String id;
    @Indexed
    private String number;
    private String type;
    private String sealNumber;

    public static ShipmentContainer toShipmentContainer(ContainerDTO containerDTO){
        ShipmentContainer shipmentContainer = new ShipmentContainer();
        if(containerDTO != null) {
            shipmentContainer.id = containerDTO.getContainerID();
            shipmentContainer.number = containerDTO.getContainerNumber();
            shipmentContainer.type = containerDTO.getContainerType();
            shipmentContainer.sealNumber = containerDTO.getSealNumber();
        }
        return shipmentContainer;
    }
}
