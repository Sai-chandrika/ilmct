package com.inspirage.ilct.documents;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pallet {
    private String palletID;
    private String sourceLocationID;
    private String destLocationID;
    private String containerID;
    private List<String> Order = new ArrayList<>();
    private List<PalletContent> PalletContents = new ArrayList<PalletContent>();
}
