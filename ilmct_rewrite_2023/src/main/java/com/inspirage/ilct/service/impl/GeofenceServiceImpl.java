package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.GeofenceBean;
import com.inspirage.ilct.dto.bean.LocationResBean;
import com.inspirage.ilct.dto.response.PageableResponse;
import com.inspirage.ilct.enums.FencingType;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.repo.GeofencingDocRepository;
import com.inspirage.ilct.repo.LocationDocRepository;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.ApplicationSettingsService;
import com.inspirage.ilct.service.GeofenceService;
import com.inspirage.ilct.util.Point;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class GeofenceServiceImpl implements GeofenceService {

    @Autowired
    private ApplicationSettingsService settingsService;

    @Autowired
    LocationDocRepository locationDocRepository;
    @Autowired
    GeofencingDocRepository geofencingDocRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenUtilService tokenUtilService;
    @Override
    public Boolean locateShipmentV2(GeofenceDoc geofenceDoc, LatLng latLng, String inout) throws ApplicationSettingsNotFoundException {
        Boolean pointStatus = Boolean.FALSE;

        if (inout.equalsIgnoreCase("in")) {
            if (geofenceDoc.getFencingType().name().equals(FencingType.CIRCLE.name())) {
                pointStatus = pointInCircleV2(geofenceDoc, latLng.getX(), latLng.getY());
            } else {
                pointStatus = pointInPolygonV2(geofenceDoc, latLng.getX(), latLng.getY());
            }
        } else {
            if (geofenceDoc.getFencingType().name().equals(FencingType.CIRCLE.name())) {
                pointStatus = pointOutCircleV2(geofenceDoc, latLng.getX(), latLng.getY());
            } else {
                pointStatus = pointOutPolygonV2(geofenceDoc, latLng.getX(), latLng.getY());
            }
        }
        return pointStatus;
    }

    private Boolean pointInCircleV2(GeofenceDoc geoFenceDoc, double lat, double lang) {
        double distBetweenPointsInMeters = distBetweenPointsInMeters(geoFenceDoc.getLocation().getX(),
                geoFenceDoc.getLocation().getY(), lat, lang);

        if ((distBetweenPointsInMeters / 1000) <= geoFenceDoc.getRadius()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean pointOutCircleV2(GeofenceDoc geoFenceDoc, double lat, double lang) throws ApplicationSettingsNotFoundException {
        ApplicationSettings applicationSettings = settingsService.getApplicationSetting();
        int dis = applicationSettings.getGeoFenceDistance();

        double distBetweenPointsInMeters = distBetweenPointsInMeters(geoFenceDoc.getLocation().getX(),
                geoFenceDoc.getLocation().getY(), lat, lang);
        double distanceInKms = (distBetweenPointsInMeters / 1000);
        double totalFenceRadius = geoFenceDoc.getRadius() + dis;

        if (distanceInKms <= totalFenceRadius && distanceInKms > geoFenceDoc.getRadius()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean pointInPolygonV2(GeofenceDoc bean, double lat, double lang) {
        Boolean oddNodes = Boolean.FALSE;
        int n = bean.getXyCordsList().size();
        Point[] polygon = new Point[n];
        Point p = new Point(lat, lang);
        for (int i = 0; i < n; i++) {
            polygon[i] = new Point(bean.getXyCordsList().get(i).getX(), bean.getXyCordsList().get(i).getY());
        }
        oddNodes = isInside(polygon, n, p);
        return oddNodes;
    }

    private Boolean pointOutPolygonV2(GeofenceDoc bean, double lat, double lang) throws ApplicationSettingsNotFoundException {
        ApplicationSettings applicationSettings = settingsService.getApplicationSetting();
        double dis = applicationSettings.getGeoFenceDistance();
        Boolean flag = Boolean.FALSE;
        int n = bean.getXyCordsList().size();
        if (n < 3) {
            return Boolean.FALSE;
        }
        for (int i = 0; i < n; i++) {
            double distBetweenPointsInMeters = distBetweenPointsInMeters(bean.getXyCordsList().get(i).getX(),
                    bean.getXyCordsList().get(i).getY(), lat, lang);
            if ((distBetweenPointsInMeters / 1000) <= dis) {
                return Boolean.TRUE;
            }
        }
        return flag;
    }

    public static Float distBetweenPointsInMeters(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (Float) (float) (earthRadius * c);
    }

    public static boolean onSegment(Point p, Point q, Point r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y)
                && q.y >= Math.min(p.y, r.y);
    }

    public static int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0)
            return 0;
        return (val > 0) ? 1 : 2;
    }

    public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;

        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;

        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;

        return o4 == 0 && onSegment(p2, q1, q2);
    }

    public static boolean isInside(Point[] polygon, int n, Point p) {
        int INF = 100000;
        if (n < 3)
            return false;

        Point extreme = new Point(INF, p.y);

        int count = 0, i = 0;
        do {
            int next = (i + 1) % n;
            if (doIntersect(polygon[i], polygon[next], p, extreme)) {
                if (orientation(polygon[i], p, polygon[next]) == 0)
                    return onSegment(polygon[i], p, polygon[next]);

                count++;
            }
            i = next;
        } while (i != 0);

        return (count & 1) == 1;
    }

    @Override
    public ApiResponse saveGeofenceData(HttpServletRequest request, GeofenceBean geofenceBean) {
        if (ObjectUtils.isEmpty(geofenceBean.getLocationDocId())) {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"please provide location");
        }
        Optional<LocationDoc> locationDocOptional = locationDocRepository.findById(geofenceBean.getLocationDocId());
        if (locationDocOptional.isEmpty()) {
            return new ApiResponse(HttpStatus.NOT_ACCEPTABLE,"location does not exist");
        }
        LocationDoc locationDoc = locationDocOptional.get();
        GeofenceDoc geofenceDoc = geofencingDocRepository.findByLocationDocId(geofenceBean.getLocationDocId()).orElse(new GeofenceDoc());
        setGeofenceProperties(geofenceBean, geofenceDoc, locationDoc, new ApiResponse());
        return new ApiResponse(HttpStatus.OK, "Geofencing is saved successfully");
    }

    private void setGeofenceProperties(GeofenceBean gfBean, GeofenceDoc geofenceDoc, LocationDoc locationDoc, ApiResponse apiResponse) {
        geofenceDoc.setFencingType(!ObjectUtils.isEmpty(gfBean.getFencingType()) ? FencingType.valueOf(gfBean.getFencingType()) : null);
        geofenceDoc.setRadius(gfBean.getRadius());
        geofenceDoc.setLocation(locationDoc.getLocation() != null && locationDoc.getLocation().getX() == 0 ? gfBean.getLocation() : locationDoc.getLocation());
        geofenceDoc.setLocationDocId(gfBean.getLocationDocId());
        if (FencingType.POLYGON.name().equals(gfBean.getFencingType())) {
            processPolygonGeofence(gfBean, geofenceDoc, apiResponse);
        } else {
            geofenceDoc.setXyCordsList(new ArrayList<>());
        }
        geofencingDocRepository.save(geofenceDoc);
        locationDocRepository.save(locationDoc);
    }

    private void processPolygonGeofence(GeofenceBean gfBean, GeofenceDoc geofenceDoc, ApiResponse apiResponse) {
        int cordsSize = gfBean.getXyCordsList().size();
        if (((cordsSize % 2) == 0) && cordsSize > 6) {
            List<LatLng> lngList = IntStream.range(0, cordsSize / 2)
                    .mapToObj(i -> new LatLng(gfBean.getXyCordsList().get(2 * i), gfBean.getXyCordsList().get(2 * i + 1)))
                    .toList();
            geofenceDoc.setXyCordsList(lngList);
            geofenceDoc.setRadius(0);
        } else {
            apiResponse.setStatus(HttpStatus.PARTIAL_CONTENT);
            apiResponse.setMessage("please provide proper location");
        }
    }

    @Override
    public ApiResponse getLocations(int pageIndex, int numberOfRecord, String searchText, HttpServletRequest request) {
        String userId = tokenUtilService.getUserId(request);
        if (Utility.isEmpty(userId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message");
        }
        User user = userRepository.findOneByUserId(userId).orElse(new User());
        List<String> locationIdList = Utility.getUserLocations(user, locationDocRepository);
        Query query = buildQuery(user, searchText, locationIdList);
        query.with(Sort.by(Sort.Direction.ASC, "siteName"));

        long totalRecords = mongoTemplate.count(query, LocationDoc.class);

        if (Utility.isEmpty(searchText)) {
            query.with(Utility.pageable(pageIndex, numberOfRecord));
        }
        List<LocationResBean> locationsList = fetchLocations(query);

        return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                .withMessage("Success")
                .withData(locationsList)
                .withPageInfo(pageIndex, numberOfRecord, totalRecords)
                .build();
    }

    private Query buildQuery(User user, String searchText, List<String> locationIdList) {
        Query query = new Query();

        if (user.getRole().equals(RoleType.PLANNER)) {
            buildPlannerQuery(query, searchText, locationIdList);
        } else {
            buildNonPlannerQuery(query, searchText);
        }

        return query;
    }

    private void buildPlannerQuery(Query query, String searchText, List<String> locationIdList) {
        if (!ObjectUtils.isEmpty(searchText)) {
            query.addCriteria(new Criteria().andOperator(
                    new Criteria().orOperator(Criteria.where("siteId").regex(searchText, "i"),
                            Criteria.where("siteName").regex(searchText, "i")),
                    new Criteria().orOperator(Criteria.where("siteId").in(locationIdList),
                            Criteria.where("siteName").in(locationIdList))));
        }

        if (ObjectUtils.isEmpty(searchText) && !locationIdList.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(Criteria.where("siteId").in(locationIdList),
                    Criteria.where("siteName").in(locationIdList)));
        }
    }

    private void buildNonPlannerQuery(Query query, String searchText) {
        if (!ObjectUtils.isEmpty(searchText)) {
            query.addCriteria(new Criteria().orOperator(Criteria.where("siteId").regex(searchText, "i"),
                    Criteria.where("siteName").regex(searchText, "i")));
        }
    }

    private List<LocationResBean> fetchLocations(Query query) {
        return mongoTemplate.find(query, LocationDoc.class)
                .stream().filter(locationDoc -> !ObjectUtils.isEmpty(locationDoc.getSiteId())).map(locationDoc -> {
                    GeofenceDoc geoFenceDoc = geofencingDocRepository.findByLocationDocId(locationDoc.getId()).orElse(new GeofenceDoc());
                    return mapToDataLocationResBean(locationDoc, geoFenceDoc);
                }).collect(Collectors.toList());
    }

    private LocationResBean mapToDataLocationResBean(LocationDoc locationDoc, GeofenceDoc geoFenceDoc) {
        LocationResBean locationResBean = new LocationResBean();
        locationResBean.setName(locationDoc.getSiteName());
        locationResBean.setCity(locationDoc.getCity());
        locationResBean.setLat(locationDoc.getLocation().getX());
        locationResBean.setLng(locationDoc.getLocation().getY());
        locationResBean.setLocationId(locationDoc.getId());
        locationResBean.setSiteId(locationDoc.getSiteId());
        locationResBean.setLocSiteId(locationDoc.getSiteId());
        locationResBean.setGeofenceBean((geoFenceDoc != null) ? mapToDataGeofenceBean(geoFenceDoc) : new GeofenceBean());
        return locationResBean;
    }

    private GeofenceBean mapToDataGeofenceBean(GeofenceDoc geoFenceDoc) {
        GeofenceBean bean = new GeofenceBean();
        bean.setFencingType(!ObjectUtils.isEmpty(geoFenceDoc.getFencingType())? geoFenceDoc.getFencingType().name() : null);
        bean.setLocation((geoFenceDoc.getLocation() != null) ? new LatLng(geoFenceDoc.getLocation().getX(), geoFenceDoc.getLocation().getY()) : new LatLng());
        bean.setRadius(geoFenceDoc.getRadius());
        bean.setXyCordsList(geoFenceDoc.getXyCordsList().stream().flatMap(latLng -> Stream.of(latLng.getX(), latLng.getY())).collect(Collectors.toList()));
        bean.setLocationDocId(geoFenceDoc.getLocationDocId());
        return bean;
    }

}
