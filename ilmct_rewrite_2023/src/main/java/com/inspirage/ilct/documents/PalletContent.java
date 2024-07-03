package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.MaterialReferenceDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PalletContent {
    private String palletId;
    private String productName;
    private String netVolume;
    private MaterialReferenceDTO[] materialReference;
    private String netWeightUOM;
    private String netVolumeUOM;
    private String quantity;
    private String productID;
    private String netWeight;
}
