package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.GeofenceDoc;
import com.inspirage.ilct.documents.LatLng;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.GeofenceBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;


public interface GeofenceService {

	Boolean locateShipmentV2(GeofenceDoc geofenceDoc, LatLng location, String inout) throws ApplicationSettingsNotFoundException;

    ApiResponse saveGeofenceData(HttpServletRequest authentication, GeofenceBean gfBean);

    ApiResponse getLocations(int pageIndex, int numberOfRecord, String searchText, HttpServletRequest request);

}
