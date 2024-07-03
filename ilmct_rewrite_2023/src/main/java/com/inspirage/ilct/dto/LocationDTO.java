package com.inspirage.ilct.dto;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
public class LocationDTO {
    private String LocationID;

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String locationID) {
        LocationID = locationID;
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
                "LocationID='" + LocationID + '\'' +
                '}';
    }
}
