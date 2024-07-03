package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.LoadMeasureDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoadMeasure {
    private String totalStops;
    private String totalWeight;
    private String totalOrders;
    private String totalWeightUOM;
    private String truckType;
    private String weightUtilization;
    private String volumeUtilization;
    private String eRUUtilization;
    private String totalVolume;
    private String totalVolumeUOM;
    private String palletCount;
    private String materialCount;

    public static LoadMeasure toLoadMeasure(LoadMeasureDTO loadMeasureDTO){
        LoadMeasure loadMeasure = new LoadMeasure();
        if(loadMeasureDTO != null) {
            loadMeasure.setTotalOrders(loadMeasureDTO.getTotalOrders());
            loadMeasure.setTotalStops(loadMeasureDTO.getTotalStops());
            loadMeasure.setTotalWeight(loadMeasureDTO.getTotalWeight());
            loadMeasure.setTotalWeightUOM(loadMeasureDTO.getTotalWeightUOM());
            loadMeasure.setTotalVolume(loadMeasureDTO.getTotalVolume());
            loadMeasure.setTotalVolumeUOM(loadMeasureDTO.getTotalVolumeUOM());
            loadMeasure.setWeightUtilization(loadMeasureDTO.getWeightUtilization());
            loadMeasure.setVolumeUtilization(loadMeasureDTO.getVolumeUtilization());
            loadMeasure.setPalletCount(loadMeasureDTO.getPalletCount());
            loadMeasure.setMaterialCount(loadMeasureDTO.getMaterialCount());
            loadMeasure.setERUUtilization(loadMeasureDTO.getERUUtilization());
            loadMeasure.setTruckType(loadMeasureDTO.getTruckType());
        }
        return loadMeasure;
    }
}
