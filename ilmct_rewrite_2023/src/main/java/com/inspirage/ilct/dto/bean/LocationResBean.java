package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inspirage.ilct.documents.LocationDoc;
import com.inspirage.ilct.documents.ShipmentTrackingV2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LocationResBean {
    private double lat, lng;
    private String siteId;
    private String name, city, province;
    private String locationType;
    private String locationId;
    private String carrierId;
    private String containerId;
    private String status;
    private String pickupDate;
    private String deliveryDate;
    private String locSiteId;
    private GeofenceBean geofenceBean;
    private String carrierName;

    public LocationResBean(ShipmentTrackingV2 bean, LocationDoc doc, String locationType) {
        setSiteId(bean.getLoadId());
        setLocSiteId(doc.getSiteId());
        setLocationType(locationType);
        if (doc.getLocation() != null) {
            setLat(doc.getLocation().getX());
            setLng(doc.getLocation().getY());
        }
        setName(doc.getSiteName());
        setCity(doc.getCity());
        setProvince(doc.getProvince());
        this.carrierId = bean.getCarrierId();
        this.containerId = bean.getContainerNumber();
        this.status = bean.getStatus();
        this.pickupDate = bean.getPickupEta();
        this.deliveryDate = bean.getDeliveryEta();
        this.carrierName = bean.getCarrierName();
        this.locationId = doc.getId();
    }
}
