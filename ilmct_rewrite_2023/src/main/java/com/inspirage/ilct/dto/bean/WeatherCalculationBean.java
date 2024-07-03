package com.inspirage.ilct.dto.bean;

import com.inspirage.ilct.documents.LatLng;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class WeatherCalculationBean {
    private List<String> product = Arrays.asList("alerts", "observation");
    private Double latitude;
    private Double longitude;
    private Boolean oneobservation = true;

    public WeatherCalculationBean(LatLng latLng) {
        this.setLatitude(latLng.getX());
        this.setLongitude(latLng.getY());
    }

}
