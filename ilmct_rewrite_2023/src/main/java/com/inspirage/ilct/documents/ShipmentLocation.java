package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentLocation {
    private String siteName;
    private String siteId;
    private String[] coordinates = new String[2];
    private String city;
    private String countryCode;
    private String postalCode;

    public static ShipmentLocation toShipmentLocation(LocationDoc locationDoc){
        ShipmentLocation location = null;
        if(locationDoc != null) {
            location = new ShipmentLocation();
            location.setSiteName(locationDoc.getSiteName());
            location.setCity(locationDoc.getCity());
            location.setSiteId(locationDoc.getSiteId());
            if(locationDoc.getLocation() != null) {
                location.coordinates[0] = locationDoc.getLocation().getY()+"";
                location.coordinates[1] = locationDoc.getLocation().getX()+"";
            }
            location.setCountryCode(locationDoc.getCountryCode());
        }
        return location;
    }
}
