package com.inspirage.ilct.dto;

import com.inspirage.ilct.documents.LatLng;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RoutePointBean extends LatLng {

    private Integer sequence;
    public double distance;
    public double distanceTravelled;
    public double distanceToTravel;
    public long estimateTime;

    public RoutePointBean(Integer stopSequence, LatLng latLng) {
 	  super(latLng.getX(), latLng.getY());
        this.sequence = stopSequence;
    }
}
