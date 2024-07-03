package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "master_truck_types")
public class MasterTruckType extends BaseDoc {
    @Indexed
    private String type;
    private String group;
    private String icon;
    private String redIcon;
    private Boolean isDefault;
    private Double weightPercentage;
    private Double volumePercentage;
    private Double baseTruckWeight;
    private String baseTruckWeightUOM;
    private Double actualTruckWeight;
    private String actualTruckWeightUOM;
}
