package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LatLng {
    private double y,x;

    public LatLng(double x, double y) {
        this.x=x;
        this.y=y;
    }
    public LatLng() {
        super();
    }
}
