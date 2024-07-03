package com.inspirage.ilct.dto.bean;

import com.inspirage.ilct.documents.LocationDoc;
import com.inspirage.ilct.documents.Pallet;
import com.inspirage.ilct.documents.PalletContent;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.dto.MaterialReferenceDTO;
import com.inspirage.ilct.util.Utility;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ShipmentTrackItemBeanV2 {

    private String itemId = "";
    private String itemName = "";
    private String itemDescription = "";
    private Double weight;
    private String weightMetric;
    private Double volume;
    private String volumeMetric;
    private String customerItemNumber = "";
    private Integer itemQuantity;
    private String commodity = "";
    private String deliveryLocation;
    private String palletCount;

    public static List<ShipmentTrackItemBeanV2> toShipmentTrackItemBeanV2(ShipmentV2 shipment, Pallet pallet, Map<String, LocationDoc> locationDocMap) {
        List<ShipmentTrackItemBeanV2> finalBeans = new ArrayList<>();
        for (PalletContent palletContent : pallet.getPalletContents()) {
            ShipmentTrackItemBeanV2 bean = new ShipmentTrackItemBeanV2();
            bean.setWeight(Double.parseDouble(palletContent.getNetWeight()));
            bean.setVolume(Double.parseDouble(palletContent.getNetVolume()));
            bean.setItemQuantity(Integer.parseInt(palletContent.getQuantity()));
            bean.setWeightMetric(palletContent.getNetWeightUOM());
            bean.setVolumeMetric(palletContent.getNetVolumeUOM());
            bean.setItemId(palletContent.getProductID());
            bean.setItemName(palletContent.getProductName());
            bean.setPalletCount(pallet.getPalletID());
            if (!Utility.isEmpty(pallet.getDestLocationID())) {
                LocationDoc locationDoc = locationDocMap.get(pallet.getDestLocationID());
                if (locationDoc != null)
                    bean.setDeliveryLocation(locationDoc.getSiteName());
            } else bean.setDeliveryLocation("");
            for (MaterialReferenceDTO materialReference : palletContent.getMaterialReference()) {
                if (materialReference != null && materialReference.getMaterialReferenceType() != null) {
                    if (materialReference.getMaterialReferenceType().equalsIgnoreCase("CustomerItemNumber")) {
                        bean.setCommodity(materialReference.getContent());
                    }
                    if (materialReference.getMaterialReferenceType().equalsIgnoreCase("Commodity")) {
                        bean.setCommodity(materialReference.getContent());
                    }
                }
            }
            finalBeans.add(bean);
        }
        return finalBeans;
    }
}
