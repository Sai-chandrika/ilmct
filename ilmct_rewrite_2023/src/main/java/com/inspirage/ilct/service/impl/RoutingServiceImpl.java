package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.RoutePointBean;
import com.inspirage.ilct.dto.bean.CalculateRouteBean;
import com.inspirage.ilct.dto.bean.TimeEstimationBean;
import com.inspirage.ilct.dto.here.CalculateRouteDto;
import com.inspirage.ilct.dto.here.weather.Leg;
import com.inspirage.ilct.dto.here.weather.Route;
import com.inspirage.ilct.service.PropertiesService;
import com.inspirage.ilct.service.RoutingService;
import com.inspirage.ilct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.inspirage.ilct.util.Utility.logger;

@Service
public class RoutingServiceImpl implements RoutingService {

    @Autowired(required = false)
    PropertiesService propertiesService;

    private CalculateRouteDto fetchRoute(UriComponentsBuilder builder, MultiValueMap<String, String> map) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<CalculateRouteDto> response = restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.POST, entity, CalculateRouteDto.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (RestClientException restClientException) {
            logger.error("Error : Can not find route between two points " + builder.toUriString());
        } catch (Exception e) {
            logger.error("Error : Can not find route between two points " + builder.toUriString());
        }
        return null;
    }

    @Override
    public TimeEstimationBean calculateRouteAndETAV3(CalculateRouteBean routeBean, DriverRestTimeDoc driverRestDoc,
                                                     ShipmentV2 shipment, boolean isChinaCountry, ApplicationSettings applicationSettings) {

        String hereMapUrl = applicationSettings.getHereMapEtaUrl();

        if (isChinaCountry) {
            hereMapUrl = applicationSettings.getHereMapEtaUrlForChina();
        }
        logger.info("Here Map URL :: " + hereMapUrl);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(hereMapUrl);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        int i = 0;
        try {
            for (LatLng latLng : routeBean.getPointBeans()) {
                int t = i++;
                map.add("waypoint" + (t), "geo!" + latLng.getX() + "," + latLng.getY());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isChinaCountry) {
            builder.queryParam("mode", "fastest;truck;traffic:enabled");
            builder.queryParam("truckType", "truck");

            if (routeBean.getTotalWeightValue() != null) {
                builder.queryParam("weightPerAxle", routeBean.getTotalWeightValue() / 1000);
            }

            if (routeBean.getStartDate() != null) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String input = df.format(routeBean.getStartDate());

                builder.queryParam("departure", input);
            } else
                builder.queryParam("departure", "now");

            if (routeBean.isHazardous())
                builder.queryParam("shippedHazardousGoods", "flammable,harmfulToWater");

            builder.queryParam("app_id", applicationSettings.getHereMap_API_ID()).queryParam("app_code",
                    applicationSettings.getHereMap_API_CODE());
            builder.queryParam("maneuverattributes", "di,sh");
            builder.queryParam("routeattributes", "sh");
        } else {
            builder.queryParam("mode", "fastest;car;traffic:enabled");
            builder.queryParam("app_id", applicationSettings.getHereMap_API_ID_CHINA()).queryParam("app_code",
                    applicationSettings.getHereMap_API_CODE_CHINA());
        }

        CalculateRouteDto routeDto = this.fetchRoute(builder, map);

        if (routeDto != null) {
            LocalDateTime etaDateTime,
                    startDateTime = (routeBean.getStartDate() != null ? routeBean.getStartDate() : LocalDateTime.now());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.dateTimeFormatter1);

            Route route = routeDto.response.route.get(0);

            TimeEstimationBean response = new TimeEstimationBean();
            Map<String, Float> driveRestTimeResponse = driveRestTimeCalculationV2(shipment, driverRestDoc);

            if (routeBean.isOnlySummeryData()) {
                response.setDistance(route.summary.distance / 1000.0);
                int trafficTime = route.summary.trafficTime;
                etaDateTime = startDateTime.plusSeconds(trafficTime);
                response.setEstimatedDate(etaDateTime);
                response.setEstimatedDateTime(etaDateTime.format(formatter));
            } else {

                Leg leg;
                double totalDistance = 0;
                long etaToDestination = 0;
                int routeIndex = 0;
                //variable used to store remain time for next stop
                float timeForNextStop = 0;

                for (RoutePointBean rpb : routeBean.getPointBeans()) {
                    if (routeIndex == 0) {

                        rpb.setDistanceTravelled(0);
                        rpb.setDistanceToTravel(0);
                        rpb.setEstimateTime(0);

                    } else if (route.leg.size() > routeIndex - 1) {

                        leg = route.leg.get(routeIndex - 1);

                        rpb.setDistance(leg.length / 1000.0);
                        rpb.setEstimateTime(leg.travelTime);
                        totalDistance += rpb.getDistance();
                        etaToDestination += rpb.getEstimateTime();

                        //adding rest time at stop level
                        if (driveRestTimeResponse.containsKey("DRIVE_TIME")
                                && driveRestTimeResponse.containsKey("REST_TIME")) {

                            float timeToCalStopETA = leg.travelTime + timeForNextStop;

                            float mapsTimeInHrs = timeToCalStopETA / (60 * 60);

                            long noOfBadges = (long) ((mapsTimeInHrs) / driveRestTimeResponse.get("DRIVE_TIME"));


                            float restTimeCalculated = noOfBadges * driveRestTimeResponse.get("REST_TIME");

                            timeForNextStop = getRestTimeForNextStop(timeToCalStopETA, driveRestTimeResponse, noOfBadges);


                            float restTimeCalculatedInSec = restTimeCalculated * 60 * 60;

                            long stopCalRestTime = (long) restTimeCalculatedInSec;

                            rpb.setEstimateTime(rpb.estimateTime + stopCalRestTime);

                        }
                    }
                    response.getRoutePoints().add(rpb);
                    routeIndex++;
                }

                long hereMapsDriveHrsInSeconds = etaToDestination;

                if (driveRestTimeResponse.containsKey("DRIVE_TIME")
                        && driveRestTimeResponse.containsKey("REST_TIME")) {

                    long calTime = 0;

                    float mapsTimeInHrs = (float) hereMapsDriveHrsInSeconds / (60 * 60);

                    long noOfBadges = (long) (mapsTimeInHrs / driveRestTimeResponse.get("DRIVE_TIME"));


                    float restTimeCalculated = noOfBadges * driveRestTimeResponse.get("REST_TIME");

                    float restTimeCalculatedInSec = restTimeCalculated * 60 * 60;

                    calTime = (long) restTimeCalculatedInSec;

                    etaToDestination += calTime;
                }

                etaDateTime = startDateTime.plusSeconds(etaToDestination);
                response.setDistance(totalDistance);
                response.setEstimatedDate(etaDateTime);
                response.setEstimatedDateTime(etaDateTime.format(formatter));
            }
            //logger.info("Here Map Response :: " + response);
            return response;
        }
        return null;

    }

    public float getRestTimeForNextStop(float totalTime, Map<String, Float> driverTimeMap, long noOfBadges) {

        float driveTime = driverTimeMap.get("DRIVE_TIME_SEC");

        return totalTime - (noOfBadges * driveTime);
    }

    public Map<String, Float> driveRestTimeCalculationV2(ShipmentV2 shipment, DriverRestTimeDoc driverRestDoc) {
        Map<String, Float> response = new HashMap<>();
        if (driverRestDoc != null) {
            if (shipment.getLoadReferences() != null && !shipment.getLoadReferences().isEmpty() &&
                    shipment.getLoadReferences().stream().anyMatch(loadReference ->
                            loadReference.getLoadReferenceType().equals("HOS") && loadReference.getContent() != null && loadReference.getContent().equals("true"))) {

                int driveHrs = driverRestDoc.getDriveHrs();
                int driveMins = driverRestDoc.getDriveMins();

                int restHrs = driverRestDoc.getRestHrs();
                int restMins = driverRestDoc.getRestMins();

                float driverTime = driveHrs + ((float) driveMins / 60);

                float restTime = restHrs + ((float) restMins / 60);

                response.put("DRIVE_TIME", driverTime);
                response.put("REST_TIME", restTime);

                float driveTimeSec = (driveHrs * 60 + driveMins) * 60;
                float restTimeSec = (restHrs * 60 + restMins) * 60;

                response.put("DRIVE_TIME_SEC", driveTimeSec);
                response.put("REST_TIME_SEC", restTimeSec);
            }
        }
        return response;
    }

    @Override
    public double calculateBearing(double lat1, double long1, double lat2, double long2) {
        try {
            double degToRad = Math.PI / 180.0;
            double phi1 = lat1 * degToRad;
            double phi2 = lat2 * degToRad;
            double lam1 = long1 * degToRad;
            double lam2 = long2 * degToRad;

            return Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2),
                    Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)) * 180
                    / Math.PI;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
