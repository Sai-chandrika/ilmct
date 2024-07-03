package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.bean.*;
import com.inspirage.ilct.dto.here.weather.Alerts;
import com.inspirage.ilct.dto.*;
import com.inspirage.ilct.dto.here.weather.Observations;
import com.inspirage.ilct.dto.here.weather.WeatherAlertDTO;
import com.inspirage.ilct.dto.shipmentstatus.*;
import com.inspirage.ilct.dto.shipmentstatus.OtmEventDt;
import com.inspirage.ilct.enums.ActionEnum;
import com.inspirage.ilct.enums.FencingEventType;
import com.inspirage.ilct.enums.MessageTypeEnum;
import com.inspirage.ilct.enums.StatusEnum;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.repo.*;
import com.inspirage.ilct.service.*;
import com.inspirage.ilct.util.CommonUtil;
import com.inspirage.ilct.util.Constants;
import com.inspirage.ilct.util.DateUtil;
import com.inspirage.ilct.util.Utility;
import com.inspirage.ilct.xml.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import static com.inspirage.ilct.util.Constants.*;

@Service
@Slf4j
public class OtmServiceV3Impl implements OtmServiceV3 {
    @Autowired
    ShipmentV2Repository shipmentV2Repository;
    @Autowired
    ApplicationSettingsRepository applicationSettingsRepository;
    @Autowired
    LocationDocRepository locationDocRepository;
    @Autowired
    CacheService cacheService;
    @Autowired
    OrdersV2Repository ordersV2Repository;
    @Autowired
    MasterTruckTypesRepository masterTruckTypesRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    DriverRestTimeRepository driverRestTimeRepository;
    @Autowired
    RoutingService routingService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    TokenUtilService tokenUtilService;
    @Autowired
    FileManagementService fileManagementService;

    private final Logger logger = LoggerFactory.getLogger(OtmServiceV3Impl.class);
    @Autowired
    ShipmentStatusRepository shipmentStatusRepository;
    @Autowired
    DriverRestTimeRepository driverRepository;

    @Autowired
    D1ProcessedEventRepository d1ProcessedEventRepository;

    @Autowired
    OutBoundEventRepository outBoundEventRepository;

    @Autowired
    GeofencingDocRepository geofencingDocRepository;
    @Autowired
    GeoFenceEventRepository geoFenceEventRepository;

    @Autowired
    GeofenceService geofenceService;

    @Override
    public ApiResponse saveShipmentXml(ShipmentTransmissionDTO transmissionDto, HttpServletRequest request) {
        User loginUser = userRepository.findOneByUserId(tokenUtilService.getUserId(request)).orElse(null);
        LoadDTO loadDTO = transmissionDto.getFeed().getFeedContent().getLoad();
        return saveShipmentV2Mapping(loginUser, loadDTO);
    }

    @Override
    public ApiResponse saveShipmentV2Mapping(User loginUser, LoadDTO loadDTO) {
        try {
            Boolean isIlmctShipment;
            if (loadDTO.getTrackingProvider().trim().equalsIgnoreCase("ILMCT")) {
                isIlmctShipment = Boolean.TRUE;
            } else isIlmctShipment = Boolean.FALSE;
            List<ShipmentV2> shipmentDocs = shipmentV2Repository.findByLoadID(loadDTO.getLoadID());
            Boolean isNewShipment;
            ShipmentV2 shipmentV2 = new ShipmentV2();
            if (!shipmentDocs.isEmpty()) {
                shipmentV2 = shipmentDocs.get(0);
                isNewShipment = Boolean.FALSE;
            } else {
                isNewShipment = Boolean.TRUE;
            }
            ApplicationSettings applicationSettings = applicationSettingsRepository.findAll().get(0);
            dtoToEntity(shipmentV2, loadDTO, isIlmctShipment, isNewShipment, applicationSettings);
            entityToDto(loginUser, shipmentV2, isIlmctShipment, applicationSettings);
            shipmentV2 = shipmentV2Repository.save(shipmentV2);
            logger.info("Shipment Saved Successfully with Load ID :: " + shipmentV2.getLoadID());

            if (isNewShipment) {
                postShipmentData(shipmentV2, applicationSettings);
                shipmentV2Repository.save(shipmentV2);
            }
            if (shipmentV2.getContainerTracking() != null && shipmentV2.getContainerTracking().equalsIgnoreCase("Y")) {
                if (shipmentV2.getContainer() != null && shipmentV2.getContainer().getId() != null && !shipmentV2.getContainer().getId().isEmpty()) {
                    saveContainers(shipmentV2,loginUser);
                }
            }
            logger.info("Shipment Saving Done Returning Response");
            return new ApiResponse(HttpStatus.OK, "saved successfully");
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Failed");
        }
    }

    private void dtoToEntity(ShipmentV2 shipmentV2, LoadDTO loadDTO, Boolean isIlmctShipment, Boolean isNewShipment, ApplicationSettings applicationSettings) {
        try {
            shipmentV2.setLoadID(loadDTO.getLoadID());
            shipmentV2.setName(loadDTO.getLoadName());
            shipmentV2.setCallBackRequired(loadDTO.getCallBackRequired());
            if (loadDTO.getCarrierID() != null) {
                shipmentV2.setCarrierID(loadDTO.getCarrierID());
            }
            shipmentV2.setCarrierName(loadDTO.getCarrierName());
            shipmentV2.setTrackingProvider(loadDTO.getTrackingProvider());
            shipmentV2.setDestination(ShipmentLocation.toShipmentLocation(locationDocRepository.findLocationBySiteId(loadDTO.getDestination().getDestLocationID())));
            shipmentV2.setSource(ShipmentLocation.toShipmentLocation(locationDocRepository.findLocationBySiteId(loadDTO.getSource().getSourceLocationID())));
            shipmentV2.setContainer(loadDTO.getContainer() != null ? ShipmentContainer.toShipmentContainer(loadDTO.getContainer()) : null);
            shipmentV2.setStartDate(new ShipmentDateTime(loadDTO.getStartDate().getDateTime(), loadDTO.getStartDate().getTZId()));
            shipmentV2.setEndDate(new ShipmentDateTime(loadDTO.getEndDate().getDateTime(), loadDTO.getStartDate().getTZId()));
            List<LoadReference> referenceList = new ArrayList<>();
            for (LoadReferenceDTO loadReferenceDTO : loadDTO.getLoadReferences().getLoadReference()) {
                if (loadReferenceDTO != null) {
                    referenceList.add(new LoadReference(loadReferenceDTO.getLoadReferenceType(), loadReferenceDTO.getContent()));
                }
            }
            List<ShipmentStopV2> shipmentStops = new ArrayList<>();
            for (StopDTO stopDTO : loadDTO.getStops().getStop()) {
                shipmentStops.add(ShipmentStopV2.toShipmentStopV2(stopDTO, cacheService, isIlmctShipment, loadDTO.getPallets()));
            }
            if (isNewShipment && loadDTO.getOrders() != null && !loadDTO.getOrders().getOrder().isEmpty()) {
                List<OrderV2Doc> orders = new ArrayList<>();
                for (Order order : loadDTO.getOrders().getOrder()) {
                    OrderV2Doc orderV2Doc;
                    if (order.getOrderID() != null) {
                        orderV2Doc = ordersV2Repository.findByOrderId(order.getOrderID()).orElse(new OrderV2Doc());
                        orderV2Doc.setOrderId(order.getOrderID());
                        if (!Utility.isEmpty(order.getGlobalID())) {
                            orderV2Doc.setGlobalId(order.getGlobalID());
                        }
                        if (!Utility.isEmpty(order.getBN())) {
                            orderV2Doc.setBn(order.getBN());
                        }
                        if (order.getShipFromLocationRef() != null && !Utility.isEmpty(order.getShipFromLocationRef().getLocationID())) {
                            orderV2Doc.setShipFromLocationId(order.getShipFromLocationRef().getLocationID());
                        }
                        if (order.getShipToLocationRef() != null && !Utility.isEmpty(order.getShipToLocationRef().getLocationID())) {
                            orderV2Doc.setShipToLocationId(order.getShipToLocationRef().getLocationID());
                        }
                        if (!orderV2Doc.getShipmentId().contains(shipmentV2.getLoadID())) {
                            orderV2Doc.getShipmentId().add(shipmentV2.getLoadID());
                        }
                        orders.add(orderV2Doc);
                    }
                }
                ordersV2Repository.saveAll(orders);
                shipmentV2.setOrders(orders.stream().map(OrderV2Doc::getOrderId).collect(Collectors.toList()));
            }
            shipmentV2.setLoadReferences(referenceList);
            if (null != loadDTO.getSpecialServices()) {
                shipmentV2.setSpecialServices(loadDTO.getSpecialServices().getSpecialService());
            }
            shipmentV2.setStops(shipmentStops);
            shipmentV2.setLoadMeasure(LoadMeasure.toLoadMeasure(loadDTO.getLoadMeasure()));
            shipmentV2.setIsChinaShipment(Boolean.FALSE);

            if (shipmentV2.getSource().getCountryCode() != null && (shipmentV2.getSource().getCountryCode().equalsIgnoreCase("CN") ||
                    shipmentV2.getSource().getCountryCode().equalsIgnoreCase("CHN"))) {
                shipmentV2.setIsChinaShipment(Boolean.TRUE);
            }
            if (shipmentV2.getId() == null || shipmentV2.getId().isEmpty()) {
                shipmentV2.setStatus(StatusEnum.NOT_STARTED);
                shipmentV2.setCurrent(getCurrentData(cacheService, shipmentV2)); //lat ,lon
            }
            shipmentV2.setLoadType(loadDTO.getShipType());
            if (loadDTO.getLanguagePreference() != null && !loadDTO.getLanguagePreference().isEmpty()) {
                shipmentV2.setLanguagePreference(loadDTO.getLanguagePreference());
            } else {
                shipmentV2.setLanguagePreference("en");
            }
            shipmentV2.setMode(loadDTO.getMode());
            shipmentV2.setCarrierName(loadDTO.getCarrierName());
            shipmentV2.setCarrierID(loadDTO.getCarrierID());
            Optional<LoadReference> containerTracking = referenceList.stream().filter(r -> r.getLoadReferenceType().equalsIgnoreCase("ContainerTracking")).findFirst();
            if (containerTracking.isPresent()) {
                shipmentV2.setContainerTracking(containerTracking.get().getContent());
            } else shipmentV2.setContainerTracking("N");
            shipmentV2.setPartition(loadDTO.getPartition());
        }
        catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
    }

    public LatLng getCurrentData(CacheService locationDocRepository, ShipmentV2 shipmentV2) {
        ShipmentStopV2 shipmentStopV2 = Utility.getFirstStop(shipmentV2);
        assert shipmentStopV2 != null;
        return locationDocRepository.findLocationBySiteId(shipmentStopV2.getLocation().getSiteId()).getLocation();
    }

    private void entityToDto(User loginUser, ShipmentV2 shipmentDoc, Boolean isIlmctShipment, ApplicationSettings
            applicationSettings) {
        Units.TemperatureUnit temperatureUnit = (loginUser == null || loginUser.getTemperatureUnit() == null)
                ? Units.TemperatureUnit.CELSIUS : loginUser.getTemperatureUnit();
        Units.DistanceUnit distanceUnit = (loginUser == null || loginUser.getDistanceUnit() == null)
                ? Units.DistanceUnit.KILOMETERS : loginUser.getDistanceUnit();
        List<ShipmentStopV2> sortedStops = shipmentDoc.getStops();
        sortedStops.sort(Comparator.comparing(ShipmentStopV2::getStopNumber));
        List<ShipmentV2> shipments = new ArrayList<>();
        shipments.add(shipmentDoc);
        Map<String, LocationDoc> locationDocMap = getLocationMapByLoadId(shipments);
        List<MasterTruckType> masterTruckTypeList = masterTruckTypesRepository.findByTypeIn(shipments.stream().filter(shipment -> shipment.getLoadMeasure() != null)
                .map(shipmentV2 -> shipmentV2.getLoadMeasure().getTruckType()).collect(Collectors.toSet()));
        Optional<MasterTruckType> defaultTruckType = masterTruckTypesRepository.findByIsDefault(true);

        List<OrderV2Doc> orderV2List = ordersV2Repository.findAllByShipmentIdIn(shipments.stream().map(ShipmentV2::getLoadID).collect(Collectors.toList()));

        ShipmentTrackingV2 bean = new ShipmentTrackingV2(Utility.getTimeZone(loginUser), temperatureUnit, distanceUnit, shipmentDoc,
                masterTruckTypeList, defaultTruckType.get(), locationDocMap, null, null, orderV2List);
        bean.setEventType(shipmentDoc.getId() == null ? 1 : 2);

        if (shipmentDoc.getMode() != null && (shipmentDoc.getMode().equals(TM_TL) || shipmentDoc.getMode().equals(TM_LTL) || shipmentDoc.getMode().equals(Constants.TM_TRUCK) ||
                shipmentDoc.getMode().equalsIgnoreCase(GROUP_AGE) || shipmentDoc.getMode().equalsIgnoreCase(INTER_MODEL))) {
            RoutePointBean[] waypointsArr = new RoutePointBean[sortedStops.size()];
            int i = 0;
            for (ShipmentStopV2 stop : sortedStops) {
                LocationDoc doc = cacheService.findLocationBySiteId(stop.getLocation().getSiteId());
                if (doc != null && doc.getLocation() != null) {
                    waypointsArr[i++] = new RoutePointBean(stop.getStopNumber(), doc.getLocation());
                }
            }
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("siteId").is(shipmentDoc.getSource().getSiteId()));
            LocationDoc location = mongoTemplate.findOne(query2, LocationDoc.class);
            DriverRestTimeDoc timing = null;
            boolean isChinaCountry = false;
            if (location != null) {
                Optional<CountryDoc> country = countryRepository
                        .findUniqueByIso2CodeOrIso3Code(location.getCountryCode(), location.getCountryCode());
                if (country.isPresent()) {
                    country.get();
                    if (country.get().getIso2Code() != null && country.get().getIso2Code().equalsIgnoreCase("CN") || country.get().getIso3Code().equalsIgnoreCase("CHN")) {
                        isChinaCountry = true;
                    }
                    timing = driverRestTimeRepository.findByCountryAndStatus(country.get(), true);
                    if (timing != null) {
                        logger.info("Driver Rest Time for Country :: " + country.get().getName() + " And Timings are " + timing);
                        new ApiResponse(HttpStatus.OK.value(), country.get().getName());
                        return;
                    }
                }
            }
            ShipmentStopV2 firstStop = sortedStops.stream().filter(ss -> ss.getStopType().equalsIgnoreCase("P")).
                    min(Comparator.comparing(ShipmentStopV2::getStopNumber)).orElse(null);
            LocalDateTime startDate = null;
            if (firstStop != null) {
                startDate = DateUtil.convertDate(firstStop.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, firstStop.getEstimatedArrival().getTZId(), null);
            }
            shipmentDoc.setStops(new ArrayList<>());
            TimeEstimationBean tImeEstimationBean = routingService.calculateRouteAndETAV3(CalculateRouteBean.Builder(Utility.parseDoubleOrNull(shipmentDoc.getLoadMeasure().getTotalWeight()), /*bean.isHazardous()*/true, DateUtil.convertDate(shipmentDoc.getStartDate().getDateTime(), DateUtil.dateTimeFormatter2, shipmentDoc.getStartDate().getTZId(), null), false, waypointsArr), timing, shipmentDoc, isChinaCountry, applicationSettings);

            if (tImeEstimationBean != null) {
                if (isIlmctShipment) {
                    ilmctStopEtaCalculation(shipmentDoc, sortedStops, tImeEstimationBean, startDate, applicationSettings);
                } else {
                    nonIlmctEtaCalculation(shipmentDoc, sortedStops, tImeEstimationBean, startDate, applicationSettings);
                }
                shipmentDoc.setDistanceTravelledInKms(tImeEstimationBean.getDistanceTravelled());
                if (tImeEstimationBean.getDistanceToTravel() > 0) {
                    shipmentDoc.setDistancePendingInKms(tImeEstimationBean.getDistanceToTravel());
                } else {
                    shipmentDoc.setDistancePendingInKms(tImeEstimationBean.getDistance());
                }
            }
        }
    }

    private Map<String, LocationDoc> getLocationMapByLoadId(List<ShipmentV2> shipmentsData) {
        Set<String> siteIds = new HashSet<>();
        Set<String> consigneeSiteIds = shipmentsData.stream().filter(shipmentV2 -> shipmentV2.getConsignee() != null && shipmentV2.getConsignee().getLocation() != null && shipmentV2.getConsignee().getLocation().getSiteId() != null).map(shipmentV2 -> shipmentV2.getConsignee().getLocation().getSiteId()).collect(Collectors.toSet());
        Set<String> shipperSiteIds = shipmentsData.stream().filter(shipmentV2 -> shipmentV2.getShipper() != null && shipmentV2.getShipper().getLocation() != null && shipmentV2.getShipper().getLocation().getSiteId() != null).map(shipmentV2 -> shipmentV2.getShipper().getLocation().getSiteId()).collect(Collectors.toSet());
        Set<String> forwarderSiteIds = shipmentsData.stream().filter(shipmentV2 -> shipmentV2.getForwarder() != null && shipmentV2.getForwarder().getLocation() != null && shipmentV2.getForwarder().getLocation().getSiteId() != null).map(shipmentV2 -> shipmentV2.getForwarder().getLocation().getSiteId()).collect(Collectors.toSet());
        Set<String> sourceSiteIds = shipmentsData.stream().filter(shipmentV2 -> shipmentV2.getSource() != null && shipmentV2.getSource().getSiteId() != null && !shipmentV2.getSource().getSiteId().isEmpty()).map(shipmentV2 -> shipmentV2.getSource().getSiteId()).collect(Collectors.toSet());
        Set<String> destSiteIds = (shipmentsData.stream().filter(shipmentV2 -> shipmentV2.getDestination() != null && shipmentV2.getDestination().getSiteId() != null).map(shipmentV2 -> shipmentV2.getDestination().getSiteId()).collect(Collectors.toSet()));
        Set<String> stopSiteIds = shipmentsData.stream().map(ShipmentV2::getStops).flatMap(shipmentStopV2s -> shipmentStopV2s.stream().filter(shipmentStopV2 -> shipmentStopV2.getLocation() != null && shipmentStopV2.getLocation().getSiteId() != null).map(shipmentStopV2 -> shipmentStopV2.getLocation().getSiteId())).collect(Collectors.toSet());
        Set<String> palletSiteIds = shipmentsData.stream().map(ShipmentV2::getStops).flatMap(shipmentStopV2s -> shipmentStopV2s.stream().filter(shipmentStopV2 -> shipmentStopV2.getStopContent() != null && shipmentStopV2.getStopContent().getPallet() != null).map(shipmentStopV2 -> shipmentStopV2.getStopContent().getPallet()).flatMap(pallets -> pallets.stream().filter(pallet -> pallet.getDestLocationID() != null && !pallet.getSourceLocationID().isEmpty()).map(Pallet::getDestLocationID))).collect(Collectors.toSet());

        siteIds.addAll(consigneeSiteIds);
        siteIds.addAll(shipperSiteIds);
        siteIds.addAll(forwarderSiteIds);
        siteIds.addAll(sourceSiteIds);
        siteIds.addAll(destSiteIds);
        siteIds.addAll(stopSiteIds);
        siteIds.addAll(palletSiteIds);

        List<String> myList = new ArrayList<String>(siteIds);
        int midpoint = myList.size() / 2;
        List<String> firstHalfList = myList.subList(0, midpoint);
        List<String> secondHalfList = myList.subList(midpoint, siteIds.size());

        Set<String> set1 = new HashSet<>(firstHalfList);
        Set<String> set2 = new HashSet<>(secondHalfList);
        Map<String, LocationDoc> firstSiteIdMap = cacheService.findLocationBySiteIdIn(set1);
        Map<String, LocationDoc> secondSiteIdMap = cacheService.findLocationBySiteIdIn(set2);
        firstSiteIdMap.putAll(secondSiteIdMap);
        return firstSiteIdMap;
    }

    private void ilmctStopEtaCalculation(ShipmentV2 shipmentDoc, List<ShipmentStopV2> sortedStops, TimeEstimationBean tImeEstimationBean, LocalDateTime lastShipmentEndDate, ApplicationSettings applicationSettings) {
        for (ShipmentStopV2 shipmentStopV2 : sortedStops) {
            try {
                RoutePointBean routePointBean = tImeEstimationBean.getRoutePoints().stream()
                        .filter(stopPoint -> shipmentStopV2.getStopNumber().equals(stopPoint.getSequence()))
                        .findFirst().orElse(null);
                if (routePointBean != null) {
                    TimeEstimationBean estimationBean = new TimeEstimationBean(routePointBean, lastShipmentEndDate);
                    shipmentStopV2.setCalculatedETA(new ShipmentDateTime(
                            DateUtil.convertDate(estimationBean.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                            , null));
                    shipmentStopV2.setDistanceInKms((float) estimationBean.getDistance());
                    lastShipmentEndDate = DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2);
                }
                Long activityTime = 0L;
                if (shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
                    activityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
                }
                if (shipmentStopV2.getStopType().equalsIgnoreCase("P")) {
                    activityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
                }
                TimeEstimationBean calculatedETD = new TimeEstimationBean(activityTime, lastShipmentEndDate);
                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(
                        DateUtil.convertDate(calculatedETD.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                        , null));
                if (shipmentStopV2.getCalculatedETD() != null) {
                    lastShipmentEndDate = DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2);
                }
            } catch (Exception e) {
                logger.info("Exception at routepoint bean calculation");
                e.printStackTrace();
            }
            shipmentDoc.getStops().add(shipmentStopV2);
        }
    }

    private void nonIlmctEtaCalculation(ShipmentV2 shipmentDoc, List<ShipmentStopV2> sortedStops, TimeEstimationBean
            tImeEstimationBean, LocalDateTime lastShipmentEndDate, ApplicationSettings applicationSettings) {
        for (ShipmentStopV2 shipmentStopV2 : sortedStops) {
            RoutePointBean routePointBean = tImeEstimationBean.getRoutePoints().stream()
                    .filter(stopPoint -> shipmentStopV2.getStopNumber().equals(stopPoint.getSequence()))
                    .findFirst().orElse(null);
            if (routePointBean != null) {
                shipmentStopV2.setDistanceInKms((float) routePointBean.getDistance());
            }
            if (shipmentStopV2.getStopType().equalsIgnoreCase("D")
                    && shipmentStopV2.getCalculatedETA() != null) {
                TimeEstimationBean calculatedETD = new TimeEstimationBean(Long.valueOf(applicationSettings.getShipmentActivityTime()),
                        DateUtil.convertDate(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2,
                                shipmentStopV2.getCalculatedETA().getTZId(), null)
                );
                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.convertDate(calculatedETD.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2), null));
            }

            if (shipmentStopV2.getStopType().equalsIgnoreCase("P") && shipmentStopV2.getCalculatedETA() != null) {
                TimeEstimationBean calculatedETD = new TimeEstimationBean(
                        Long.valueOf(applicationSettings.getShipmentPickupActivityTime()), DateUtil.convertDate(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2,
                        shipmentStopV2.getCalculatedETA().getTZId(), null));
                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(
                        DateUtil.convertDate(calculatedETD.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                        , shipmentStopV2.getEstimatedArrival().getTZId()));
            }
            shipmentDoc.getStops().add(shipmentStopV2);
        }
    }

    private void postShipmentData(ShipmentV2 shipmentV2, ApplicationSettings applicationSettings) {
        Map<String, String> urls;
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        String loadId = shipmentV2.getLoadID();
        boolean isContainerTracking = shipmentV2.getContainerTracking() != null ? shipmentV2.getContainerTracking().equalsIgnoreCase("Y") : Boolean.FALSE;
        List<LoadReference> loadReferenceStream = shipmentV2.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType() != null &&
                loadReference.getLoadReferenceType().equalsIgnoreCase("LegType") && loadReference.getContent() != null &&
                loadReference.getContent().equalsIgnoreCase("MULTI LEG") || loadReference.getContent().equalsIgnoreCase("SINGLE LEG")).toList();
        boolean isMultiLegShipment = !loadReferenceStream.isEmpty();
        boolean isSingleLegShipment = !isMultiLegShipment;

        Map<String, String> orderUrls = Utility.generateEncryptedOrderUrls(shipmentV2, sdf.format(shipmentV2.getCreatedDate()), applicationSettings.getTrackTraceUrl(), loadId, ordersV2Repository, isSingleLegShipment, isMultiLegShipment);
        urls = Utility.generateEncryptedUrls(loadId.trim(), sdf.format(shipmentV2.getCreatedDate()), isContainerTracking,
                applicationSettings.getTrackTraceUrl(), shipmentV2.getMode());
        FeedBean feedBean = new FeedBean();
        FeedHeaderBean feedHeader = new FeedHeaderBean();
        feedHeader.setFeedGenDtTime(sdf.format(new Date()));
        FeedContentBean feedContent = new FeedContentBean();
        List<ILMCTOutXML> loadUrls = new ArrayList<>();
        for (Map.Entry<String, String> urlEntry : urls.entrySet()) {
            ILMCTOutXML bean = new ILMCTOutXML(null, null, shipmentV2.getLoadID(), urlEntry.getKey(),
                    urlEntry.getValue());
            loadUrls.add(bean);
            shipmentV2.setTtURL(bean.getLoadUrl());
            shipmentV2Repository.save(shipmentV2);
            logger.info("TT URL Created :: " + bean.getLoadUrl());
            logger.info("TT URL Created in shipment2:: " + shipmentV2.getTtURL());
        }
        if (!orderUrls.isEmpty()) {
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (Map.Entry<String, String> urlEntry : orderUrls.entrySet()) {
                OrderDetail orderDetail = new OrderDetail(urlEntry.getKey(), urlEntry.getValue());
                orderDetails.add(orderDetail);
            }
            ILMCTOrderOutXML ilmctOrderOutXML = new ILMCTOrderOutXML(shipmentV2.getLoadID(), new OrderDetails(orderDetails));
            feedContent.setILMCTOrderOutXML(ilmctOrderOutXML);
        }
        feedContent.setILMCTOutXML(loadUrls);
        feedBean.setFeedHeader(feedHeader);
        feedBean.setFeedContent(feedContent);
        Boolean isOrders = !shipmentV2.getOrders().isEmpty();
        generateShipmentXml(feedBean, applicationSettings, isOrders, isMultiLegShipment);
    }


    private void generateShipmentXml(FeedBean feedBean, ApplicationSettings applicationSettings, Boolean isOrders, Boolean isMultiLegShipment) {
        String shipmentXML = CommonUtil.convertObjectToXML(feedBean, FeedBean.class);
        if (!shipmentXML.isEmpty()) {
            String shipmentStylisedXML = Utility.stylizer("ShipmentTrackingStyleSheet_V2.xsl", shipmentXML);
            if (isOrders) {
                String shipmentOrderStylisedXML = Utility.stylizer("ShipmentOrdersTrackingStyleSheet.xsl", shipmentXML);
                postOrderTrackingData(shipmentOrderStylisedXML, applicationSettings);
            }
            postShipmentTrackingData(shipmentStylisedXML, applicationSettings);
        }
    }

    private void postOrderTrackingData(String eventStylisedXML, ApplicationSettings applicationSettings) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            String auth = applicationSettings.getOtmUsername() + ":" + applicationSettings.getOtmPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> requestEntity = new HttpEntity<>(eventStylisedXML, headers);
            logger.info("requestEntity" + requestEntity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                    requestEntity, String.class);
            logger.info("Order Tracking" + responseEntity);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                result = responseEntity.getBody();
            }
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
        }
    }

    private void postShipmentTrackingData(String eventStylisedXML, ApplicationSettings applicationSettings) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            logger.info("Shipment TnT Auth" + applicationSettings.getOtmUsername() + ":" + applicationSettings.getOtmPassword());
            String auth = applicationSettings.getOtmUsername() + ":" + applicationSettings.getOtmPassword();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> requestEntity = new HttpEntity<>(eventStylisedXML, headers);
            logger.info("Shipment TnT Request" + requestEntity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                    requestEntity, String.class);
            logger.info("Shipment TnT Transmission" + responseEntity);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                result = responseEntity.getBody();
            }
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
        }
    }

    private void saveContainers(ShipmentV2 shipmentDoc, User loginUser) {
        Container container = containerRepository.findByContainerNumberAndShipContainerId(shipmentDoc.getContainer().getNumber(), shipmentDoc.getContainer().getId());
        if (container == null) {
            container = new Container();
        }
        container.setShipContainerId(shipmentDoc.getContainer().getId());
        container.setContainerNumber(shipmentDoc.getContainer().getNumber());
        container.setContainerRef(shipmentDoc.getContainer().getType());
        container.setDomainName(shipmentDoc.getPartition());
        container.setContainerInitial(shipmentDoc.getContainer().getType());

        List<ShipmentV2> shipments = container.getShipmentsV2() == null ? new ArrayList<>()
                : container.getShipmentsV2();
        boolean isShipmentExists = false;
        final String shipId = shipmentDoc.getId();
        if (!shipments.isEmpty()) {
            Optional<ShipmentV2> s = shipments.stream().filter(x -> x.getId().equals(shipId)).findAny();
            if (s.isPresent()) {
                isShipmentExists = true;
                shipments.remove(s.get());
                shipments.add(shipmentDoc);
            }
        }
        if (!isShipmentExists) {
            shipments.add(shipmentDoc);
            List<String> shipmentIds = container.getShipmentId() == null ? new ArrayList<>()
                    : container.getShipmentId();
            shipmentIds.add(shipmentDoc.getId());
            container.setShipmentId(shipmentIds);
        }

        shipments = CommonUtil.sortContainerShipments(shipments);
        container.setShipmentsV2(shipments);

        List<String> startDates = container.getStartDate() == null ? new ArrayList<>()
                : container.getStartDate();
        if (!startDates.contains(shipmentDoc.getStartDate().getDateTime())) {
            startDates.add(shipmentDoc.getStartDate().getDateTime());
        }
        container.setStartDate(startDates);

        try {
            container.setSequence(CommonUtil.generateSequenceBasedOnDates(startDates));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> endDates = container.getEndDate() == null ? new ArrayList<>() : container.getEndDate();
        if (!endDates.contains(shipmentDoc.getEndDate().getDateTime())) {
            endDates.add(shipmentDoc.getEndDate().getDateTime());
        }
        container.setEndDate(endDates);

        List<String> modes = container.getMode() == null ? new ArrayList<>() : container.getMode();
        modes.add(shipmentDoc.getMode());
        container.setMode(modes);

        List<String> carriageTypes = container.getCarriageType() == null ? new ArrayList<>()
                : container.getCarriageType();
        if (!carriageTypes.stream().allMatch(s -> s.equals(shipmentDoc.getContainer().getType()))) {
            carriageTypes.add(shipmentDoc.getContainer().getType());
            container.setCarriageType(carriageTypes);
        }

        Set<String> sealNumbers = container.getSealNumber() == null ? new LinkedHashSet<>()
                : container.getSealNumber();
        sealNumbers.add(shipmentDoc.getContainer().getSealNumber());
        container.setSealNumber(sealNumbers);
        if (container.getStatus() == null || container.getStatus().name().equals(StatusEnum.NOT_STARTED.name())) {
            container.setStatus(shipmentDoc.getStatus());
        } else if (!shipmentDoc.getStatus().name().equals(StatusEnum.NOT_STARTED.name())) {
            container.setStatus(shipmentDoc.getStatus());
        }

        TimeZone zone = TimeZone.getDefault();

        List<ShipmentV2> shipmentV2List = container.getShipmentsV2();
        for (ShipmentV2 shipmentV2 : shipmentV2List) {
            for (ShipmentStopV2 shipmentStopV2 : shipmentV2.getStops()) {
                if (shipmentStopV2.getStopType().equals("D")) {
                    List<LoadReference> loadReferences = shipmentV2.getLoadReferences();
                    for (LoadReference loadReference : loadReferences) {
                        if (loadReference.getContent() != null && loadReference.getContent().equals("MAIN CARRIAGE")) {
                            container.setDeliveryPta(DateUtil.formatDate(DateUtil.convertDate(shipmentStopV2.getPlannedArrival().getDateTime(),
                                            DateUtil.dateTimeFormatter2, shipmentStopV2.getPlannedArrival().getTZId(), zone.getID()),
                                    DateUtil.dateTimeFormatter1));
                        }
                    }
                }
            }
        }
        containerRepository.save(container);
        logger.info("Container Saved Successfully with number :: " + container.getContainerNumber());
    }

    //shipment status api
    @Override
    public ApiResponse saveShipmentStatus(ShipmentStatusDto shipmentStatusDto, HttpServletRequest request){
        try {
            User loginUser=userRepository.findOneByUserId(tokenUtilService.getUserId(request)).orElse(null);
            if (shipmentStatusDto.transmission!=null && shipmentStatusDto.transmission.transmissionBody!=null &&
            shipmentStatusDto.transmission.transmissionBody.gLogXMLElement!=null &&
            shipmentStatusDto.transmission.transmissionBody.gLogXMLElement.otmShipmentStatus!=null){
                String refNumQualifierGid="";
                if (shipmentStatusDto.transmission.otmTransmissionHeader!=null && shipmentStatusDto.transmission.otmTransmissionHeader.Refnum!=null){
                    refNumQualifierGid =(shipmentStatusDto.transmission.otmTransmissionHeader.Refnum.refNumValue);
                }
                OtmShipmentStatus otmShipmentStatus=shipmentStatusDto.transmission.transmissionBody.gLogXMLElement.otmShipmentStatus;
                String locationName="";
                logger.info("saveShipmentStatus" );
                return saveShipmentStatus(otmShipmentStatus,loginUser,refNumQualifierGid,locationName);
            }
            else {
                loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).user(null).message("Invalid Event").build(), request);
                return new ApiResponse(HttpStatus.BAD_REQUEST,"Invalid event");
            }
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong");
        }
    }

    public ApiResponse saveShipmentStatus(OtmShipmentStatus otmShipmentStatus,User loginUser,String refNumQualifierGid,String locationName) throws ParseException, ApplicationSettingsNotFoundException {
        ShipmentStatusDoc shipmentStatusDoc = buildShipmentStatusDoc.apply(otmShipmentStatus); //set the data from dto to entity
        shipmentStatusDoc.setLocationName(locationName);
        shipmentStatusDoc.setParcelRefNum(refNumQualifierGid);
        OtmSSLocation otmAddress = CommonUtil.resolve(() -> otmShipmentStatus.otmSSStop.otmSSLocation)
                .orElse(new OtmSSLocation());
        if (otmAddress.otmLatitude != null && otmAddress.otmLongitude != null) {
            shipmentStatusDoc.setLocation(new LatLng(otmAddress.otmLatitude, otmAddress.otmLongitude));
        }
        return saveEvent(shipmentStatusDoc, loginUser, refNumQualifierGid);
    }

    private final Function<OtmShipmentStatus, ShipmentStatusDoc> buildShipmentStatusDoc = (otmShipmentStatus) -> {
        ShipmentStatusDoc shipmentStatusDoc = new ShipmentStatusDoc();
        Optional<OtmShipmentStatus> optionalOtmShipmentStatus = Optional.ofNullable(otmShipmentStatus);
        if (optionalOtmShipmentStatus.isPresent()) {
            if (!Utility
                    .isEmpty(CommonUtil.resolve(() -> otmShipmentStatus.otmShipmentGid.otmGid.otmDomainName).orElse(null)))
                shipmentStatusDoc.setLoadId(
                        CommonUtil.resolve(() -> otmShipmentStatus.otmShipmentGid.otmGid.otmDomainName).orElse(null)
                                + Constants.SEPARATOR_DOMAIN_VS_LOADID
                                + CommonUtil.resolve(() -> otmShipmentStatus.otmShipmentGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc.setQuickCodeGid(CommonUtil.resolve(() -> otmShipmentStatus.quickCodeGid.otmGid.otmXid).orElse(null));

            shipmentStatusDoc.setEventDate(otmShipmentStatus.otmEventDt);
            shipmentStatusDoc.setEventDescription(
                    CommonUtil.resolve(() -> otmShipmentStatus.otmEventGroup.otmEventGroupDescription).orElse(null));
            shipmentStatusDoc.setStatusReasonCodeGid(
                    CommonUtil.resolve(() -> otmShipmentStatus.otmStatusReasonCodeGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc.setStatusCodeId(
                    CommonUtil.resolve(() -> otmShipmentStatus.otmStatusCodeGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc
                    .setDriverId(CommonUtil.resolve(() -> otmShipmentStatus.otmDriverGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc.setPowerUnitId(
                    CommonUtil.resolve(() -> otmShipmentStatus.otmPowerUnitGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc
                    .setEventReceivedDate(CommonUtil.resolve(() -> otmShipmentStatus.otmEventRecdDate).orElse(null));
            shipmentStatusDoc.setEventType(
                    CommonUtil.resolve(() -> otmShipmentStatus.otmResponsiblePartyGid.otmGid.otmXid).orElse(null));
            shipmentStatusDoc.setShipmentRefnum(otmShipmentStatus.otmShipmentRefnum);
            shipmentStatusDoc
                    .setStopSequence(CommonUtil.resolve(() -> otmShipmentStatus.otmSSStop.otmSSStopSequenceNum).orElse(0));
            if (!Utility.isEmpty(shipmentStatusDoc.getShipmentRefnum())) {
                shipmentStatusDoc.getShipmentRefnum().forEach(otmShipmentRefnum -> {
                    if (otmShipmentRefnum.otmShipmentRefnumQualifierGid != null)
                        if (KEY_TRUCK_NO.equals(otmShipmentRefnum.otmShipmentRefnumQualifierGid.otmGid.otmXid)) {
                            shipmentStatusDoc.setTruckNo(otmShipmentRefnum.otmShipmentRefnumValue);
                        }
                });
            }
            if (otmShipmentStatus.otmFlexFieldDates != null) {
                shipmentStatusDoc.setOtmflexFieldDates(otmShipmentStatus.otmFlexFieldDates);
            }
        }
        return shipmentStatusDoc;
    };

    @Autowired
    LoggerService loggerService;
    public ApiResponse saveEvent(ShipmentStatusDoc shipmentStatusDoc, User loginUser, String refNumQualifierGid) throws ParseException, ApplicationSettingsNotFoundException {
        ApplicationSettings applicationSettings = applicationSettingsRepository.findAll().get(0);
        ShipmentV2 shipmentV2 = null;
        if (!Utility.isEmpty(shipmentStatusDoc.getLoadId())) {
            try {
                shipmentV2 = shipmentV2Repository.findOneByLoadID(shipmentStatusDoc.getLoadId()).orElse(new ShipmentV2());
            } catch (IncorrectResultSizeDataAccessException e) {
                List<ShipmentV2> shipmentByLoadID = shipmentV2Repository.findShipmentByLoadID(shipmentStatusDoc.getLoadId());
                shipmentV2 = shipmentByLoadID.get(0);
            }
            logger.info("saveEvent shipmentV2","()"+shipmentV2);
        } else {
            List<ShipmentV2> shipments = new ArrayList<>();
            for (ShipmentV2 availableShipment : shipmentV2Repository.findAll()) {
                if (availableShipment.getLoadReferences().stream().anyMatch(loadReference -> loadReference.getLoadReferenceType().equals(KEY_TRUCK_NO) && loadReference.getContent() != null && loadReference.getContent().equals(shipmentStatusDoc.getTruckNo()))) {
                    shipments.add(availableShipment);
                }
            }
            if (!Utility.isEmpty(shipments)) {
                if (shipments.size() > 1) {
                    loggerService.saveLog(Log.builder().loadId(shipmentV2.getLoadID()).actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message("More than one active Shipment is linked to a truck plate number.").build());
                    return new ApiResponse(HttpStatus.CONFLICT, "More than one active Shipment is linked to a truck plate number.");
                }
                shipmentV2 = shipments.get(0);
            }
        }
        if (shipmentV2 != null && !ObjectUtils.isEmpty(shipmentV2.getId())
                && !StatusEnum.getCompletedStatuses().contains(shipmentV2.getStatus())) {
            logger.info("Shipment Data Available with load Id:: " + shipmentV2.getLoadID());
            if (!StringUtils.isEmpty(shipmentV2.getMode()) && (shipmentV2.getMode().equals(EXPRESS) || refNumQualifierGid.equalsIgnoreCase(Constants.PARCEL_REF_NUM))) {
                if (shipmentStatusDoc.getStatusCodeId() != null) {
                    if (shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase("X3") || shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase("AF")) {
                        shipmentV2.setStatus(StatusEnum.STARTED);
                    } else if (shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase("CLOSED") || shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase("COMPLETED")) {
                        shipmentV2.setStatus(StatusEnum.COMPLETED);
                    }
                }
                shipmentV2Repository.save(shipmentV2);
                shipmentStatusRepository.save(shipmentStatusDoc);
                return new ApiResponse(HttpStatus.OK, "saved successfully");
            } else {
                ApiResponse response = validateStatusDoc(shipmentStatusDoc, shipmentV2);
                logger.info("saveEvent validateStatusDoc","()"+response);
                if (response.getStatus().equals(HttpStatus.OK)) {
                    logger.info("Response status is ok from server call");
                    shipmentStatusDoc.setLoadId(shipmentV2.getLoadID());
                    return shipmentStatusDocMapping(shipmentStatusDoc, applicationSettings, shipmentV2, loginUser);
                } else {
                    return response;
                }
            }
        } else {
            logger.info("saveEvent else");
            loggerService.saveLog(Log.builder().loadId(shipmentStatusDoc.getLoadId()).actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now())
                    .type(MessageTypeEnum.ERROR).message("Shipment was not found").build());
            return new ApiResponse(HttpStatus.NOT_ACCEPTABLE, "Shipment not found");
        }
    }

    private ApiResponse validateStatusDoc(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2) {
        if (shipmentStatusDoc.getStatusCodeId() != null && shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())) {
            return new ApiResponse(HttpStatus.OK, "SUCCESS");
        }
        if (shipmentStatusDoc.getLocation() == null || shipmentStatusDoc.getLocation().getX() == 0) {
            if (shipmentStatusDoc.getStopSequence() > 0) {
                if (shipmentV2.getStops().isEmpty()) {
                    return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid stop no.");
                } else {
                    shipmentV2.getStops().stream()
                            .filter(shipmentStopV2 -> shipmentStopV2.getStopNumber() == shipmentStatusDoc
                                    .getStopSequence())
                            .findAny().ifPresent(locationId -> shipmentStatusDoc.setLocation(
                                    cacheService.findLocationBySiteId(locationId.getLocation().getSiteId()).getLocation()));
                }
            } else {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid stop no.");
            }
        }
        if (shipmentStatusDoc.getLocation() == null || shipmentStatusDoc.getLocation().getX() == 0) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Location is invalid as either it is null or the co-ordinates are 0");
        }
        if (shipmentV2.getStatus().name().equalsIgnoreCase(StatusEnum.CLOSED.name())) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Shipment Already Closed");
        }
        if (shipmentV2.getTrackingProvider() == null || shipmentV2.getTrackingProvider().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "TRACKING_PROVIDER attribute is missing.");
        }
        return new ApiResponse(HttpStatus.OK, "SUCCESS");
    }

    private ApiResponse shipmentStatusDocMapping(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings applicationSettings, ShipmentV2 shipmentV2, User loginUser) throws ParseException, ApplicationSettingsNotFoundException {
        String userTimeZone = CommonUtil.getTimeZone(loginUser);
        Units.TemperatureUnit temperatureUnit = (loginUser == null || loginUser.getTemperatureUnit() == null)
                ? Units.TemperatureUnit.CELSIUS
                : loginUser.getTemperatureUnit();
        Units.DistanceUnit distanceUnit = (loginUser == null || loginUser.getDistanceUnit() == null)
                ? Units.DistanceUnit.KILOMETERS
                : loginUser.getDistanceUnit();

        boolean hazardous = false;
        Optional<LoadReference> isHazardous = shipmentV2.getLoadReferences()
                .stream().filter(loadReference -> loadReference.getLoadReferenceType().equals("HazardousFlag")).findAny();
        if (isHazardous.isPresent()) {
            LoadReference hazardousPresent = isHazardous.get();
            if (hazardousPresent.getContent() != null) {
                hazardous = Boolean.parseBoolean(hazardousPresent.getContent());
            }
        }

        if (shipmentStatusRepository.countByLoadId(shipmentV2.getLoadID()) == 0) {
            if (shipmentV2.getStatus().equals(StatusEnum.NOT_STARTED) && shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())) {
                return new ApiResponse(HttpStatus.NOT_ACCEPTABLE, "Intransit event can't be inserted as Shipment not started");
            }
        }

        StatusEnum previousShipmentStatus = shipmentV2.getStatus();
        final ShipmentDateTime previousEta = Utility.getLastDeliveryStop(shipmentV2).getCalculatedETA();
        final Date previousEtaForOutBound = !Utility.isEmpty(previousEta.getDateTime())
                ? DateUtil.formatDateStringWithoutTime(previousEta.getDateTime(), DateUtil.dateTimeFormatter2)
                : null;
        if (shipmentStatusDoc.getLocation() != null) {
            shipmentV2.setWeatherAlert(this.fetchWeatherAlert(new WeatherCalculationBean(shipmentStatusDoc.getLocation()), applicationSettings));
        }
        shipmentStatusDoc.setWeatherAlert(shipmentV2.getWeatherAlert());
        if (shipmentStatusDoc.getSpeed() > 0)
            shipmentV2.setCurrentSpeed(shipmentStatusDoc.getSpeed() + " " + shipmentStatusDoc.getSpeedUnit());
        if (shipmentStatusDoc.getTemperature() > 0)
            shipmentV2.setCurrentTemp(shipmentStatusDoc.getTemperature() + " " + shipmentStatusDoc.getTempUnit());
        if (shipmentStatusDoc.getFuel() > 0) {
            shipmentV2.setCurrentFuelInLtr(shipmentStatusDoc.getFuel());
            shipmentV2.setCurrentFuel(shipmentStatusDoc.getFuel() + " " + (Utility.isEmpty(shipmentStatusDoc.getFuelUnit()) ? "" : shipmentStatusDoc.getFuelUnit().toUpperCase()));
        }
        shipmentV2.setAvgSpeed((shipmentV2.getAvgSpeed() + shipmentStatusDoc.getSpeed()) / 2);
        shipmentV2.setSpeedUnit(shipmentStatusDoc.getSpeedUnit());

        if (!Constants.TRACKINGPROVIDERETASTATUSES.contains(shipmentStatusDoc.getStatusCodeId())) {
            shipmentStatusRepository.save(shipmentStatusDoc);
        }

        if (shipmentStatusDoc.getStatusCodeId() != null && !shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase("IOT")) {
            logger.info("SaveShipment Status : Status Based Events Started :: ");
            statusBaseEventAction(shipmentStatusDoc, applicationSettings, shipmentV2, hazardous);
            loggerService.saveLog(Log.builder().loadId(shipmentV2.getLoadID()).actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now())
                    .type(MessageTypeEnum.INFO).message("SaveShipment Status IOT: Status Based Events Mapped").build());
            logger.info("SaveShipment Status : Status Based Events Mapped :: ");
        }

        shipmentV2 = shipmentV2Repository.save(shipmentV2);
        if (!Constants.TRACKINGPROVIDERETASTATUSES.contains(shipmentStatusDoc.getStatusCodeId())) {
            shipmentStatusRepository.save(shipmentStatusDoc);
            loggerService.saveLog(Log.builder().loadId(shipmentV2.getLoadID()).actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now())
                    .type(MessageTypeEnum.INFO).message("SaveShipment Status Saved Successfully").build());
            logger.info("SaveShipment Status Saved Successfully");
        }
        List<Container> containers = containerRepository.findByShipmentsV2LoadIDAndShipContainerId(shipmentV2.getLoadID(), shipmentV2.getContainer().getId());
        for (Container container : containers) {
            updatingContainerData(shipmentV2, container, userTimeZone);
            logger.info("Updating Containers Data ");
        }

        try {
            String etaUpdateReq = shipmentV2.getCallBackRequired();
            ShipmentStopV2 lastStop = Utility.getLastDeliveryStop(shipmentV2);
            boolean isStatusValid = StatusEnum.getInOTMUpdateStatuses().contains(shipmentV2.getStatus().name());

            boolean isStatusChanged = !previousShipmentStatus.name().equalsIgnoreCase(shipmentV2.getStatus().name());
            Date currentEtaForOutBound = lastStop != null
                    ? DateUtil.formatDateStringWithoutTime(lastStop.getCalculatedETA().getDateTime(),
                    DateUtil.dateTimeFormatter2)
                    : null;

            boolean isEtaChanged = previousEtaForOutBound == null || currentEtaForOutBound != null && !previousEtaForOutBound.equals(currentEtaForOutBound);

            if (isStatusValid && (etaUpdateReq != null && !etaUpdateReq.equals("N"))
                    && !Constants.TRACKINGPROVIDERETASTATUSES.contains(shipmentStatusDoc.getStatusCodeId())
                    && (isStatusChanged || isEtaChanged)) {
                raiseOutBountEvent(shipmentV2, shipmentStatusDoc, applicationSettings);
                loggerService.saveLog(Log.builder().loadId(shipmentV2.getLoadID()).actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now())
                        .type(MessageTypeEnum.INFO).message("SaveShipment Status : Raise Out Bound Events").build());
                logger.info("SaveShipment Status : Raise Out Bound Events ");
            }
            ShipmentStopV2 lastDeliveryStop = Utility.getLastDDeliveryStop(shipmentV2);
            if (lastDeliveryStop != null && lastDeliveryStop.isDelivered()
                    && lastDeliveryStop.getStopNumber().equals(shipmentStatusDoc.getStopSequence())
                    && shipmentStatusDoc.getStatusCodeId().equals("D1")) {
                generateD1ProcessedXml(shipmentV2, applicationSettings);
            }
            etaPropagation(shipmentStatusDoc, applicationSettings, shipmentV2, hazardous);

            return new ApiResponse(HttpStatus.OK, shipmentStatusDoc.getStatusCodeId()+" Event Added Sucessfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("shipmentStatusDocMapping catch");
            return new ApiResponse(HttpStatus.NOT_ACCEPTABLE, "Something went wrong");
        }
    }

    public void generateD1ProcessedXml(ShipmentV2 shipment, ApplicationSettings applicationSettings) {
        FeedBean feedBean = new FeedBean();
        FeedHeaderBean feedHeader = new FeedHeaderBean();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        feedHeader.setFeedGenDtTime(sdf.format(now));
        FeedContentBean feedContent = new FeedContentBean();
        D1ProcessedEvent event = new D1ProcessedEvent(shipment.getLoadID());
        feedContent.setD1ProcessedEvent(event);
        feedBean.setFeedHeader(feedHeader);
        feedBean.setFeedContent(feedContent);
        String xmlString = Utility.convertObjectToXML(feedBean);
        if (!xmlString.isEmpty()) {
            String d1ProcessedXML = Utility.stylizer("D1_Processed_V2.xsl", xmlString);
            d1ProcessedEventData(d1ProcessedXML, applicationSettings);
        }
    }

    private void d1ProcessedEventData(String eventStylisedXML, ApplicationSettings applicationSettings) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            String username = applicationSettings.getOtmUsername();
            String password = applicationSettings.getOtmPassword();
            HttpEntity<String> requestEntity = new HttpEntity<>(eventStylisedXML, createHeaders(username, password));

            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                    requestEntity, String.class);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                result = responseEntity.getBody();
                logger.info("==================== D1 EVENT PROCESSED SENT ======================\n" + eventStylisedXML);
                loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_SHIPMENT_STATUS).localDateTime(LocalDateTime.now()).type(MessageTypeEnum.INFO).message("D1 EVENT PROCESSED SUCCESSFULLY").build());
                logger.info(result);
                D1ProcessedDoc d1ProcessedDoc = new D1ProcessedDoc();
                d1ProcessedDoc.setRequest(requestEntity.getBody());
                d1ProcessedDoc.setD1ProcessedResponse(result);
                d1ProcessedEventRepository.save(d1ProcessedDoc);
            }
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
        }
    }


    private synchronized void etaPropagation(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings applicationSettings, ShipmentV2 shipmentV2, Boolean hazardous) {
        AtomicReference<ShipmentDateTime> calculatedETD = new AtomicReference<>();
        DriverRestTimeDoc timing = null;
        Container container = containerRepository.findByContainerNumberAndShipContainerId(shipmentV2.getContainer().getNumber(), shipmentV2.getContainer().getId());
        if (container == null) {
            new ApiResponse(HttpStatus.OK, "Success", "ETA propagation not done due to container un availabiliy");
            return;
        }
        List<ShipmentV2> shipmentV2List = container.getShipmentsV2();
        ShipmentV2 mainCarriageShipment = null;
        for (ShipmentV2 shipment : shipmentV2List) {
            Optional<LoadReference> carriageTypeReference = shipment.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType().equals("CarriageType")).findAny();
            if (carriageTypeReference.isPresent() && carriageTypeReference.get().getContent() != null && carriageTypeReference.get().getContent().equals("MAIN CARRIAGE")) {
                mainCarriageShipment = shipment;
            }
        }
        List<ShipmentV2> lastMileDeliveryShipments;
        lastMileDeliveryShipments = getLastMileDeliveryShipments(mainCarriageShipment);
        if (lastMileDeliveryShipments == null) {
            lastMileDeliveryShipments = new ArrayList<>();
        }
        lastMileDeliveryShipments.addAll(shipmentV2List);

        Optional<LoadReference> optionalCarriageType = shipmentV2.getLoadReferences().stream()
                .filter(loadReference -> loadReference.getLoadReferenceType().equals("CarriageType")).findAny();
        if (optionalCarriageType.isPresent()) {
            if (optionalCarriageType.get().getContent() != null && optionalCarriageType.get().getContent().equals("MAIN CARRIAGE")) {
                ShipmentStopV2 lastDDeliveryStop = Utility.getLastDDeliveryStop(shipmentV2);
                assert lastDDeliveryStop != null;
                if (lastDDeliveryStop.getStopNumber().equals(shipmentStatusDoc.getStopSequence())) {
                    calculatedETD.set(lastDDeliveryStop.getCalculatedETD());
                }
            }
        }
        List<ShipmentV2> filteredList = new ArrayList<>();
        for (ShipmentV2 shipmentV21 : lastMileDeliveryShipments) {
            if (!shipmentV21.getLoadID().equalsIgnoreCase(mainCarriageShipment.getLoadID()) && !shipmentV21.getStatus().equals(StatusEnum.CLOSED)) {
                Optional<LoadReference> carriageTypeReference = shipmentV21.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType() != null
                        && loadReference.getLoadReferenceType().equals("CarriageType")).findAny();
                if (carriageTypeReference.isPresent() && carriageTypeReference.get().getContent() != null && carriageTypeReference.get().getContent().equals("ON CARRIAGE")) {
                    Optional<LoadReference> transmitETAReference = shipmentV21.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType() != null
                            && loadReference.getLoadReferenceType().equals("TransmitETA")).findAny();
                    if (transmitETAReference.isPresent() && transmitETAReference.get().getContent() != null && transmitETAReference.get().getContent().equals("Y")) {
                        filteredList.add(shipmentV21);
                    }
                } else {
                    Optional<LoadReference> transmitETAReference = shipmentV21.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType() != null
                            && loadReference.getLoadReferenceType().equals("TransmitETA")).findAny();
                    if (transmitETAReference.isPresent() && transmitETAReference.get().getContent() != null && transmitETAReference.get().getContent().equals("Y")) {
                        filteredList.add(shipmentV21);
                    }
                }
            }
        }

        if (calculatedETD.get() != null) {
            ShipmentV2 previousShipment = null;
            int shipmentIndex;
            for (ShipmentV2 shipmentV21 : filteredList) {

                Boolean isIlmct = shipmentV21.getTrackingProvider().equals("ILMCT") ? Boolean.TRUE : Boolean.FALSE;

                ShipmentStopV2 firstStop = shipmentV21.getStops().stream().filter(ss -> ss.getStopType().equalsIgnoreCase("P")).
                        min(Comparator.comparing(ShipmentStopV2::getStopNumber)).orElse(null);
                LocalDateTime startDate = null;
                if (firstStop != null) {
                    startDate = DateUtil.convertDate(calculatedETD.get().getDateTime(), DateUtil.dateTimeFormatter2, calculatedETD.get().getTZId(), null);
                }
                ShipmentStopV2 previousStop = null;
                for (ShipmentStopV2 shipmentStopV2 : shipmentV21.getStops()) {
                    int index = shipmentV21.getStops().indexOf(shipmentStopV2);
                    if (shipmentStopV2.getStopType().equals("P")) {
                        shipmentStopV2.setCalculatedETA(calculatedETD.get());
                        shipmentStopV2.setEstimatedArrival(calculatedETD.get());
                        TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(
                                Long.valueOf(applicationSettings.getShipmentActivityTime()),
                                DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2));
                        shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2), null));
                    } else {
                        if (isIlmct) {
                            Optional<CountryDoc> country = countryRepository.findUniqueByIso2CodeOrIso3Code(shipmentV21.getSource().getCountryCode(), shipmentV21.getSource().getCountryCode());
                            boolean isChinaCountry = false;
                            if (country.isPresent()) {
                                if (country.get().getIso2Code().equalsIgnoreCase("CN") || country.get().getIso2Code().equalsIgnoreCase("CHN")) {
                                    isChinaCountry = true;
                                }
                                timing = driverRepository.findByCountryAndStatus(country.get(), true);
                            }

                            List<RoutePointBean> routePointBeans = new ArrayList<>();
                            routePointBeans.add(new RoutePointBean(previousStop.getStopNumber(), new LatLng(Double.parseDouble(previousStop.getLocation().getCoordinates()[1]), Double.parseDouble(previousStop.getLocation().getCoordinates()[0]))));
                            routePointBeans.add(new RoutePointBean(shipmentStopV2.getStopNumber(), new LatLng(Double.parseDouble(shipmentStopV2.getLocation().getCoordinates()[1]), Double.parseDouble(shipmentStopV2.getLocation().getCoordinates()[0]))));

                            TimeEstimationBean tImeEstimationBean = routingService.calculateRouteAndETAV3(
                                    CalculateRouteBean.Builder(Utility.parseDoubleOrNull(shipmentV21.getLoadMeasure().getTotalWeight()), hazardous,
                                            DateUtil.convertDate(previousStop.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2,
                                                    previousStop.getCalculatedETD().getTZId(), null),
                                            false, routePointBeans.toArray(new RoutePointBean[routePointBeans.size()])),
                                    timing, shipmentV2, isChinaCountry, applicationSettings);
                            if (tImeEstimationBean != null) {
                                RoutePointBean routePointBean = tImeEstimationBean.getRoutePoints().stream()
                                        .filter(stopPoint -> shipmentStopV2.getStopNumber().equals(stopPoint.getSequence()))
                                        .findFirst().orElse(null);

                                if (routePointBean != null) {
                                    TimeEstimationBean estimationBean = new TimeEstimationBean(routePointBean, startDate);
                                    shipmentStopV2.setCalculatedETA(new ShipmentDateTime(
                                            DateUtil.convertDate(estimationBean.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                                            , null));
                                    shipmentStopV2.setEstimatedArrival(new ShipmentDateTime(
                                            DateUtil.convertDate(estimationBean.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                                            , null));
                                    shipmentStopV2.setDistanceInKms((float) estimationBean.getDistance());
                                    startDate = DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2);
                                }
                                Long activityTime = 0L;
                                if (shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
                                    activityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
                                }
                                TimeEstimationBean calculatedETDBean = new TimeEstimationBean(activityTime, startDate);
                                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(
                                        DateUtil.convertDate(calculatedETDBean.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                                        , null));
                                if (shipmentStopV2.getCalculatedETD() != null) {
                                    startDate = DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2);
                                }
                            }

                        } else {
                            if (shipmentStatusDoc.otmflexFieldDates != null && shipmentStatusDoc.otmflexFieldDates.AttributeDate2 != null) {
                                LocalDateTime trackingProviderEstimatedDate = DateUtil.convertDate(
                                        previousStop.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2,
                                        previousStop.getCalculatedETD().getTZId(), null);
                                if (trackingProviderEstimatedDate != null) {
                                    shipmentStopV2.getCalculatedETA().setDateTime(DateUtil.formatDate(trackingProviderEstimatedDate, DateUtil.dateTimeFormatter2));
                                    shipmentStopV2.getEstimatedArrival().setDateTime(DateUtil.formatDate(trackingProviderEstimatedDate, DateUtil.dateTimeFormatter2));
                                }
                                TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(
                                        Long.valueOf(applicationSettings.getShipmentActivityTime()),
                                        DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2));
                                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2), null));
                            }
                        }

                    }
                    previousStop = shipmentStopV2;
                    calculatedETD.set(previousStop.getCalculatedETD());
                    shipmentV21.getStops().set(index, shipmentStopV2);
                }
                shipmentV2Repository.save(shipmentV21);
                Optional<ShipmentV2> any = container.getShipmentsV2().stream().filter(shipmentV22 -> shipmentV22.getLoadID().equals(shipmentV21.getLoadID())).findAny();
                shipmentIndex = container.getShipmentsV2().indexOf(shipmentV21);
                if (any.isPresent()) {
                    container.getShipmentsV2().set(shipmentIndex, shipmentV21);
                }
            }
            Container savedContainer = containerRepository.save(container);
        }
        new ApiResponse(HttpStatus.OK, "Success");
    }

    public List<ShipmentV2> getLastMileDeliveryShipments(ShipmentV2 mainCarriageShipment) {
        if (mainCarriageShipment != null && mainCarriageShipment.getOrders() != null) {
            List<String> collect = ordersV2Repository.findByShipmentId(mainCarriageShipment.getLoadID()).stream().map(OrderV2Doc::getOrderId).toList();
            List<ShipmentV2> shipments1;
            shipments1 = shipmentV2Repository.findByOrdersInAndStatusIn(collect, StatusEnum.getLastMileDeliveryStatus());
            List<ShipmentV2> shipments = new ArrayList<>();
            for (ShipmentV2 shipmentV2 : shipments1) {
                List<LoadReference> list = shipmentV2.getLoadReferences();
                for (LoadReference reference : list) {
                    if (reference.getLoadReferenceType() != null && reference.getLoadReferenceType().equalsIgnoreCase("ContainerTracking")
                            && reference.getContent() != null && reference.getContent().equalsIgnoreCase("N")) {
                        shipments.add(shipmentV2);
                    }
                }
            }

            return shipments.stream().filter(shipmentV2 -> shipmentV2.getContainer().getId() != null && shipmentV2.getContainer().getId().equalsIgnoreCase(mainCarriageShipment.getContainer().getId())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void updatingContainerData(ShipmentV2 shipmentV2, Container container, String userTimeZone) {
        List<ShipmentV2> shipments = container.getShipmentsV2() == null ? new ArrayList<>()
                : container.getShipmentsV2();
        boolean isShipmentExists = false;
        final String shipmentId = shipmentV2.getId();
        if (!shipments.isEmpty()) {
            Optional<ShipmentV2> optionalShipmentV2 = shipments.stream().filter(shipmentV2Object -> shipmentV2Object.getId().equals(shipmentId)).findAny();
            if (optionalShipmentV2.isPresent()) {
                isShipmentExists = true;
                shipments.remove(optionalShipmentV2.get());
                shipments.add(shipmentV2);
            }
        }
        if (!isShipmentExists) {
            shipments.add(shipmentV2);
        }
        shipments = CommonUtil.sortContainerShipments(shipments);
        container.setShipmentsV2(shipments);
        AtomicReference<String> deliveryPta = null;
        int shipmentIndex = container.getShipmentsV2().indexOf(shipmentV2);
        long noOfShipments = shipments.size();
        long noOfCompletedShipments = shipments.stream()
                .filter(shipmentV2Object -> shipmentV2Object.getStatus().name().equals(StatusEnum.COMPLETED.name()) || shipmentV2Object.getStatus().name().equals(StatusEnum.CLOSED.name())).count();
        if (noOfShipments == noOfCompletedShipments) {
            container.setStatus(StatusEnum.COMPLETED);
        } else {
            if (StatusEnum.getInTransitStatuses().contains(shipmentV2.getStatus().name())) {
                container.setStatus(shipmentV2.getStatus());
                container.setContainerStatus("Leg " + (shipmentIndex + 1) + " " + shipmentV2.getStatus());
            }
            List<ShipmentV2> shipmentV2List = container.getShipmentsV2();
            for (ShipmentV2 shipment : shipmentV2List) {
                for (ShipmentStopV2 shipmentStopV2 : shipment.getStops()) {
                    if (shipmentStopV2.getStopType().equals("D")) {
                        List<LoadReference> loadReferences = shipment.getLoadReferences();
                        for (LoadReference loadReference : loadReferences) {
                            if (loadReference.getContent() != null && loadReference.getContent().equals("MAIN CARRIAGE")) {
                                container.setDeliveryPta(DateUtil.formatDate(DateUtil.convertDate(shipmentStopV2.getPlannedArrival().getDateTime(),
                                                DateUtil.dateTimeFormatter2, shipmentStopV2.getPlannedArrival().getTZId(), userTimeZone),
                                        DateUtil.dateTimeFormatter1));
                            }
                        }
                    }
                }
            }
        }
        containerRepository.save(container);
    }

    private void raiseOutBountEvent(ShipmentV2 shipment, ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings
            applicationSettings) {
        OutBoundEventDoc doc = outBoundEventRepository.findTopByOrderByCreatedDateDesc();
        String eventId = "0000000001";
        if (doc != null) {
            eventId = doc.getFeedBean().getFeedHeader().getFeedRefNum();
            eventId = generateEventId(eventId);
        }
        FeedBean feedBean = new FeedBean();
        FeedHeaderBean feedHeader = new FeedHeaderBean();
        feedHeader.setFeedRefNum(eventId);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        feedHeader.setFeedGenDtTime(sdf.format(new Date()));
        FeedContentBean feedContent = new FeedContentBean();
        LocationBean location = new LocationBean(shipmentStatusDoc.getLocation().getX(),
                shipmentStatusDoc.getLocation().getY());
        EventDtBean eventDate = new EventDtBean(shipmentStatusDoc.getEventDate().otmGLogDate,
                shipment.getStartDate().getTZId());
        List<ILMCTOutXML> iLMCTOutXMLList = new ArrayList<>();
        String statusMod = shipment.getStatus().name().replaceAll("_", " ");
        StatusUpdateBean StatusUpdate = new StatusUpdateBean(shipment.getLoadID(), statusMod);
        ILMCTOutXML iLMCTOutXML1 = new ILMCTOutXML(StatusUpdate, null, null, null, null);
        iLMCTOutXMLList.add(iLMCTOutXML1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.dateTimeFormatter2);
        for (ShipmentStopV2 stop : shipment.getStops()) {
            if (!stop.isDelivered()) {
                LocalDateTime convertedTimeZone = null;
                if (stop.getCalculatedETA() != null) {
                    convertedTimeZone = DateUtil.convertDate(stop.getCalculatedETA().getDateTime(),
                            DateUtil.dateTimeFormatter2, stop.getCalculatedETA().getTZId(), stop.getPlannedArrival().getTZId());
                }
                ETABean etaBean = null;
                etaBean = new ETABean(convertedTimeZone != null ? convertedTimeZone.format(formatter) : null,
                        stop.getPlannedArrival().getTZId());
                long activityTimeInMin = stop.getStopType().equalsIgnoreCase("P")
                        ? applicationSettings.getShipmentPickupActivityTime()
                        : applicationSettings.getShipmentActivityTime();
                LocalDateTime etdTime = convertedTimeZone != null ? convertedTimeZone.plusMinutes(activityTimeInMin)
                        : null;

                ETDBean etdBean = new ETDBean(etdTime != null ? etdTime.format(formatter) : null,
                        stop.getPlannedArrival().getTZId());
                String truckNumber = null;
                Optional<LoadReference> optionalLoadReference = shipment.getLoadReferences().stream().filter(loadReference ->
                        loadReference.getLoadReferenceType().equals("VehicleNumber")).findAny();
                if (optionalLoadReference.isPresent()) {
                    truckNumber = optionalLoadReference.get().getContent();
                }

                ETAUpdateBean ETAUpdate = new ETAUpdateBean(shipment.getLoadID(), stop.getStopNumber(), location,
                        eventDate, truckNumber != null ? truckNumber : "", etaBean, etdBean);
                ILMCTOutXML iLMCTOutXML2 = new ILMCTOutXML(null, ETAUpdate, null, null, null);
                iLMCTOutXMLList.add(iLMCTOutXML2);

            }
        }

        feedContent.setILMCTOutXML(iLMCTOutXMLList);
        feedBean.setFeedHeader(feedHeader);
        feedBean.setFeedContent(feedContent);
        OutBoundEventDoc outBoundEventDoc = new OutBoundEventDoc();
        outBoundEventDoc.setFeedBean(feedBean);
        outBoundEventRepository.save(outBoundEventDoc);
        generateOutBoundXml(outBoundEventDoc, applicationSettings);
    }

    private String generateEventId(String eventId) {
        long n = Long.parseLong(eventId);
        int count = 10;
        String n1 = String.valueOf(n + 1);
        int loop = count - n1.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 1; i <= loop; i++) {
            temp.append("0");
        }
        temp.append(n1);
        return temp.toString();
    }

    public void generateOutBoundXml(OutBoundEventDoc outBoundEventDoc, ApplicationSettings applicationSettings) {
        String outBoundEventXML = CommonUtil.convertObjectToXML(outBoundEventDoc.getFeedBean(), FeedBean.class);
        if (!outBoundEventXML.isEmpty()) {
            String eventStylisedXML = CommonUtil.stylizer("OutBoundStylesheet_v2.xsl", outBoundEventXML);
            postOutBoundEventData(eventStylisedXML, outBoundEventDoc, applicationSettings);
        }
    }

    private String postOutBoundEventData(String eventStylisedXML, OutBoundEventDoc
            outBoundEventDoc, ApplicationSettings applicationSettings) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            String username = applicationSettings.getOtmUsername();
            String password = applicationSettings.getOtmPassword();
            HttpEntity<String> requestEntity = new HttpEntity<>(eventStylisedXML, createHeaders(username, password));

            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                    requestEntity, String.class);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                result = responseEntity.getBody();
                logger.info("==================== OUT BOUND ETA,STATUS UPDATE EVENT RESPONSE ======================");
                logger.info(result);
                outBoundEventDoc.setOutBoundEventResponse(result);
                outBoundEventRepository.save(outBoundEventDoc);
            }
        } catch (HttpClientErrorException ex) {

        }
        return result;
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
    }
    private void statusBaseEventAction(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings applicationSettings, ShipmentV2 shipmentV2, boolean hazardous) throws ApplicationSettingsNotFoundException {
        Boolean isIlmctTrackingProvider = shipmentV2.getTrackingProvider().equalsIgnoreCase("ILMCT") ? Boolean.TRUE : Boolean.FALSE;
        TimeEstimationBean shipmentETAObj = new TimeEstimationBean();
        if (!Constants.TRACKINGPROVIDERETASTATUSES.contains(shipmentStatusDoc.getStatusCodeId())
                && !shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())) {

            shipmentETAObj = updateDistanceCalculation(shipmentV2, applicationSettings, shipmentStatusDoc, hazardous);
        }
        ShipmentStopV2 firstPickupStop = Utility.getFirstStop(shipmentV2);
        if (!firstPickupStop.isDelivered()) {
            if (isIlmctTrackingProvider) {
                updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
            }
            shipmentV2.getStops().set(0, firstPickupStop);
        }
        switch (shipmentStatusDoc.getStatusCodeId()) {
            case KEY_STOP_GATE_IN:
                gateInEventStatusUpdate(shipmentStatusDoc, shipmentV2, applicationSettings);
                if (isIlmctTrackingProvider) {
                    updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
                }
                break;
            case KEY_STOP_GATE_OUT:
                gateOutEventStatusUpdate(shipmentStatusDoc, shipmentV2);
                if (isIlmctTrackingProvider) {
                    updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
                }
                break;
            case GVIT_STATUS:
                if (isIlmctTrackingProvider) {
                    updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
                }
                gvitEvent(shipmentStatusDoc, shipmentV2, applicationSettings);
                break;
            case KEY_DELIVERY_STOP_ARRIVAL:
                deliveryStopArrivalEventStatusUpdate(shipmentStatusDoc, shipmentV2, applicationSettings);
                if (isIlmctTrackingProvider) {
                    updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
                }
                break;
            case KEY_STOP_DELIVERED:
                deliveredEventStatusUpdate(shipmentStatusDoc, shipmentV2);
                if (isIlmctTrackingProvider) {
                    updateETAForSubSequentStops(shipmentETAObj, shipmentV2, applicationSettings, shipmentStatusDoc);
                }
                break;
            case KEY_STATUS_COMPLETED:
                closedEventStatusUpdate(shipmentStatusDoc, applicationSettings, shipmentV2);
                break;
            case TRACKINGPROVIDERETASTATUSES1:
            case TRACKINGPROVIDERETASTATUSES2:
                nonIlmctTrackingProviderEventStatusUpdate(shipmentStatusDoc, shipmentV2, applicationSettings);
                break;
        }
    }

    private void gateInEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2, ApplicationSettings applicationSettings) {
        ShipmentStopV2 shipmentStopV2 = Utility.getStopMatchesWithSequence(shipmentV2, shipmentStatusDoc.getStopSequence());
        if (shipmentStopV2 != null && !shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
            int index = shipmentV2.getStops().indexOf(shipmentStopV2);
            shipmentStopV2.setActualArrival(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc.
                    getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2), null));
            Long activityTime = 0L;
            if (shipmentStopV2.getStopType().equalsIgnoreCase("P")) {
                activityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
            }
            TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(activityTime,
                    DateUtil.stringToLocalDateTime(shipmentStopV2.getActualArrival().getDateTime(),
                            DateUtil.dateTimeFormatter2));
            shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.convertDate(stopDepartureTimeEstimation.
                    getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
                    , null));
            shipmentStopV2.setGateInEventOn(DateUtil.convertDate(LocalDateTime.now().toString(),
                    DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2)
            );
            shipmentV2.getStops().set(index, shipmentStopV2);
        }
    }

    private void gateOutEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2) {
        logger.info("SaveShipment Status :: Stop Gate Out (AF) Event Started");
        ShipmentStopV2 shipmentStopV2 = Utility.getStopMatchesWithSequence(shipmentV2, shipmentStatusDoc.getStopSequence());
        if (shipmentStopV2 != null && !shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
            int index = shipmentV2.getStops().indexOf(shipmentStopV2);
            shipmentStopV2.setGateOutEventOn(
                    DateUtil.convertDate(LocalDateTime.now().toString(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2));
            if (shipmentStopV2.getActualDeparture() == null) {
                shipmentStopV2.setActualDeparture(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc.getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2)
                        , null));
                if (shipmentStopV2.getActualArrival() == null) {
                    shipmentStopV2.setActualArrival(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc.getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2),
                            null));
                    if (shipmentStopV2.getCalculatedETD() == null) {
                        shipmentStopV2.setCalculatedETD(shipmentStopV2.getActualArrival());
                    } else
                        shipmentStopV2.getCalculatedETD().setDateTime(shipmentStopV2.getActualArrival().getDateTime());
                }
            }
            shipmentStopV2.setDelivered(Boolean.TRUE);
            shipmentV2.getStops().set(index, shipmentStopV2);
        }
        logger.info("SaveShipment Status :: Stop Gate Out (AF) Event Ended");
    }

    private void updateETAForSubSequentStops(TimeEstimationBean stopsETAObj, ShipmentV2
            shipmentV2, ApplicationSettings applicationSettings, ShipmentStatusDoc shipmentStatusDoc) {
        LocalDateTime lastShipmentEndDate = shipmentStatusDoc.getEventDateAsLocalDate(null);
        List<ShipmentStopV2> sortedStops = Utility.getSortedStopsV2(shipmentV2);
        TimeEstimationBean stopTimeEstimation = null;
        for (ShipmentStopV2 otmShipmentStop : sortedStops) {
            Integer stopSequence = otmShipmentStop.getStopNumber();
            int index = sortedStops.indexOf(otmShipmentStop);
            if (otmShipmentStop.isDelivered()) {
                shipmentV2.getStops().set(index, otmShipmentStop);
            } else {
                try {
                    if (stopsETAObj != null && stopsETAObj.getRoutePoints() != null && !Objects.requireNonNull(stopsETAObj).getRoutePoints().isEmpty()) {
                        RoutePointBean routePointBean = stopsETAObj.getRoutePoints().stream()
                                .filter(stopPoint -> stopSequence.equals(stopPoint.getSequence()))
                                .findFirst().orElse(null);
                        if (routePointBean != null) {
                            stopTimeEstimation = new TimeEstimationBean(routePointBean, lastShipmentEndDate);
                            otmShipmentStop.getCalculatedETA().setDateTime(DateUtil.formatDate(stopTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2));
                            Long activityTime = 0l;
                            if (otmShipmentStop.getStopType().equalsIgnoreCase("D")) {
                                activityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
                            }
                            if (otmShipmentStop.getStopType().equalsIgnoreCase("P")) {
                                activityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
                            }
                            TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(activityTime,
                                    stopTimeEstimation.getEstimatedDate());
                            otmShipmentStop.getCalculatedETD().setDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2));
                        }
                    } else {
                        stopTimeEstimation = null;
                    }
                    if (otmShipmentStop.getCalculatedETD() != null && otmShipmentStop.getCalculatedETA() != null
                            && otmShipmentStop.getCalculatedETA().getDateTime() != null) {
                        lastShipmentEndDate = DateUtil.convertDate(otmShipmentStop.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2, otmShipmentStop.getCalculatedETA().getTZId(), null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                shipmentV2.getStops().set(index, otmShipmentStop);
            }

        }

    }

    private void deliveredEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2) {
        logger.info("SaveShipment Status :: Stop Delivered (D1) Event Started");
        ShipmentStopV2 shipmentStopV2 = Utility.getStopMatchesWithSequence(shipmentV2, shipmentStatusDoc.getStopSequence());
        if (shipmentStopV2 != null && shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
            int index = shipmentV2.getStops().indexOf(shipmentStopV2);
            shipmentStopV2.setDelivered(true);
            LocalDateTime localDateTimeEvent = DateUtil.convertDate(shipmentStatusDoc.getEventDate().otmGLogDate,
                    DateUtil.dateTimeFormatter2, shipmentStatusDoc.getEventDate().otmTZId, null);
            String eventDate = DateUtil.formatDate(localDateTimeEvent, DateUtil.dateTimeFormatter2);
            if (shipmentStopV2.getCalculatedETD() != null) {
                shipmentStopV2.getCalculatedETD().setDateTime(eventDate);
            } else {
                ShipmentDateTime calculatedEtd = new ShipmentDateTime(eventDate, shipmentStatusDoc.getEventDate().otmTZId);
                shipmentStopV2.setCalculatedETD(calculatedEtd);
            }
            shipmentStopV2.setActualDeparture(new ShipmentDateTime(eventDate, null));
            if (shipmentStopV2.getActualArrival() == null) {
                shipmentStopV2.setActualArrival(new ShipmentDateTime(eventDate, null));
            }
            shipmentV2.getStops().set(index, shipmentStopV2);
        }
        logger.info("SaveShipment Status :: Stop Delivered (D1) Event Ended");
    }

    private void closedEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings applicationSettings, ShipmentV2 shipmentV2) {
        logger.info("SaveShipment Status :: Stop Closed Event Started");
        ShipmentStopV2 lastStop = Utility.getLastStop(shipmentV2);
        if (lastStop != null && lastStop.isDelivered() &&
                lastStop.getStopNumber().equals(shipmentStatusDoc.getStopSequence())) {
            findTypeOfDelivery(shipmentV2, applicationSettings);
            shipmentV2.setStatus(StatusEnum.COMPLETED);
        }
        logger.info("SaveShipment Status :: Stop Closed  Event Ended");
    }

    public ShipmentV2 findTypeOfDelivery(ShipmentV2 shipment
            , ApplicationSettings applicationSettings) {
        List<ShipmentStopV2> sortedStops = Utility.getSortedStopsV2(shipment);
        if (!shipment.getStatus().equals(StatusEnum.COMPLETED)) {

            ShipmentStopV2 lastShipment = sortedStops.get(sortedStops.size() - 1);
            LocalDateTime lastShipmentEndDate = DateUtil.convertDate(lastShipment.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2, lastShipment.getCalculatedETA().getTZId(), null);

            int delayMinutesConsider;
            String delayType = "";
            Optional<LoadReference> optionalLoadReference = shipment.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType().equals("LeadTime")).findFirst();
            if (optionalLoadReference.isPresent()) {
                delayType = optionalLoadReference.get().getContent();
            }
            LocalDateTime fromDate = DateUtil.convertDate(lastShipment.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, lastShipment.getEstimatedArrival().getTZId(), null);
            LocalDateTime toDate = DateUtil.convertDate(lastShipment.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2, lastShipment.getEstimatedArrival().getTZId(), null);

            long minutes = ChronoUnit.MINUTES.between(fromDate, toDate);
            switch (delayType) {
                case "SHORT_LEAD_TIME":
                    delayMinutesConsider = applicationSettings.getShipmentShortDelayedTime();
                    break;
                case "AVERAGE_LEAD_TIME":
                    delayMinutesConsider = applicationSettings.getShipmentAvgDelayedTime();
                    break;
                case "LONG_LEAD_TIME":
                    delayMinutesConsider = applicationSettings.getShipmentLongDelayedTime();
                    break;
                default:
                    delayMinutesConsider = applicationSettings.getShipmentDelayedTime();
                    break;
            }
            if (shipment.getEndDate() != null && shipment.getEndDate().getDateTime() != null) {
                LocalDateTime localDateTime = DateUtil.convertDate(shipment.getEndDate().getDateTime(), DateUtil.dateTimeFormatter2, shipment.getEndDate().getTZId(), null);
                if (localDateTime != null && lastShipmentEndDate != null) {
                    shipment.setTypeOfDelivery(localDateTime
                            .plusMinutes(delayMinutesConsider).isBefore(lastShipmentEndDate)
                            ? StatusEnum.DELAYED
                            : StatusEnum.STARTED);
                }
            }
            if (minutes > delayMinutesConsider) {
                shipment.setTypeOfDelivery(StatusEnum.DELAYED);
            } else if (minutes >= 0 && minutes <= delayMinutesConsider) {
                shipment.setTypeOfDelivery(StatusEnum.ONTIME);
            } else if (minutes < 0) {
                shipment.setTypeOfDelivery(StatusEnum.ARRIVING_EARLY);
            }
        }
        return shipment;
    }


    private void nonIlmctTrackingProviderEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2, ApplicationSettings applicationSettings) {
        logger.info("SaveShipment Status :: Stop Tracking Provider Events (X6/AG) Event Started");
        ShipmentStopV2 shipmentStopV2 = Utility.getStopMatchesWithSequence(shipmentV2, shipmentStatusDoc.getStopSequence());
        if (shipmentStopV2 != null && !shipmentV2.getTrackingProvider().equalsIgnoreCase("ILMCT") &&
                shipmentStatusDoc.otmflexFieldDates != null && shipmentStatusDoc.otmflexFieldDates.AttributeDate2 != null) {
            int index = shipmentV2.getStops().indexOf(shipmentStopV2);
            LocalDateTime trackingProviderEstimatedDate = DateUtil.convertDate(
                    shipmentStatusDoc.otmflexFieldDates.AttributeDate2.otmGLogDate, DateUtil.dateTimeFormatter2,
                    shipmentStatusDoc.otmflexFieldDates.AttributeDate2.otmTZId, null);
            if (trackingProviderEstimatedDate != null) {
                shipmentStopV2.getCalculatedETA().setDateTime(DateUtil.formatDate(trackingProviderEstimatedDate, DateUtil.dateTimeFormatter2));
            }
            Long activityTime = 0L;
            if (shipmentStopV2.getStopType().equalsIgnoreCase("P")) {
                activityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
            }
            if (shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
                activityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
            }
            TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(
                    Long.valueOf(applicationSettings.getShipmentActivityTime()),
                    DateUtil.stringToLocalDateTime(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2));
            shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2), null));
            shipmentV2.getStops().set(index, shipmentStopV2);
        }
        logger.info("SaveShipment Status :: Stop Tracking Provider Events (X6/AG) Event Ended");
    }


    public void gvitEvent(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2, ApplicationSettings applicationSettings) throws ApplicationSettingsNotFoundException {
        logger.info("SaveShipment Status :: GVIT Events Started");
        String truckNumber = null;
        Optional<LoadReference> optionalLoadReference = shipmentV2.getLoadReferences().stream().filter(loadReference ->
                loadReference.getLoadReferenceType().equals(KEY_TRUCK_NO)).findAny();
        if (optionalLoadReference.isPresent()) {
            truckNumber = optionalLoadReference.get().getContent();
        }
        List<ShipmentStopV2> sortedStops = Utility.getSortedStopsV2(shipmentV2);
        shipmentV2.setLastGVITEventTriggeredOn(DateUtil.getStartDate(DateUtil.formatDateWithSystemTimeZone(shipmentStatusDoc.getEventDate().otmGLogDate)));
        fenceEligibleEvents(shipmentStatusDoc, applicationSettings, shipmentV2, truckNumber, sortedStops);
        logger.info("SaveShipment Status :: GVIT Events Ended");

    }

    private void fenceEligibleEvents(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings
            applicationSettings, ShipmentV2 shipmentV2, String truckNumber, List<ShipmentStopV2> sortedStops) throws ApplicationSettingsNotFoundException {
        ShipmentStopV2 fenceInShipmentStop = findGeoFenceInForStops(shipmentV2, sortedStops,
                shipmentStatusDoc.getLocation());
        if (fenceInShipmentStop == null) {
            ShipmentStopV2 fenceOutShipmentStop = findGeoFenceOutForStops(shipmentV2, sortedStops,
                    shipmentStatusDoc.getLocation());
            if (fenceOutShipmentStop != null) {
                try {
                    ifStopReachedOutToTheFence(shipmentStatusDoc, shipmentV2, truckNumber, fenceOutShipmentStop, applicationSettings);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                updatingGeoFenceDocInStatusDoc(shipmentStatusDoc, applicationSettings, shipmentV2, truckNumber, fenceInShipmentStop);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public ShipmentStopV2 findGeoFenceInForStops(ShipmentV2 shipment, List<ShipmentStopV2> sortedStops, LatLng
            latLng) throws ApplicationSettingsNotFoundException {
        int firstSeqNo = 0;
        if (sortedStops.size() > 0) {
            firstSeqNo = sortedStops.get(0).getStopNumber();
        }
        for (ShipmentStopV2 otmShipmentStop : sortedStops) {
            List<GeoFenceEventDoc> docs = geoFenceEventRepository
                    .findAllByShipmentIdAndShipmentStopV2StopNumberAndFenceEventType(shipment.getId(),
                            otmShipmentStop.getStopNumber(), FencingEventType.FENCE_IN);
            if (docs.size() == 0 && otmShipmentStop.getStopNumber() != firstSeqNo) {
                LocationDoc locDoc = cacheService
                        .findLocationBySiteId(otmShipmentStop.getLocation().getSiteId());
                Optional<GeofenceDoc> geofenceDoc = geofencingDocRepository.findByLocationDocId(locDoc.getId());
                if (geofenceDoc != null) {
                    boolean isGeoIn = geofenceService.locateShipmentV2(geofenceDoc.get(), latLng, "in");
                    if (isGeoIn) {
                        return otmShipmentStop;
                    }
                }
            }
        }
        return new ShipmentStopV2();
    }

    private ShipmentStopV2 findGeoFenceOutForStops(ShipmentV2 shipment, List<ShipmentStopV2> sortedStops, LatLng
            latLng) throws ApplicationSettingsNotFoundException {
        ShipmentStopV2 fenceInStop = null;
        for (ShipmentStopV2 otmShipmentStop : sortedStops) {
            LocationDoc locDoc = cacheService
                    .findLocationBySiteId(otmShipmentStop.getLocation().getSiteId());
            Optional<GeofenceDoc> geofenceDoc = geofencingDocRepository.findByLocationDocId(locDoc.getId());
            if (geofenceDoc != null) {
                boolean isGeoIn = geofenceService.locateShipmentV2(geofenceDoc.get(), latLng, "in");
                if (isGeoIn) {
                    fenceInStop = otmShipmentStop;
                }
            }
        }
        int lastSeqNo = 0;
        if (!sortedStops.isEmpty()) {
            lastSeqNo = sortedStops.get(sortedStops.size() - 1).getStopNumber();
        }
        for (ShipmentStopV2 otmShipmentStop : sortedStops) {
            if (loopStopsToGetGeoFenceDocs(shipment, latLng, fenceInStop, lastSeqNo, otmShipmentStop))
                return otmShipmentStop;
        }
        return new ShipmentStopV2();
    }

    private boolean loopStopsToGetGeoFenceDocs(ShipmentV2 shipment, LatLng latLng, ShipmentStopV2 fenceInStop,
                                               int lastSeqNo, ShipmentStopV2 otmShipmentStop) throws ApplicationSettingsNotFoundException {
        List<GeoFenceEventDoc> docs = geoFenceEventRepository
                .findAllByShipmentIdAndShipmentStopV2StopNumberAndFenceEventType(shipment.getId(),
                        otmShipmentStop.getStopNumber(), FencingEventType.FENCE_IN);
        List<GeoFenceEventDoc> outDocs = geoFenceEventRepository
                .findAllByShipmentIdAndShipmentStopV2StopNumberAndFenceEventType(shipment.getId(),
                        otmShipmentStop.getStopNumber(), FencingEventType.FENCE_OUT);
        if (outDocs.isEmpty() && otmShipmentStop.getStopNumber() != lastSeqNo
                && (otmShipmentStop.getStopType().equalsIgnoreCase("P")
                || (!docs.isEmpty() && otmShipmentStop.getStopType().equalsIgnoreCase("D")))) {
            LocationDoc locDoc = cacheService
                    .findLocationBySiteId(otmShipmentStop.getLocation().getSiteId());
            Optional<GeofenceDoc> geofenceDoc = geofencingDocRepository.findByLocationDocId(locDoc.getId());
            if (geofenceDoc != null) {
                boolean isGeoOut = geofenceService.locateShipmentV2(geofenceDoc.get(), latLng, "out");
                return isGeoOut && (fenceInStop == null || !fenceInStop.getStopNumber().equals(otmShipmentStop.getStopNumber()));
            }

        }
        return false;
    }


    private void ifStopReachedOutToTheFence(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2, String
            truckNumber, ShipmentStopV2 fenceOutShipmentStop, ApplicationSettings applicationSettings) throws ParseException {
        GeoFenceEventDoc doc = raiseFenceOutEvent(fenceOutShipmentStop, shipmentStatusDoc,
                shipmentV2, truckNumber, applicationSettings);
        shipmentStatusDoc.setStatusCodeId(doc.getFenceEventType().name());
        shipmentStatusDoc.setStopSequence(fenceOutShipmentStop.getStopNumber());
        shipmentStatusDoc.setGeoFenceEventDoc(doc);
        shipmentV2.getStops().stream().filter(otmShipmentStop -> otmShipmentStop.getStopNumber().equals(fenceOutShipmentStop.getStopNumber()))
                .findAny().ifPresent(otmShipmentStop -> {
                    otmShipmentStop.setActualDeparture(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc
                            .getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2), null));
                    if (otmShipmentStop.getActualArrival() == null) {
                        otmShipmentStop.setActualArrival(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc
                                .getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2), null));
                        TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(0l,
                                DateUtil.convertDate(otmShipmentStop.getActualArrival().getDateTime(), DateUtil.dateTimeFormatter2, otmShipmentStop.getActualArrival().getTZId(), null));
                        otmShipmentStop.getCalculatedETD().setDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2));
                    }
                });

    }

    private GeoFenceEventDoc raiseFenceOutEvent(ShipmentStopV2 fenceOutShipmentStop,
                                                ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipment, String truckNumber, ApplicationSettings
                                                        applicationSettings) throws ParseException {

        GeoFenceEventDoc doc = geoFenceEventRepository.findTopByOrderByCreatedDateDesc();
        String eventId = "0000000001";

        if (doc != null) {
            eventId = doc.getEventId();
            eventId = generateEventId(eventId);
        }
        GeoFenceEventDoc geoFenceEventDoc = new GeoFenceEventDoc();
        geoFenceEventDoc.setEventId(eventId);
        geoFenceEventDoc.setShipmentStopV2(fenceOutShipmentStop);
        geoFenceEventDoc.setLoadId(shipmentStatusDoc.getLoadId());
        geoFenceEventDoc.setShipmentId(shipment.getId());
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        geoFenceEventDoc.setTriggeredOn(sdf.parse(shipmentStatusDoc.getEventDate().otmGLogDate));
        geoFenceEventDoc.setTimeZone(shipmentStatusDoc.getEventDate().otmTZId);
        geoFenceEventDoc.setTimeZone(fenceOutShipmentStop.getPlannedArrival().getTZId());
        geoFenceEventDoc.setLatitude(shipmentStatusDoc.getLocation().getX());
        geoFenceEventDoc.setLongitude(shipmentStatusDoc.getLocation().getY());
        geoFenceEventDoc.setTruckId(truckNumber);
        geoFenceEventDoc.setFenceEventType(FencingEventType.FENCE_OUT);
        geoFenceEventDoc.setGLogDate(shipmentStatusDoc.getEventDate().otmGLogDate);
        geoFenceEventDoc.setGLogTimeZone(shipmentStatusDoc.getEventDate().otmTZId);
        geoFenceEventRepository.save(geoFenceEventDoc);
        generateFenceEventXml(geoFenceEventDoc, shipmentStatusDoc, applicationSettings);
        return geoFenceEventDoc;
    }

    public void generateFenceEventXml(GeoFenceEventDoc geoFenceEventDoc, ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings applicationSettings) {
        FeedBean feedBean = new FeedBean();
        FeedHeaderBean feedHeader = new FeedHeaderBean();
        feedHeader.setFeedRefNum(geoFenceEventDoc.getEventId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.dateTimeFormatter2);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        feedHeader.setFeedGenDtTime(sdf.format(now));
        FeedContentBean feedContent = new FeedContentBean();
        LocationBean location = new LocationBean(geoFenceEventDoc.getLatitude(), geoFenceEventDoc.getLongitude());
        EventDtBean eventDate = new EventDtBean(sdf.format(geoFenceEventDoc.getTriggeredOn()),
                geoFenceEventDoc.getTimeZone());
        LocalDateTime convertedTimeZone = DateUtil.convertDate(shipmentStatusDoc.getEventDate().otmGLogDate,
                DateUtil.dateTimeFormatter2, shipmentStatusDoc.getEventDate().otmTZId, geoFenceEventDoc.getTimeZone());
        logger.debug(convertedTimeZone + " converted timezone");
        EventDtBean fenceEventDate = null;
        if (convertedTimeZone != null) {
            fenceEventDate = new EventDtBean(convertedTimeZone.format(formatter),
                    geoFenceEventDoc.getTimeZone());
        }
        GeoFenceEvent geoFenceEvent = new GeoFenceEvent(geoFenceEventDoc.getLoadId(),
                geoFenceEventDoc.getShipmentStopV2().getStopNumber(),
                geoFenceEventDoc.getFenceEventType().eventXMLFormattedValue(), location, fenceEventDate,
                geoFenceEventDoc.getTruckId());
        feedContent.setGeoFenceEvent(geoFenceEvent);
        feedBean.setFeedHeader(feedHeader);
        feedBean.setFeedContent(feedContent);
        String fenceEventXML = CommonUtil.convertObjectToXML(feedBean, FeedBean.class);
        logger.info(fenceEventXML);
        if (!fenceEventXML.isEmpty()) {
            String eventStylisedXML = Utility.stylizer("GeoFenceStylesheet_v2.xsl", fenceEventXML);
            logger.info(eventStylisedXML);
            postFenceEventData(eventStylisedXML, geoFenceEventDoc, applicationSettings);
        }
    }

    private String postFenceEventData(String eventStylisedXML, GeoFenceEventDoc
            geoFenceEventDoc, ApplicationSettings applicationSettings) {

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
        String result = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        String username = applicationSettings.getOtmUsername();
        String password = applicationSettings.getOtmPassword();
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(eventStylisedXML, createHeaders(username, password));
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                    requestEntity, String.class);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                result = responseEntity.getBody();
                logger.info("==================== OTM SHIPMENT STATUS FENCE EVENT RESPONSE ======================");
                logger.info(result);
                geoFenceEventDoc.setOtmGeoFenceEventResponse(result);
                geoFenceEventRepository.save(geoFenceEventDoc);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    private void updatingGeoFenceDocInStatusDoc(ShipmentStatusDoc shipmentStatusDoc, ApplicationSettings
            applicationSettings, ShipmentV2 shipmentV2, String truckNumber, ShipmentStopV2 fenceInShipmentStop) throws
            ParseException {
        GeoFenceEventDoc doc = raiseFenceInEvent(fenceInShipmentStop, shipmentStatusDoc, shipmentV2, truckNumber, applicationSettings);
        shipmentStatusDoc.setStatusCodeId(doc.getFenceEventType().name());
        shipmentStatusDoc.setStopSequence(fenceInShipmentStop.getStopNumber());
        shipmentStatusDoc.setGeoFenceEventDoc(doc);
        shipmentV2.getStops().stream().filter(otmShipmentStop -> otmShipmentStop.getStopNumber().equals(fenceInShipmentStop.getStopNumber()))
                .findAny().ifPresent(otmShipmentStop -> {
                    otmShipmentStop.setActualArrival(new ShipmentDateTime(DateUtil.formatDate(shipmentStatusDoc
                            .getEventDateAsLocalDate(null), DateUtil.dateTimeFormatter2), null));
                    Long activityTime = otmShipmentStop.getStopType().equalsIgnoreCase("P")
                            ? Long.valueOf(applicationSettings.getShipmentPickupActivityTime())
                            : Long.valueOf(applicationSettings.getShipmentActivityTime());
                    TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(
                            activityTime,
                            DateUtil.convertDate(otmShipmentStop.getActualArrival().getDateTime(),
                                    DateUtil.dateTimeFormatter2, otmShipmentStop.getActualArrival().getTZId(), null));
                    otmShipmentStop.setCalculatedETD(new ShipmentDateTime(DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2), null));
                });

    }

    private GeoFenceEventDoc raiseFenceInEvent(ShipmentStopV2 fenceInShipmentStop, ShipmentStatusDoc
            shipmentStatusDoc,
                                               ShipmentV2 shipment, String truckNumber, ApplicationSettings applicationSettings) throws ParseException {

        GeoFenceEventDoc doc = geoFenceEventRepository.findTopByOrderByCreatedDateDesc();
        String eventId = "0000000001";

        if (doc != null) {
            eventId = doc.getEventId();
            eventId = generateEventId(eventId);
        }
        GeoFenceEventDoc geoFenceEventDoc = new GeoFenceEventDoc();
        geoFenceEventDoc.setEventId(eventId);
        geoFenceEventDoc.setShipmentStopV2(fenceInShipmentStop);
        geoFenceEventDoc.setLoadId(shipmentStatusDoc.getLoadId());
        geoFenceEventDoc.setShipmentId(shipment.getId());

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        geoFenceEventDoc.setTriggeredOn(sdf.parse(shipmentStatusDoc.getEventDate().otmGLogDate));
        geoFenceEventDoc.setTimeZone(fenceInShipmentStop.getPlannedArrival().getTZId());
        geoFenceEventDoc.setLatitude(shipmentStatusDoc.getLocation().getX());
        geoFenceEventDoc.setLongitude(shipmentStatusDoc.getLocation().getY());
        geoFenceEventDoc.setTruckId(truckNumber);
        geoFenceEventDoc.setFenceEventType(FencingEventType.FENCE_IN);
        geoFenceEventDoc.setGLogDate(shipmentStatusDoc.getEventDate().otmGLogDate);
        geoFenceEventDoc.setGLogTimeZone(shipmentStatusDoc.getEventDate().otmTZId);
        geoFenceEventRepository.save(geoFenceEventDoc);
        generateFenceEventXml(geoFenceEventDoc, shipmentStatusDoc, applicationSettings);
        return geoFenceEventDoc;

    }
    private void deliveryStopArrivalEventStatusUpdate(ShipmentStatusDoc shipmentStatusDoc, ShipmentV2 shipmentV2, ApplicationSettings applicationSettings) {
        logger.info("SaveShipment Status :: Stop Delivery Stop Arrival (X1) Event Started");
        ShipmentStopV2 shipmentStopV2 = Utility.getStopMatchesWithSequence(shipmentV2, shipmentStatusDoc.getStopSequence());
        if (shipmentStopV2 != null) {
            int index = shipmentV2.getStops().indexOf(shipmentStopV2);
            Long activityTime = 0L;
            if (shipmentStopV2.getStopType().equalsIgnoreCase("P")) {
                activityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
            }
            if (shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
                activityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
            }
            LocalDateTime localDateTimeEvent = DateUtil.convertDate(shipmentStatusDoc.getEventDate().otmGLogDate,
                    DateUtil.dateTimeFormatter2, shipmentStatusDoc.getEventDate().otmTZId, null);
            String eventDate = DateUtil.formatDate(localDateTimeEvent, DateUtil.dateTimeFormatter2);
            shipmentStopV2.setActualArrival(new ShipmentDateTime(eventDate, null));
            TimeEstimationBean stopDepartureTimeEstimation = new TimeEstimationBean(
                    activityTime, DateUtil.stringToLocalDateTime(shipmentStopV2.getActualArrival().getDateTime(),
                    DateUtil.dateTimeFormatter2));
            if (shipmentStopV2.getCalculatedETD() == null) {
                shipmentStopV2.setCalculatedETD(new ShipmentDateTime(DateUtil.convertDate(stopDepartureTimeEstimation.getEstimatedDateTime(), DateUtil.dateTimeFormatter1, DateUtil.dateTimeFormatter2), null));
            }
            shipmentStopV2.getCalculatedETD().setDateTime(
                    DateUtil.formatDate(stopDepartureTimeEstimation.getEstimatedDate(), DateUtil.dateTimeFormatter2));
            shipmentV2.getStops().set(index, shipmentStopV2);
        }
        logger.info("SaveShipment Status :: Stop Delivery Stop Arrival (X1) Event Ended");
    }

    private TimeEstimationBean updateDistanceCalculation(ShipmentV2 shipment, ApplicationSettings applicationSettings, ShipmentStatusDoc shipmentStatusDoc, Boolean hazardous) {
        LocalDateTime lastShipmentEndDate = shipmentStatusDoc.getEventDateAsLocalDate(null);
        List<ShipmentStopV2> sortedStops = Utility.getSortedStopsV2(shipment);
        Optional<ShipmentStatusDoc> lastEvent = shipmentStatusRepository
                .findTopByLoadIdAndStatusCodeIdNotOrderByEventDateOtmGLogDateAsc(shipment.getLoadID(), StatusEnum.INTRANSIT.name());

        Optional<CountryDoc> country = countryRepository.findUniqueByIso2CodeOrIso3Code(shipment.getSource().getCountryCode(), shipment.getSource().getCountryCode());

        DriverRestTimeDoc timing = null;
        boolean isChinaCountry = false;

        if (country.isPresent()) {
            if (country.get().getIso2Code().equalsIgnoreCase("CN")
                    || country.get().getIso2Code().equalsIgnoreCase("CHN")) {
                isChinaCountry = true;
            }
            timing = driverRepository.findByCountryAndStatus(country.get(), true);
        }
        TimeEstimationBean shipmentETAObj = null;
        // Calculate Distance Travelled
        if (lastEvent.isPresent() && lastEvent.get().getLocation() != null && shipmentStatusDoc.getLocation() != null) {

            List<ShipmentStatusDoc> events = shipmentStatusRepository.findByLoadIdOrderByEventDateOtmGLogDateAsc(shipment.getLoadID());
            RoutePointBean[] beans2 = new RoutePointBean[events.size()];
            for (int i = 0; i < events.size(); i++) {
                beans2[i] = new RoutePointBean(null, events.get(i).getLocation());
            }
            ShipmentStatusDoc lastEventDoc = events.get(events.size() - 1);
            if (lastEventDoc.getLocation() != null) {
                if (shipment.getCurrent() != null) {
                    shipmentStatusDoc.setTruckDirectionAngle(routingService.calculateBearing(shipment.getCurrent().getX(), shipment.getCurrent().getY(),
                            lastEventDoc.getLocation().getX(), lastEventDoc.getLocation().getY()));
                }
                shipment = calculateLeadTimeValues(shipment, applicationSettings, false, 0, null);
            }
            shipment.setCurrent(lastEventDoc.getLocation());

            shipmentETAObj = routingService.calculateRouteAndETAV3(CalculateRouteBean.Builder(Utility.parseDoubleOrNull(shipment.getLoadMeasure().getTotalWeight()), hazardous, DateUtil.convertDate(shipment.getStartDate().getDateTime(), DateUtil.dateTimeFormatter2, shipment.getStartDate().getTZId(), null), false,beans2), timing, shipment, isChinaCountry, applicationSettings);

            if (shipmentETAObj != null) {
                shipment.setDistanceTravelledInKms(shipmentETAObj.getDistance());
            }
        }
        // Calculate Distance To Travel & return TimeEstimation for further stops ETA
        final List<RoutePointBean> routePointBeans = new ArrayList<>();
        routePointBeans.add(new RoutePointBean(1, cacheService.findLocationBySiteId(sortedStops.get(0).getLocation().getSiteId()).getLocation()));
        ShipmentStopV2 lastShipmentStopV2 = sortedStops.get(sortedStops.size() - 1);
        routePointBeans.add(new RoutePointBean(lastShipmentStopV2.getStopNumber(), cacheService.findLocationBySiteId(lastShipmentStopV2.getLocation().getSiteId()).getLocation()));

        if (routePointBeans.size() > 1) {
            shipmentETAObj = routingService.calculateRouteAndETAV3(CalculateRouteBean.Builder(Utility.parseDoubleOrNull(shipment.getLoadMeasure().getTotalWeight()), hazardous, lastShipmentEndDate, false, routePointBeans.toArray(new RoutePointBean[routePointBeans.size()])), timing, shipment, isChinaCountry, applicationSettings);
            if (shipmentETAObj != null) {
                shipment.setDistancePendingInKms(shipmentETAObj.getDistance() - shipment.getDistanceTravelledInKms());
            }
        }
        return shipmentETAObj;
    }

    @Override
    public ShipmentV2 calculateLeadTimeValues(ShipmentV2 shipment, ApplicationSettings applicationSettings, Boolean isOrder, Integer leadTime, OrderV2Doc orderV2) {
        try {
            if (!shipment.getStatus().equals(StatusEnum.COMPLETED)) {
                ShipmentStopV2 shipmentStopV2;
                if (!isOrder) {
                    shipmentStopV2 = Utility.getLastDeliveryStop(shipment);
                    if (shipmentStopV2 == null) {
                        shipmentStopV2 = Utility.getLastStop(shipment);
                    }
                } else {
                    shipmentStopV2 = shipment.getStops().stream().filter(s -> s.getLocation().getSiteId().equalsIgnoreCase(orderV2.getShipToLocationId())).findFirst().get();
                }
                int delayMinutesConsider;
                String delayType = "";
                Optional<LoadReference> optionalLoadReference = shipment.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType().equals("LeadTime")).findFirst();
                if (optionalLoadReference.isPresent()) {
                    if (null != optionalLoadReference.get().getContent()) {
                        delayType = optionalLoadReference.get().getContent();
                    }
                }
                LocalDateTime
                        fromDate = DateUtil.convertDate(shipmentStopV2.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, shipmentStopV2.getPlannedArrival().getTZId(), null);
                LocalDateTime
                        toDate = DateUtil.convertDate(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2, null, null);

                long minutes = ChronoUnit.MINUTES.between(fromDate, toDate);
                if (!isOrder) {
                    switch (delayType) {
                        case "SHORT_LEAD_TIME":
                            delayMinutesConsider = applicationSettings.getShipmentShortDelayedTime();
                            break;
                        case "AVERAGE_LEAD_TIME":
                            delayMinutesConsider = applicationSettings.getShipmentAvgDelayedTime();
                            break;
                        case "LONG_LEAD_TIME":
                            delayMinutesConsider = applicationSettings.getShipmentLongDelayedTime();
                            break;
                        default:
                            delayMinutesConsider = applicationSettings.getShipmentDelayedTime();
                            break;
                    }
                } else delayMinutesConsider = leadTime;
                if (shipment.getEndDate() != null && shipment.getEndDate().getDateTime() != null) {
                    LocalDateTime localDateTime = DateUtil.convertDate(shipment.getEndDate().getDateTime(), DateUtil.dateTimeFormatter2, shipment.getEndDate().getTZId(), null);
                    if (localDateTime != null && toDate != null) {
                        shipment.setStatus(localDateTime
                                .plusMinutes(delayMinutesConsider).isBefore(toDate)
                                ? StatusEnum.DELAYED
                                : StatusEnum.STARTED);
                    }
                }
                if (minutes > delayMinutesConsider) {
                    shipment.setStatus(StatusEnum.DELAYED);
                } else if (minutes >= 0 && minutes <= delayMinutesConsider) {
                    shipment.setStatus(StatusEnum.ONTIME);
                } else if (minutes < 0) {
                    shipment.setStatus(StatusEnum.ARRIVING_EARLY);
                }
            logger.info("Shipment Last Stop Promised Date ETA: " + shipmentStopV2.getEstimatedArrival().getDateTime());
            logger.info("Shipment Selected Stop Delivery Date ETA: " + shipmentStopV2.getCalculatedETA().getDateTime());
            logger.info("Shipment Selected Stop and Last Stop Delivery ETA Difference in  Minutes :" + minutes);
            logger.info("Shipment Delay Type : " + delayType + " And Delay Consider Minutes :" + delayMinutesConsider + " And the Shipment Status :" + shipment.getStatus().name());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shipment = shipmentV2Repository.save(shipment);
        return shipment;
    }

    private Alerts fetchWeatherAlert(WeatherCalculationBean weatherCalculationBean, ApplicationSettings applicationSettings) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getHereMapWeatherURL());

        for (Field field : weatherCalculationBean.getClass().getDeclaredFields()) {
            Class t = field.getType();
            field.setAccessible(true);
            try {
                if (t.equals(List.class)) {
                    try {
                        List<String> products = (List<String>) field.get(weatherCalculationBean);
                        for (String product : products) {
                            builder.queryParam(field.getName(), product);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field.get(weatherCalculationBean) != null) {
                    builder.queryParam(field.getName(), field.get(weatherCalculationBean));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        builder.queryParam("app_id", applicationSettings.getHereMap_API_ID()).queryParam("app_code",
                applicationSettings.getHereMap_API_CODE());
        HttpEntity entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<WeatherAlertDTO> response = restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.GET, entity, WeatherAlertDTO.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Optional<WeatherAlertDTO> weatherAlertDTO = Optional.ofNullable(response.getBody());

                Optional<com.inspirage.ilct.dto.here.weather.Alerts> alert = weatherAlertDTO.map(WeatherAlertDTO::getAlerts);

                com.inspirage.ilct.dto.here.weather.Alerts alerts = new com.inspirage.ilct.dto.here.weather.Alerts();
                if (alert.isPresent()) {
                    alerts = alert.get();
                }
                Alerts finalAlerts = alerts;
                weatherAlertDTO.map(WeatherAlertDTO::getObservations).map(Observations::getLocation).ifPresent(l -> {
                    finalAlerts.setObservation(!l.isEmpty() ? l.get(0).getObservation() : null);
                });
                return alerts;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    @Override
    public ApiResponse addDocumentsToShipment(List<MultipartFile> files, String loadId, HttpServletRequest request, String comment) {
        if (Utility.isEmpty(loadId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid Load Id");
        }
        if (files.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "No document is provided");
        }
        return shipmentV2Repository.findOneByLoadID(loadId)
                .map(shipment -> {
                    try {
                        return uploadAndSaveDocuments(shipment, files, request, comment);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid Load Id"));
    }

    private ApiResponse uploadAndSaveDocuments(ShipmentV2 shipment, List<MultipartFile> files, HttpServletRequest request, String comment) throws IOException {
        List<FileDocument> fileDocuments = fileManagementService.uploadMultipleFiles(files, request, comment);
        if (fileDocuments.isEmpty()) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Problem uploading document");
        }
        List<String> newFileIds = fileDocuments.stream().map(FileDocument::getFileId).toList();
        shipment.getUploadDocuments().addAll(newFileIds);
        List<FileDocument> existingFileDocuments = shipment.getFileDocuments();
        existingFileDocuments.addAll(fileDocuments);
        existingFileDocuments.sort(Comparator.comparing(FileDocument::getCreatedDate).reversed());
        ShipmentV2 savedShipment = shipmentV2Repository.save(shipment);
        logger.info("Documents added to shipment successfully");
        return new ApiResponse(HttpStatus.OK, "Documents added successfully", savedShipment);
    }

    @Override
    @SneakyThrows
    public ApiResponse addEventToShipment(AddEventBean addEventBean, HttpServletRequest request) {
        User loginUser = userRepository.findOneByUserId(tokenUtilService.getUserId(request)).orElse(null);
        ShipmentStatusDoc shipmentStatusDoc = new ShipmentStatusDoc();

        String msg = "";
        if (Utility.isEmpty(addEventBean.getLoadId())) {
            msg = "Invalid LoadId";
        }
        if (Utility.isEmpty(addEventBean.getEventType())) {
            msg = "Invalid Event Type";
        }
        if (Utility.isEmpty(addEventBean.getDateAndTime())) {
            msg = "Invalid date and time";
        }

        if (Utility.isEmpty(addEventBean.getTimezone())) {
            msg = "Invalid timezone";
        }
        if (!addEventBean.getEventType().equalsIgnoreCase(GVIT_STATUS)) {
            if (addEventBean.getTruckSequence() == 0) {
                msg = "Invalid truck sequence";
            }
        }

        if (addEventBean.getEventType().equalsIgnoreCase(GVIT_STATUS)) {
            if (Utility.isEmpty(addEventBean.getLatitude())) {
                msg = "Invalid latitude";
            }
            if (Utility.isEmpty(addEventBean.getLongitude())) {
                msg = "Invalid longitude";
            }
            shipmentStatusDoc.setLocation(new LatLng(addEventBean.getLatitude(), addEventBean.getLongitude()));
        }

        shipmentStatusDoc.setLoadId(addEventBean.getLoadId());
        if (!addEventBean.getEventType().equalsIgnoreCase(Constants.GVIT_STATUS)) {
            shipmentStatusDoc.setStopSequence(addEventBean.getTruckSequence());
        }
        shipmentStatusDoc.setStatusCodeId(addEventBean.getEventType());
        shipmentStatusDoc.setEventDescription(addEventBean.getEventDescription());
        shipmentStatusDoc.setRemarks(addEventBean.getRemarks());
        shipmentStatusDoc.setAddress(addEventBean.getAddress()); // address pending

        OtmEventDt otmEventDt = new OtmEventDt();
        otmEventDt.setOtmGLogDate(DateUtil.formatDate(addEventBean.getDateAndTime(), DateUtil.dateTimeFormatter2));
        otmEventDt.setOtmTZId(addEventBean.getTimezone());

        shipmentStatusDoc.setEventDate(otmEventDt);

        if (addEventBean.getEventType().equalsIgnoreCase(TRACKINGPROVIDERETASTATUSES1) || addEventBean.getEventType().equalsIgnoreCase(TRACKINGPROVIDERETASTATUSES2)) {
            if (Utility.isEmpty(addEventBean.getOtmFlexDateAndTime())) {
                msg = "Invalid otm flex date and time";
            }
            if (Utility.isEmpty(addEventBean.getOtmFlexTimezone())) {
                msg = "Invalid otm flex timezone";
            }
            if (!Utility.isEmpty(msg)) {
                return new ApiResponse(HttpStatus.BAD_REQUEST,"something went wrong");
            }

            AttributeDate2 attributeDate2 = new AttributeDate2();
            attributeDate2.setOtmGLogDate(DateUtil.formatDate(addEventBean.getOtmFlexDateAndTime(), DateUtil.dateTimeFormatter2));
            attributeDate2.setOtmTZId(addEventBean.getOtmFlexTimezone());

            OtmFlexFieldDates otmFlexFieldDates = new OtmFlexFieldDates();
            otmFlexFieldDates.AttributeDate2 = attributeDate2;

            shipmentStatusDoc.setOtmflexFieldDates(otmFlexFieldDates);
        }

        return saveEvent(shipmentStatusDoc, loginUser, "");
    }

    @Override
    public ApiResponse addToWatchList(String loadId, HttpServletRequest request) {
        if (Utility.isEmpty(loadId))
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid Load Id");
        String userId = tokenUtilService.getUserId(request);
        Optional<User> user = userRepository.findOneByUserId(userId);
        Optional<ShipmentV2> shipmentOp = shipmentV2Repository.findOneByLoadID(loadId);
        if (shipmentOp.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid Load Id");
        }
        if (user.isPresent()) {
            User user1 = user.get();
            List<String> watchLists = user1.getWatchList();
            if (watchLists.contains(loadId)) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Shipment already added to watchlist");
            } else {
                watchLists.add(loadId);
                userRepository.save(user1);
                return new ApiResponse(HttpStatus.OK, "Shipment added to watchlist");
            }
        } else {
            return new ApiResponse(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public ApiResponse closeShipment(String loadId, String userId) {
        logger.info("Shipment Close Request :: Load ID:" + loadId + " , User Id:" + userId);
        Optional<ShipmentV2> shipmentVal = shipmentV2Repository.findOneByLoadID(loadId);
        if (!shipmentVal.isPresent()) {
            return new ApiResponse(HttpStatus.NO_CONTENT, "No Data Found");
        }
        ShipmentV2 shipment = shipmentVal.get();
        shipment.setStatus(StatusEnum.CLOSED);
        shipment.setLoadClosedOn(DateUtil.formatDate(new Date(), DateUtil.dateTimeFormatter2));
        shipment.setClosedBy(userId);
        shipmentV2Repository.save(shipment);
        if (shipment.getContainer() != null && !org.springframework.util.StringUtils.isEmpty(shipment.getContainer().getNumber())) {
            closeContainer(shipment.getLoadID(), shipment.getContainer().getNumber(), shipment.getContainer().getId());
        }
        generateCloseShipmentXml(shipment);
        logger.info("Shipment Closed SuccessFully");
        return new ApiResponse(HttpStatus.OK, "Success");
    }

    private void closeContainer(String loadId, String containerNumber, String shipContainerId) {
        Container container = containerRepository.findByContainerNumberAndShipContainerId(containerNumber, shipContainerId);
        if (container != null) {
            List<ShipmentV2> shipmentV2s = container.getShipmentsV2();
            shipmentV2s.stream().filter(shipmentV2 -> shipmentV2.getLoadID().equals(loadId)).forEach(shipmentV2 -> shipmentV2.setStatus(StatusEnum.CLOSED));
            long numberOfCompletedClosedShipments = shipmentV2s.stream().filter(f -> f.getStatus().equals(StatusEnum.CLOSED) || f.getStatus().equals(StatusEnum.COMPLETED)).count();
            if ((long) shipmentV2s.size() == numberOfCompletedClosedShipments) {
                container.setStatus(StatusEnum.COMPLETED);
            }
            container.setShipmentsV2(shipmentV2s);
            containerRepository.save(container);
        }
    }

    public void generateCloseShipmentXml(ShipmentV2 shipment) {
        FeedBean feedBean = new FeedBean();
        FeedHeaderBean feedHeader = new FeedHeaderBean();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.dateTimeFormatter2);
        feedHeader.setFeedGenDtTime(sdf.format(now));
        FeedContentBean feedContent = new FeedContentBean();
        EventDtBean eventDt = new EventDtBean(sdf.format(now), null);
        ShipmentCloseEvent shipmentCloseEvent = new ShipmentCloseEvent(shipment.getLoadID(), StatusEnum.CLOSED.name(), eventDt);
        feedContent.setShipmentCloseEvent(shipmentCloseEvent);
        feedBean.setFeedHeader(feedHeader);
        feedBean.setFeedContent(feedContent);
        String shipmentCloseXML = Utility.convertObjectToXML(feedBean);
        if (!shipmentCloseXML.isEmpty()) {
            String shipmentCloseStylisedXML = Utility.stylizer("EndTrackingStyleSheet_v2.xsl", shipmentCloseXML);
            postShipmentCloseData(shipmentCloseStylisedXML);
        }
    }

    private void postShipmentCloseData(String shipmentCloseStylisedXML) {
        ApplicationSettings applicationSettings = applicationSettingsRepository.findAll().get(0);
        if (applicationSettings != null) {
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationSettings.getOtmUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            String username = applicationSettings.getOtmUsername();
            String password = applicationSettings.getOtmPassword();
            System.out.println(shipmentCloseStylisedXML);
            HttpEntity<String> requestEntity = new HttpEntity<>(shipmentCloseStylisedXML,
                    createHeaders(username, password));
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,
                        requestEntity, String.class);
                HttpStatusCode statusCode = responseEntity.getStatusCode();
                if (statusCode == HttpStatus.OK) {
                    logger.info("Shipment Closed Data Pushed to OTM Server" + responseEntity.getBody());
                }
            } catch (Exception ex) {
                logger.info("Closed Data call for OTM Server call FAILED!!!!!!!!!!!!!!", ex.getMessage());
            }
        }
    }
}

