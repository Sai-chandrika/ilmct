package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.PalletContentDTO;
import com.inspirage.ilct.dto.PalletDTO;
import com.inspirage.ilct.dto.PalletsDTO;
import com.inspirage.ilct.dto.StopContentDTO;
import com.inspirage.ilct.service.CacheService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentStopContent {
    private String ActivityType;
    private List<Pallet> Pallet = new ArrayList<>();

    public static ShipmentStopContent toShipmentStopContent(StopContentDTO stopContentDTO, CacheService cacheService, PalletsDTO palletsDTO){
        ShipmentStopContent shipmentStopContent = new ShipmentStopContent();
        shipmentStopContent.setActivityType(stopContentDTO.getActivityType());
        if(stopContentDTO.getPallet() != null && !stopContentDTO.getPallet().getPalletID().isEmpty()){
            for(String palletId : stopContentDTO.getPallet().getPalletID()){
                PalletDTO palletDTO = palletsDTO.getPallet().stream().
                        filter(p -> p.getPalletID().equalsIgnoreCase(palletId)).
                        findFirst().orElse(null);
                if(palletDTO != null ){
                    Pallet pallet = new Pallet();
                    pallet.setSourceLocationID(palletDTO.getSourceLocationID());
                    pallet.setDestLocationID(palletDTO.getDestLocationID());
                    pallet.setPalletID(palletId);
                    pallet.setContainerID(palletDTO.getContainerID());
                    if(palletDTO.getOrder() != null) {
                        pallet.getOrder().addAll(palletDTO.getOrder().getOrderID());
                    }
                    List<PalletContentDTO> palletContentDTOS =  palletDTO.getPalletContent();
                    if(!palletContentDTOS.isEmpty()){
                        for(PalletContentDTO palletContentDTO : palletContentDTOS){
                            PalletContent palletContent = new PalletContent();
                            palletContent.setPalletId(palletId);
                            palletContent.setProductID(palletContentDTO.getProductID());
                            palletContent.setProductName(palletContentDTO.getProductName());
                            palletContent.setQuantity(palletContentDTO.getQuantity());
                            palletContent.setMaterialReference(palletContentDTO.getMaterialReference());
                            palletContent.setNetVolume(palletContentDTO.getNetVolume());
                            palletContent.setNetVolumeUOM(palletContentDTO.getNetVolumeUOM());
                            palletContent.setNetWeight(palletContentDTO.getNetWeight());
                            palletContent.setNetWeightUOM(palletContentDTO.getNetWeightUOM());
                            pallet.getPalletContents().add(palletContent);
                        }
                    }
                    shipmentStopContent.getPallet().add(pallet);
                }
            }
        }
        return shipmentStopContent;
    }
}
