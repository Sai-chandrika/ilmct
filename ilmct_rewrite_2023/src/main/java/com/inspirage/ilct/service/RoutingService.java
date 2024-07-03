package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.documents.DriverRestTimeDoc;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.dto.bean.CalculateRouteBean;
import com.inspirage.ilct.dto.bean.TimeEstimationBean;

public interface RoutingService {

	TimeEstimationBean calculateRouteAndETAV3(CalculateRouteBean routeBean, DriverRestTimeDoc driverRestDoc,
                                              ShipmentV2 shipment, boolean isChinaCountry, ApplicationSettings applicationSettings);

    double calculateBearing(double lat1, double long1, double lat2, double long2);
}

