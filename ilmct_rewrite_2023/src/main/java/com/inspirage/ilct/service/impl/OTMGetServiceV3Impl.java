package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.OrderV2;
import com.inspirage.ilct.dto.ShipmentV2Bean;
import com.inspirage.ilct.dto.bean.ShipmentTrackItemBeanV2;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import com.inspirage.ilct.dto.bean.rewrite.StatusCountBean;
import com.inspirage.ilct.dto.response.*;
import com.inspirage.ilct.dto.response.ShipmentTrackingBeanV2;
import com.inspirage.ilct.enums.ActionEnum;
import com.inspirage.ilct.enums.MessageTypeEnum;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.enums.StatusEnum;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.exceptions.PageRedirectionException;
import com.inspirage.ilct.repo.*;
import com.inspirage.ilct.service.*;
import com.inspirage.ilct.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.inspirage.ilct.enums.StatusEnum.CLOSED;
import static com.inspirage.ilct.util.Constants.*;
import static com.inspirage.ilct.util.SearchResultPageConstant.TABLE_CONTAINER_VISIBILITY;
import static com.inspirage.ilct.util.SearchResultPageConstant.*;
import static java.util.stream.Collectors.toList;

@Service
public class OTMGetServiceV3Impl implements OTMGetServiceV3 {

    private final MongoTemplate mongoTemplate;
    private final TokenUtilService tokenUtilService;
    private final UserRepository userRepository;
    private final MasterTruckTypesRepository masterTruckTypesRepository;
    private final CacheService cacheService;
    private final ApplicationSettingsService applicationSettingsService;
    private final ShipmentStatusRepository shipmentStatusRepository;
    private final OrdersV2Repository ordersV2Repository;
    @Autowired
    MessageByLocaleService localeService;
    @Autowired
    SingleTonClass singleTonClass;
    private ShipmentV2Repository shipmentV2Repository;
    private final LocationDocRepository locationRepository;
    private final LoggerService loggerService;
    private final RuleRepository ruleRepository;


    public OTMGetServiceV3Impl(MongoTemplate mongoTemplate, TokenUtilService tokenUtilService, UserRepository userRepository, MasterTruckTypesRepository masterTruckTypesRepository, CacheService cacheService, ApplicationSettingsService applicationSettingsService, ShipmentStatusRepository shipmentStatusRepository, OrdersV2Repository ordersV2Repository, ShipmentV2Repository shipmentV2Repository, LocationDocRepository locationRepository, LoggerService loggerService, RuleRepository ruleRepository) {
        this.mongoTemplate = mongoTemplate;
        this.tokenUtilService = tokenUtilService;
        this.userRepository = userRepository;
        this.masterTruckTypesRepository = masterTruckTypesRepository;
        this.cacheService = cacheService;
        this.applicationSettingsService = applicationSettingsService;
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.ordersV2Repository = ordersV2Repository;
        this.shipmentV2Repository = shipmentV2Repository;
        this.locationRepository = locationRepository;
        this.loggerService = loggerService;
        this.ruleRepository = ruleRepository;
    }

    public static final String GLOBAL_ID = "GID";
    private static final String BOOKING_REF_NUM = "BN";
    private static final Logger logger = LoggerFactory.getLogger(OTMGetServiceV3Impl.class);

//    @Async
    @Override
    public ApiResponse getShipmentTrackingBeans(Boolean isPagination, int pageIndex, int numberOfRecord, String containerId, String loadId, String carrierId, String customerId, String status, Boolean hazardous, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        List<ShipmentTrackingBeanV2> shipmentTrackingBeanV2s = new ArrayList<>();
        String userId = tokenUtilService.getUserId(request);
        if (ObjectUtils.isEmpty(userId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message", shipmentTrackingBeanV2s);
        }
        Query query = mappingMongoQueryForGettingShipments(containerId, loadId, carrierId, customerId, status, hazardous);
        Optional<User> optionalUser = userRepository.findOneByUserIdIgnoreCase(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Failed", "error.login.user_not_exist", null);
        }
        Criteria criteria = null;
        List<String> rules = user.getRules();
        List<String> contactIds;

        switch (user.getRole()) {
            case ADMIN:
                break;
            case SUB_ADMIN:
                break;
            case CUSTOMER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getCustomerCriteria(criteria, rules, contactIds);
                break;
            case PLANNER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getPlannerCriteria(criteria, rules, contactIds);
                break;
            case CARRIER:
                if (rules != null && !rules.isEmpty()) {
                    query.addCriteria(Criteria.where("carrierID").in(rules));
                }
                break;
            default:
                return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", shipmentTrackingBeanV2s);
        }
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        final String userTimeZone = Utility.getTimeZone(user);
        if (isPagination != null && isPagination) {
            return shipmentWithPagination(pageIndex, numberOfRecord, query, userTimeZone, user);
        } else {
            return shipmentWithOutPagination(loadId, request, query, shipmentTrackingBeanV2s, userTimeZone, user);
        }
    }

    private ApiResponse shipmentWithOutPagination(String loadId, HttpServletRequest request, Query query, List<ShipmentTrackingBeanV2> shipmentTrackingBeanV2s, String userTimeZone, User user) {
        List<ShipmentV2> shipmentsData = mongoTemplate.find(query, ShipmentV2.class);
        try {
            shipmentTrackingBeanV2s = mapDataToShipmentTrackingBean(userTimeZone, shipmentsData, user);
        } catch (Exception e) {
            loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.SHIPMENT_ERROR).actionEnum(ActionEnum.FINDING_SHIPMENT_TRACKING).loadId(loadId).build(), request);
        }
        return new ApiResponse(HttpStatus.OK, "get shipment List successfully", shipmentTrackingBeanV2s);
    }

    private ApiResponse shipmentWithPagination(int pageIndex, int numberOfRecord, Query query, String userTimeZone, User user) throws ApplicationSettingsNotFoundException {
        long totalRecords;
        List<ShipmentTrackingBeanV2> shipmentTrackingBeanV2s;
        query.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        totalRecords = mongoTemplate.count(query, ShipmentV2.class);
        query.with(PageRequest.of(pageIndex, numberOfRecord));
        List<ShipmentV2> shipments = mongoTemplate.find(query, ShipmentV2.class);
        shipmentTrackingBeanV2s = mapDataToShipmentTrackingBean(userTimeZone, shipments, user);
        if (shipmentTrackingBeanV2s.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Shipment cannot be parsed");
        } else {
            return new PageableResponse.PageableResponseBuilder(HttpStatus.OK).withMessage("Success").withData(checkForWatchList(shipmentTrackingBeanV2s, user)).withPageInfo(pageIndex, numberOfRecord, totalRecords).build();
        }
    }

    private Query mappingMongoQueryForGettingShipments(String containerId, String loadId, String carrierId, String customerId, String status, Boolean hazardous) {
        Query query = new Query();
        Criteria criteriaMode;
        if (Boolean.FALSE) {
            criteriaMode = Criteria.where("mode").in(List.of(EXPRESS));
        } else {
            criteriaMode = Criteria.where("mode").in(Arrays.asList(Constants.TM_TL, "TM_TL", Constants.TM_TRUCK, TM_LTL, GROUP_AGE, INTER_MODEL));
            if (!ObjectUtils.isEmpty(containerId)) {
                query.addCriteria(Criteria.where("container.number").is(containerId));
            }
        }
        query.addCriteria(criteriaMode);
        if (ObjectUtils.isEmpty(containerId) && ObjectUtils.isEmpty(loadId) && ObjectUtils.isEmpty(carrierId) && ObjectUtils.isEmpty(customerId) && Utility.isEmpty(hazardous) && ObjectUtils.isEmpty(status)) {
            if (Boolean.FALSE) {
                query.addCriteria(Criteria.where("status").in(StatusEnum.getExpressStatus()));
            } else {
                query.addCriteria(Criteria.where("status").in(StatusEnum.getInTransitStatuses()));
            }
        }
        if (!ObjectUtils.isEmpty(loadId)) {
            query.addCriteria(Criteria.where("loadID").is(loadId));
        }
        if (!ObjectUtils.isEmpty(carrierId)) {
            query.addCriteria(Criteria.where("carrierID").is(carrierId));
        }
        if (!ObjectUtils.isEmpty(customerId)) {
            query.addCriteria(Criteria.where("destination.siteId").is(customerId));
        }
        if (!Utility.isEmpty(hazardous)) {
            query.addCriteria(Criteria.where("loadReferences.loadReferenceType").is("HazardousFlag").and("loadReferences.content").is(String.valueOf(hazardous)));
        }
        if (!ObjectUtils.isEmpty(status)) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        return query;
    }

    private Criteria getCustomerCriteria(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            if (criteria == null) criteria = new Criteria();
            criteria.orOperator(Criteria.where("consignee.location.siteId").in(rules), new Criteria().andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("consignee.location.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds));
        }
        return criteria;
    }

    private Criteria getPlannerCriteria(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("source.siteId").in(rules), Criteria.where("destination.siteId").in(rules), new Criteria().andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("source.siteId").in(rules), Criteria.where("destination.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds));
        }
        return criteria;
    }

    private List<ShipmentTrackingBeanV2> mapDataToShipmentTrackingBean(String userTimeZone, List<ShipmentV2> shipments, User user) throws ApplicationSettingsNotFoundException {
        List<MasterTruckType> masterTruckTypeList = masterTruckTypesRepository.findByTypeIn(shipments.stream().filter(shipment -> shipment.getLoadMeasure() != null).map(shipment -> shipment.getLoadMeasure().getTruckType()).collect(Collectors.toSet()));
        Optional<String> truckType = masterTruckTypesRepository.findByIsDefault(true).map(MasterTruckType::getType);
        List<SpecialServicesBean> specialServicesBeans = applicationSettingsService.getApplicationSetting().getSpecialServices();
        List<AlertsBean> alerts = applicationSettingsService.getApplicationSetting().getAlerts();
        List<ShipmentStatusDoc> lastSeenDocsList = shipmentStatusRepository.findByLoadIdInAndStatusCodeIdInOrderByCreatedDateDesc(shipments.stream().map(ShipmentV2::getLoadID).toList(), Constants.getGVITStatusList());
        return shipments.parallelStream().map(shipmentV2 -> {
            ShipmentTrackingBeanV2 shipmentTrackingBeanV2 = new ShipmentTrackingBeanV2();
            processedShipmentBasicFiedls(shipmentV2, shipmentTrackingBeanV2);
            if (shipmentV2.getLoadReferences() != null && !shipmentV2.getLoadReferences().isEmpty()) {
                processedLoadReferences(shipmentV2, shipmentTrackingBeanV2);
            }
            processedShipmetStops(userTimeZone, shipmentV2, shipmentTrackingBeanV2);
            processedTruckDetails(shipmentV2, masterTruckTypeList, shipmentTrackingBeanV2, truckType);
            shipmentTrackingBeanV2.setSpecialServices(shipmentV2.getSpecialServices().stream().map(specialServiceName ->
                    specialServicesBeans.stream()
                            .filter(specialService -> specialService.getSpecialServiceName().equalsIgnoreCase(specialServiceName)).findFirst()
                            .orElse(null)).toList());
            Map<String, AlertsBean> alertsBeanMap = alerts.stream().collect(HashMap::new,
                    (map, alert) -> map.put(alert.getAlertName(), alert), HashMap::putAll);
            shipmentTrackingBeanV2.setAlerts(getCorrespondingAlert(alertsBeanMap, shipmentTrackingBeanV2));
            Optional<ShipmentStatusDoc> shipmentStatusDocOptional = lastSeenDocsList.stream().filter(statusDoc -> statusDoc.getLoadId().equalsIgnoreCase(shipmentV2.getLoadID())).findFirst();
            String adminUserId = null;
            if (!user.getRole().equals(RoleType.ADMIN)) {
                adminUserId = userRepository.findByRole(RoleType.ADMIN).stream().findFirst().map(User::getUserId).orElse(null);
            }
            Optional<RuleDoc> adminRules = ruleRepository.findOneByUserId(adminUserId);
            int lastSeenHours = 0;
            if (adminRules.isPresent()) {
                lastSeenHours = adminRules.get().getLastSeenHours();
            }
            if(shipmentStatusDocOptional.isPresent()) {
                ShipmentStatusDoc statusDoc = shipmentStatusDocOptional.get();
                shipmentTrackingBeanV2.setLastSeen(DateUtil.formatDate(statusDoc.getEventDateAsLocalDate(userTimeZone), DateUtil.dateTimeFormatter1));
                    Date pastDate = Date.from(Instant.now().minus(Duration.ofHours(lastSeenHours)));
                    if (statusDoc.getCreatedDate() != null && statusDoc.getCreatedDate().before(pastDate)) {
                        shipmentTrackingBeanV2.setLastSeenColorCode(Constants.LASTSEEN_LABELCOLOR_RED);
                    } else {
                        shipmentTrackingBeanV2.setLastSeenColorCode(Constants.LASTSEEN_LABELCOLOR_GREEN);
                    }
            }
            return shipmentTrackingBeanV2;
        }).toList();
    }

    private static void processedTruckDetails(ShipmentV2 shipmentV2, List<MasterTruckType> masterTruckTypeList, ShipmentTrackingBeanV2 shipmentTrackingBeanV2, Optional<String> truckType) {
        Optional<MasterTruckType> optionalMasterTruckType = masterTruckTypeList.stream().filter(masterTruckType -> masterTruckType.getType().equals(shipmentV2.getLoadMeasure().getTruckType())).findAny();
        if (optionalMasterTruckType.isPresent()) {
            shipmentTrackingBeanV2.setTruckType(optionalMasterTruckType.get().getType());
        } else {
            shipmentTrackingBeanV2.setTruckType(truckType.orElse(null));
        }
    }

    private static void processedShipmentBasicFiedls(ShipmentV2 shipmentV2, ShipmentTrackingBeanV2 shipmentTrackingBeanV2) {
        shipmentTrackingBeanV2.setLoadId(shipmentV2.getLoadID());
        shipmentTrackingBeanV2.setContainerNo(shipmentV2.getContainer().getNumber());
        shipmentTrackingBeanV2.setWeightPercentage(shipmentV2.getWeightPercentage());
        shipmentTrackingBeanV2.setCarrierName(shipmentV2.getCarrierName());
        shipmentTrackingBeanV2.setSource(shipmentV2.getSource().getSiteName());
        shipmentTrackingBeanV2.setDestination(shipmentV2.getDestination().getSiteName());
        shipmentTrackingBeanV2.setUrl(shipmentV2.getTtURL());
        shipmentTrackingBeanV2.setStatus(shipmentV2.getStatus().name());
    }

    private static void processedLoadReferences(ShipmentV2 shipmentV2, ShipmentTrackingBeanV2 shipmentTrackingBeanV2) {
        shipmentV2.getLoadReferences().stream().filter(loadReference -> loadReference.getLoadReferenceType() != null).forEach(loadReference -> {
            if (loadReference.getLoadReferenceType().equals(KEY_TRUCK_NO)) {
                shipmentTrackingBeanV2.setTruckNo((loadReference.getContent()));
            } else if (loadReference.getLoadReferenceType().equals(KEY_TRAILER_NO)) {
                shipmentTrackingBeanV2.setTrailerNo(loadReference.getContent());
            }
        });
    }

    private static void processedShipmetStops(String userTimeZone, ShipmentV2 shipmentV2, ShipmentTrackingBeanV2 shipmentTrackingBeanV2) {
        shipmentV2.getStops().parallelStream()
                .filter(shipmentStopV2 -> shipmentStopV2.getStopContent() != null && shipmentStopV2.getStopContent().getPallet() != null)
                .forEach(shipmentStopV2 -> {
                    List<String> orderNoList = shipmentStopV2.getStopContent().getPallet().stream().flatMap(pallet -> pallet.getOrder().stream()).distinct().toList();
                    shipmentTrackingBeanV2.setOrderNo(String.join(",", orderNoList));
                    if (shipmentStopV2.getStopType().equals("D")) {
                        shipmentTrackingBeanV2.setDeliveryStops(shipmentTrackingBeanV2.getDeliveryStops() + 1);
                    }
                    if (shipmentStopV2.getCalculatedETA() != null) {
                        shipmentTrackingBeanV2.setDeliveryPTA(
                                DateUtil.formatDate(DateUtil.convertDate(shipmentStopV2.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2,
                                                shipmentStopV2.getCalculatedETA().getTZId(),
                                        userTimeZone), DateUtil.dateTimeFormatter1));
                        shipmentTrackingBeanV2.setDeliveryETA(
                                DateUtil.formatDate(DateUtil.convertDate(shipmentStopV2.getPlannedArrival().getDateTime(), DateUtil.dateTimeFormatter2,
                                                shipmentStopV2.getPlannedArrival().getTZId(),
                                        userTimeZone), DateUtil.dateTimeFormatter1));
                    }
                });
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

        List<String> myList = new ArrayList<>(siteIds);
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

    private List<AlertsBean> getCorrespondingAlert(Map<String, AlertsBean> alertsBeanMap, ShipmentTrackingBeanV2 shipmentTrackingBeanV2) {
        List<AlertsBean> alerts = new ArrayList<>();
        if (shipmentTrackingBeanV2.getWeatherAlerts() != null && (!ObjectUtils.isEmpty(shipmentTrackingBeanV2.getHighTemperature()) || !ObjectUtils.isEmpty(shipmentTrackingBeanV2.getLowTemperature()) || !ObjectUtils.isEmpty(shipmentTrackingBeanV2.getHumidity()))) {
            AlertsBean alertsBean = alertsBeanMap.get("weather");
            if (alertsBean != null) {
                alertsBean.setAlertValue(shipmentTrackingBeanV2.getHighTemperature());
                alerts.add(alertsBean);
            }
        }
        if (shipmentTrackingBeanV2.isHighTemperatureAlert()) {
            AlertsBean alertsBean = alertsBeanMap.get("highTemperature");
            if (alertsBean != null) {
                alertsBean.setAlertValue(shipmentTrackingBeanV2.getHighTemperature());
                alerts.add(alertsBean);
            }
        }
        if (shipmentTrackingBeanV2.isHighSpeedAlert()) {
            AlertsBean alertsBean = alertsBeanMap.get("overspeed");
            if (alertsBean != null) {
                alertsBean.setAlertValue(shipmentTrackingBeanV2.getCurrentSpeed());
                alerts.add(alertsBean);
            }
        }
        if (shipmentTrackingBeanV2.isLowFuelAlert()) {
            AlertsBean alertsBean = alertsBeanMap.get("lowfuel");
            if (alertsBean != null) {
                alertsBean.setAlertValue(shipmentTrackingBeanV2.getCurrentFuel());
                alerts.add(alertsBean);
            }
        }
        return alerts;
    }

    private List<ShipmentTrackingBeanV2> checkForWatchList(List<ShipmentTrackingBeanV2> trackingBeans, User user) {
        Map<String, String> watchList = user.getWatchList().stream().collect(Collectors.toMap(i -> i, i -> i));
        List<ShipmentTrackingBeanV2> list = new ArrayList<>();
        for (ShipmentTrackingBeanV2 bean : trackingBeans) {
            if (bean.getLoadId().equalsIgnoreCase(watchList.get(bean.getLoadId())))
                bean.setIsWatchlistAdded(Boolean.TRUE);
            list.add(bean);
        }
        list.sort(Comparator.comparing(b -> !b.getIsWatchlistAdded()));
        return list;
    }

    @Override
    public ApiResponse getLegShipmentsV3(Boolean isPagination, int pageIndex, int noOfRecords, String containerId, String loadId, String carrierId, String customerId, String status, Boolean hazardous, HttpServletRequest request, String globalId, String deleveryOrderNo, String bookingRefNo, String domainName, String fromPage) {
        final TreeMap<String, LinkedList<ContainerShipmentBean>> containerMap = new TreeMap<>();

        String userId = tokenUtilService.getUserId(request);
        if (Utility.isEmpty(userId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message", containerMap);
        }

        User user = userRepository.findOneByUserIdIgnoreCase(userId).orElse(new User());
        Query query = new Query();
        query.addCriteria(Criteria.where("status").in(StatusEnum.getInTransitStatuses()));
        query.addCriteria(Criteria.where("shipmentsV2").exists(true));
        List<String> rules = user.getRules();
        List<String> contactIds;
        Criteria criteria = new Criteria();

        switch (user.getRole()) {
            case ADMIN:
                break;
            case SUB_ADMIN:
                break;
            case CUSTOMER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getCustomerCriteriaContainer(criteria, rules, contactIds);
                break;
            case PLANNER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getPlannerCriteriaContainer(criteria, rules, contactIds);
                break;
            case CARRIER:
                if (rules != null && !rules.isEmpty()) {
                    criteria.and("shipmentsV2.carrierId").in(user.getRules());
                }
                break;
            default:
                return null;
        }
        query.addCriteria(criteria);
        if (isPagination) {
            return containerShipmentPagination(pageIndex, noOfRecords, globalId, bookingRefNo, fromPage, query, containerMap, user);
        } else {
            return containerShipmentWithOutPagination(globalId, bookingRefNo, query, containerMap, user);
        }
    }

    private ApiResponse containerShipmentWithOutPagination(String globalId, String bookingRefNo, Query query, TreeMap<String, LinkedList<ContainerShipmentBean>> containerMap, User user) {
        String userTimeZone = Utility.getTimeZone(user);
        List<Container> containerList = mongoTemplate.find(query, Container.class);
        if (containerList.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", containerMap);
        }
        List<Container> newContainerList = new ArrayList<>();
        containerList.stream().collect(Collectors.groupingBy(Container::getShipContainerId)).forEach((shipmentId, containers) -> newContainerList.addAll(containers));
        newContainerList.stream()
                .filter(sameContainer -> {
                    List<ShipmentV2> shipments = sameContainer.getShipmentsV2();
                    return shipments.stream().allMatch(shipment -> ObjectUtils.isEmpty(globalId) || ObjectUtils.isEmpty(bookingRefNo) || isValidShipment(globalId, bookingRefNo, shipment, true));
                })
                .forEach(sameContainer -> {
                    List<ContainerShipmentBean> containerShipmentBean = mapDataToContainerShipmentBean(userTimeZone, sortContainerShipments(sameContainer.getShipmentsV2()));
                    String conNumber = sameContainer.getShipContainerId() == null ? sameContainer.getContainerNumber() : sameContainer.getContainerNumber() + " " + sameContainer.getShipContainerId();
                    containerMap.computeIfAbsent(conNumber, k -> new LinkedList<>()).addAll(containerShipmentBean);
                });
        return new ApiResponse(HttpStatus.OK, "containers list fetch successfully", containerMap);
    }

    private ApiResponse containerShipmentPagination(int pageIndex, int noOfRecords, String globalId, String bookingRefNo, String fromPage, Query query, TreeMap<String, LinkedList<ContainerShipmentBean>> containerMap, User user) {
        long totalRecords;
        String userTimeZone = Utility.getTimeZone(user);
        totalRecords = mongoTemplate.count(query, Container.class);
        query.with(Utility.pageable(pageIndex, noOfRecords));
        query.with(Sort.by(Sort.Direction.DESC, "deliveryPta"));
        List<Container> containerList = mongoTemplate.find(query, Container.class);
        if (containerList.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", containerMap);
        }
        List<Container> newContainerList = new ArrayList<>();
        containerList.stream().collect(Collectors.groupingBy(Container::getShipContainerId)).forEach((shipmentId, containers) -> newContainerList.addAll(containers));
        List<String> loadIdList = newContainerList.stream()
                .filter(sameContainer -> {
                    List<ShipmentV2> shipments = sortContainerShipments(sameContainer.getShipmentsV2().isEmpty() ? null : sameContainer.getShipmentsV2());
                    return shipments.stream().allMatch(shipment ->
                            ObjectUtils.isEmpty(globalId) || ObjectUtils.isEmpty(bookingRefNo) || isValidShipment(globalId, bookingRefNo, shipment, true));
                }).map(Container::getShipmentsV2).flatMap(Collection::stream).map(ShipmentV2::getLoadID).toList();
        List<ShipmentStatusDoc> statusDocs = shipmentStatusRepository.findByLoadIdIn(loadIdList, Sort.by(Sort.Direction.DESC, "lastUpdated"));
        getContainerShipmentsDetails(globalId, bookingRefNo, fromPage, newContainerList, containerMap, statusDocs, user);
        return new PageableResponse.PageableResponseBuilder(HttpStatus.OK).withMessage("Success").withData(containerMap).withPageInfo(pageIndex, noOfRecords, totalRecords).build();
    }

    private void getContainerShipmentsDetails(String globalId, String bookingRefNo, String fromPage, List<Container> newContainerList, TreeMap<String, LinkedList<ContainerShipmentBean>> containerMap, List<ShipmentStatusDoc> statusDocs, User user) {
        String userTimeZone = Utility.getTimeZone(user);
        newContainerList.stream()
                .filter(sameContainer -> {
                    List<ShipmentV2> shipments = sortContainerShipments(sameContainer.getShipmentsV2().isEmpty() ? null : sameContainer.getShipmentsV2());
                    return shipments.stream().allMatch(shipment -> ObjectUtils.isEmpty(globalId) || ObjectUtils.isEmpty(bookingRefNo) || isValidShipment(globalId, bookingRefNo, shipment, true));
                }).forEach(sameContainer -> {
                    List<ContainerShipmentBean> containerShipmentBean = mapDataToContainerShipmentBean(userTimeZone, sortContainerShipments(sameContainer.getShipmentsV2()));
                    String conNumber = sameContainer.getShipContainerId() == null ? sameContainer.getContainerNumber() : sameContainer.getContainerNumber() + " " + sameContainer.getShipContainerId();

                    containerMap.computeIfAbsent(conNumber, k -> new LinkedList<>()).addAll(containerShipmentBean);

                    if (fromPage != null && fromPage.equalsIgnoreCase(TABLE_CONTAINER_VISIBILITY.name())) {
                        containerMap.forEach((key, legShipment) -> {
                            if (!Utility.isEmpty(statusDocs) && statusDocs.stream().allMatch(statusLoadId -> legShipment.stream().anyMatch(containerShipment -> statusLoadId.getLoadId().equals(containerShipment.getLoadId())))) {
                                legShipment.get(0).setCurrentStatus(sameContainer.getContainerStatus());
                            }
                        });
                    }
                });
    }

    private List<ContainerShipmentBean> mapDataToContainerShipmentBean(String userTimeZone, List<ShipmentV2> shipments) {
        List<ContainerShipmentBean> containerShipmentBeanList = new ArrayList<>();
        for (ShipmentV2 shipment : shipments) {
            Optional<ContainerShipmentBean> optionalContainerShipmentBean = containerShipmentBeanList.stream()
                    .filter(containerShipment -> Objects.equals(containerShipment.getContainerNo(), shipment.getContainer().getNumber()))
                    .findFirst();
            if (optionalContainerShipmentBean.isPresent()) {
                ContainerShipmentBean containerShipmentBean = optionalContainerShipmentBean.get();
                containerShipmentBean.getLegShipmentsList().add(getLegShipments(shipment));
                containerShipmentBean.setNoOfLegs(containerShipmentBean.getNoOfLegs() + 1);
            } else {
                ContainerShipmentBean containerShipmentBean = createContainerShipmentBean(userTimeZone, shipment);
                containerShipmentBean.setNoOfLegs(containerShipmentBean.getNoOfLegs() + 1);
                containerShipmentBeanList.add(containerShipmentBean);
            }
        }
        return containerShipmentBeanList;
    }

    private ContainerShipmentBean createContainerShipmentBean(String userTimeZone, ShipmentV2 shipment) {
        ContainerShipmentBean containerShipmentBean = new ContainerShipmentBean();
        containerShipmentBean.setLoadId(shipment.getLoadID());
        containerShipmentBean.setContainerNo(shipment.getContainer() != null ? shipment.getContainer().getNumber() : null);

        List<String> orderNoList = shipment.getStops().stream()
                .filter(shipmentStopV2 -> shipmentStopV2.getStopContent() != null && shipmentStopV2.getStopContent().getPallet() != null)
                .flatMap(shipmentStopV2 -> shipmentStopV2.getStopContent().getPallet().stream())
                .flatMap(pallet -> pallet.getOrder().stream())
                .distinct()
                .collect(toList());

        containerShipmentBean.setOrderNo(String.join(",", orderNoList));
        containerShipmentBean.setLiner(shipment.getCarrierName());
        containerShipmentBean.setPTA(getFormattedDates(shipment.getStops(), userTimeZone, "calculatedETA"));
        containerShipmentBean.setETA(getFormattedDates(shipment.getStops(), userTimeZone, "plannedArrival"));
        containerShipmentBean.setNoOfOrders(orderNoList.size());
        containerShipmentBean.setWeightPercentage(shipment.getWeightPercentage());
        containerShipmentBean.setSource(shipment.getSource().getSiteName());
        containerShipmentBean.setDestination(shipment.getDestination().getSiteName());
        containerShipmentBean.setOriginPort(shipment.getSource().getSiteName());
        containerShipmentBean.setDestinationPort(shipment.getDestination().getSiteName());

        LegShipmentsBean legShipmentsBean = getLegShipments(shipment);
        containerShipmentBean.getLegShipmentsList().add(legShipmentsBean);

        return containerShipmentBean;
    }

    private String getFormattedDates(List<ShipmentStopV2> stops, String userTimeZone, String dateType) {
        return stops.stream()
                .filter(shipmentStopV2 -> shipmentStopV2.getCalculatedETA() != null)
                .map(shipmentStopV2 -> DateUtil.formatDate(
                        DateUtil.convertDate(
                                getDateFromShipmentStop(shipmentStopV2, dateType),
                                DateUtil.dateTimeFormatter2,
                                shipmentStopV2.getCalculatedETA().getTZId(),
                                userTimeZone),
                        DateUtil.dateTimeFormatter1))
                .skip(1)
                .collect(Collectors.joining(", "));
    }

    private String getDateFromShipmentStop(ShipmentStopV2 shipmentStop, String dateType) {
        return dateType.equals("calculatedETA") ? shipmentStop.getCalculatedETA().getDateTime() : shipmentStop.getPlannedArrival().getDateTime();
    }

    private LegShipmentsBean getLegShipments(ShipmentV2 shipment) {
        LegShipmentsBean legShipmentsBean = new LegShipmentsBean();

        if (shipment.getMode().equals("VESSEL-CO")) {
            WaterTransportLeg waterTransportLeg = new WaterTransportLeg();
            waterTransportLeg.setLoadId(shipment.getLoadID());
            waterTransportLeg.setGlobalId(shipment.getLoadReferences().stream()
                    .filter(loadReference -> loadReference.getLoadReferenceType() != null && loadReference.getContent() != null && loadReference.getLoadReferenceType().equals(GLOBAL_ID))
                    .map(LoadReference::getContent)
                    .findFirst()
                    .orElse(null));
            waterTransportLeg.setLiner(shipment.getCarrierName());
            waterTransportLeg.setOriginPort(shipment.getSource().getSiteName());
            waterTransportLeg.setDestinationPort(shipment.getDestination().getSiteName());
            waterTransportLeg.setStatus(shipment.getStatus().name());
            waterTransportLeg.setVesselName(null);
            waterTransportLeg.setBL(null);
            waterTransportLeg.setFF(null);
            legShipmentsBean.setWaterTransportLeg(waterTransportLeg);
        } else {
            RoadTransportLeg roadTransportLeg = new RoadTransportLeg();
            roadTransportLeg.setLoadId(shipment.getLoadID());
            roadTransportLeg.setGlobalId(shipment.getLoadReferences().stream()
                    .filter(loadReference -> loadReference != null && loadReference.getLoadReferenceType() != null && loadReference.getLoadReferenceType().equals(GLOBAL_ID))
                    .map(LoadReference::getContent)
                    .findFirst()
                    .orElse(null));
            roadTransportLeg.setContainerName(shipment.getContainer().getNumber());
            roadTransportLeg.setPickup(shipment.getSource().getSiteName());
            roadTransportLeg.setDelivery(shipment.getDestination().getSiteName());
            roadTransportLeg.setTruckNumber(shipment.getLoadReferences().stream()
                    .filter(loadReference -> loadReference != null && loadReference.getLoadReferenceType() != null && loadReference.getContent() != null && loadReference.getLoadReferenceType().equals(KEY_TRUCK_NO))
                    .map(LoadReference::getContent)
                    .findFirst()
                    .orElse(null));
            roadTransportLeg.setStatus(shipment.getStatus().name());
            legShipmentsBean.setRoadTransportLeg(roadTransportLeg);
        }
        return legShipmentsBean;
    }

    private List<ShipmentV2> sortContainerShipments(List<ShipmentV2> shipmentV2s) {

        return Constants.CARRIAGETYPES.stream()
                .flatMap(carriageType -> shipmentV2s.stream()
                        .filter(legShipment -> {
                            Optional<LoadReference> optionalLoadReference = legShipment.getLoadReferences().stream()
                                    .filter(loadReference -> loadReference.getLoadReferenceType().equals("CarriageType"))
                                    .findAny();
                            return optionalLoadReference.isPresent() &&
                                    optionalLoadReference.get().getContent().equalsIgnoreCase(carriageType);
                        })
                        .sorted(Comparator.comparing(shipmentV2 -> shipmentV2.getStartDate().getDateTime()))
                )
                .collect(toList());
    }

    private boolean isValidShipment(String globalId, String bookingRefNo, ShipmentV2 shipment, boolean validShipment) {
        if (shipment.getLoadReferences() != null && shipment.getLoadReferences().stream()
                .anyMatch(x -> switch (x.getLoadReferenceType()) {
                    case "GLOBAL_ID" -> !ObjectUtils.isEmpty(globalId) && x.getContent() != null &&
                            !x.getContent().toUpperCase().contains(globalId.trim().toUpperCase());
                    case "BN" -> !ObjectUtils.isEmpty(bookingRefNo) && x.getContent() != null &&
                            !x.getContent().toUpperCase().contains(bookingRefNo.trim().toUpperCase());
                    default -> true;
                })) {
            validShipment = false;
        }
        return validShipment;
    }

    private Criteria getCustomerCriteriaContainer(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            if (criteria == null) criteria = new Criteria();
            criteria.orOperator(Criteria.where("shipmentsV2.consignee.location.siteId").in(rules), new Criteria().andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("shipmentsV2.loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("shipmentsV2.consignee.location.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("shipmentsV2.loadReferences.content").in(contactIds));
        }
        return criteria;
    }

    private Criteria getPlannerCriteriaContainer(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("shipmentsV2.source.siteId").in(rules), Criteria.where("shipmentsV2.destination.siteId").in(rules), new Criteria().andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("shipmentsV2.loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("shipmentsV2.source.siteId").in(rules), Criteria.where("shipmentsV2.destination.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("shipmentsV2.loadReferences.content").in(contactIds));
        }
        return criteria;
    }


    @Override
    public ApiResponse getIntransitShipmentsV3(int pageIndex, int numberOfRecord, String materialId, HttpServletRequest request, Authentication authentication, String globalId, String deleveryOrderNo, String bookingRefNo, String domainName, String containerId, String loadId, String carrierId, String customerId, Boolean hazardous, Boolean isPagination) throws ApplicationSettingsNotFoundException {
        List<ShipmentTrackingBeanV2> shipmentTrackingBeans = new LinkedList<>();
        String userId = tokenUtilService.getUserId(request);
        if (Utility.isEmpty(userId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message", shipmentTrackingBeans);
        }
        User user = userRepository.findOneByUserIdIgnoreCase(userId).orElse(new User());

        Criteria criteria = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("status").in(StatusEnum.getInTransitStatuses()));
        getQueryForParams(domainName, containerId, loadId, carrierId, customerId, hazardous, query);
        List<String> rules = user.getRules();
        List<String> contactIds;
        switch (user.getRole()) {
            case ADMIN:
                break;
            case SUB_ADMIN:
                break;
            case CUSTOMER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getCustomerCriteriaItemVisibility(criteria, rules, contactIds);
                break;
            case PLANNER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getPlannerCriteriaItemVisibility(criteria, rules, contactIds);
                break;
            case CARRIER:
                if (rules != null && !rules.isEmpty()) {
                    query.addCriteria(Criteria.where("carrierID").in(rules));
                }
                break;

            default:
                loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.SHIPMENT_NOT_FOUND).actionEnum(ActionEnum.FINDING_SHIPMENTS_INTRANSIT).loadId(loadId).build(), request);
                return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", shipmentTrackingBeans);
        }

        if (criteria != null) query.addCriteria(criteria);

        final String userTimeZone = Utility.getTimeZone(user);
        long totalRecords;
        if (isPagination != null && isPagination) {
            totalRecords = mongoTemplate.count(query, ShipmentV2.class);
            List<ShipmentV2> shipments = mongoTemplate.find(query, ShipmentV2.class);

            Map<String, LocationDoc> locationDocMap = getLocationMapByLoadId(shipments);

            List<ShipmentV2> shipmentList = shipments.stream()
                    .filter(shipment -> {
                        boolean validShipment = true;
                        if (!ObjectUtils.isEmpty(globalId) || !ObjectUtils.isEmpty(bookingRefNo)) {
                            validShipment = isValidShipment(globalId, bookingRefNo, shipment, validShipment);
                        }
                        return validShipment && !getShipmentTrackItems(shipment, Boolean.TRUE, locationDocMap).isEmpty();
                    }).toList();
            List<ShipmentTrackingBeanV2> shipmentTrackingBeanV2s = mapDataToShipmentTrackingBean(userTimeZone, shipmentList, user);

            int totalItems = shipmentTrackingBeanV2s.size();
            int totalPages = (int) Math.ceil((double) totalItems / numberOfRecord);
            if (pageIndex < 0 || pageIndex > totalPages) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "invalid page");
            }
            int startIndex = (pageIndex) * numberOfRecord;
            int endIndex = Math.min(startIndex + numberOfRecord, totalItems);

            List<ShipmentTrackingBeanV2> currentPageList = shipmentTrackingBeanV2s.subList(startIndex, endIndex);
            shipmentTrackingBeans.addAll(currentPageList);
            loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.INFO).message(Constants.SHIPMENT_FOUND).actionEnum(ActionEnum.FINDING_SHIPMENTS_INTRANSIT).loadId(loadId).build(), request);

            return new PageableResponse.PageableResponseBuilder(HttpStatus.OK).withMessage("Success").withData(checkForWatchList(currentPageList, user)).withPageInfo(pageIndex, numberOfRecord, totalRecords).build();
        }
        List<ShipmentV2> shipmentsData = mongoTemplate.find(query, ShipmentV2.class);
        try {
            shipmentTrackingBeans = mapDataToShipmentTrackingBean(userTimeZone, shipmentsData,user);
        } catch (Exception e) {
            loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(SHIPMENT_ERROR).actionEnum(ActionEnum.FINDING_SHIPMENT_TRACKING).loadId(loadId).build(), request);
        }
        return new ApiResponse(HttpStatus.OK, "Success " + shipmentsData.size(), shipmentTrackingBeans);
    }

    public List<ShipmentTrackItemBeanV2> getShipmentTrackItems(ShipmentV2 shipment, Boolean isIntransit,
                                                               Map<String, LocationDoc> locationDocMap) {
        Set<String> intransitItemIdList = new LinkedHashSet<>();
        List<ShipmentTrackItemBeanV2> beanList = new LinkedList<>();
        if (shipment.getStops() != null && !shipment.getStops().isEmpty()) {
            shipment.getStops().forEach(shipmentStopV2 -> {
                if (shipmentStopV2.getStopType().equalsIgnoreCase("P")
                        || shipmentStopV2.getStopType().equalsIgnoreCase("D")) {
                    if (isIntransit) {
                        if (!shipmentStopV2.isDelivered()) {
                            shipmentStopV2.getStopContent().getPallet().forEach(pallet -> intransitItemIdList.add(pallet.getPalletID()));
                        }
                    } else {
                        if (shipmentStopV2.getStopContent().getPallet() != null) {
                            shipmentStopV2.getStopContent().getPallet().forEach(pallet -> intransitItemIdList.add(pallet.getPalletID()));
                        }
                    }
                }
            });
        }
        intransitItemIdList.forEach(palletId -> {
            if (palletId != null && !palletId.isEmpty()) {
                shipment.getStops().stream().filter(shipmentStopV2 -> !shipmentStopV2.isDelivered() && shipmentStopV2.getStopType().equalsIgnoreCase("D"))
                        .forEach(shipmentStopV2 -> shipmentStopV2.getStopContent().getPallet().forEach(pallet -> {
                    if (pallet.getPalletID().equalsIgnoreCase(palletId.trim())) {
                        beanList.addAll(ShipmentTrackItemBeanV2.toShipmentTrackItemBeanV2(shipment, pallet,
                                locationDocMap));
                    }
                }));
            }
        });
        return beanList.stream().distinct().toList();
    }

    private void getQueryForParams(String domainName, String containerId, String loadId, String carrierId, String customerId, Boolean hazardous, Query query) {
        if (!ObjectUtils.isEmpty(domainName)) {
            query.addCriteria(Criteria.where("name").regex(domainName, "i"));
        }
        if (!ObjectUtils.isEmpty(containerId))
            query.addCriteria(Criteria.where("container.number").regex(containerId, "i"));

        if (!ObjectUtils.isEmpty(loadId)) query.addCriteria(Criteria.where("loadID").regex(loadId, "i"));

        if (!ObjectUtils.isEmpty(carrierId)) query.addCriteria(Criteria.where("carrierID").regex(carrierId, "i"));

        if (!ObjectUtils.isEmpty(customerId))
            query.addCriteria(Criteria.where("destination.siteId").regex(customerId, "i"));

        if (!Utility.isEmpty(hazardous)) {
            query.addCriteria(Criteria.where("loadReferences.loadReferenceType").is("HazardousFlag").and("loadReferences.content").is(String.valueOf(hazardous)));
        }
    }

    private Criteria getCustomerCriteriaItemVisibility(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            if (criteria == null) criteria = new Criteria();
            criteria.orOperator(Criteria.where("consignee.location.siteId").in(rules), new Criteria().andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("consignee.location.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds));
        }
        return criteria;
    }

    private Criteria getPlannerCriteriaItemVisibility(Criteria criteria, List<String> rules, List<String> contactIds) {
        if (rules != null && !rules.isEmpty() && !contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("source.siteId").in(rules), Criteria.where("destination.siteId").in(rules), new Criteria().andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds)));

        } else if (rules != null && !rules.isEmpty()) {
            criteria = new Criteria();
            criteria.orOperator(Criteria.where("source.siteId").in(rules), Criteria.where("destination.siteId").in(rules));
        } else if (!contactIds.isEmpty()) {
            criteria = new Criteria();
            criteria.andOperator(Criteria.where("loadReferences.loadReferenceType").is("LogisticsContact"), Criteria.where("loadReferences.content").in(contactIds));
        }
        return criteria;
    }

    @Override
    public ApiResponse getStops(String loadId, Boolean isItemVisibility, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(loadId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid loadId or orderId");
        }
        User user = userRepository.findOneByUserIdIgnoreCase(tokenUtilService.getUserId(request)).orElse(new User());
        String userTimeZone = Utility.getTimeZone(user);
        Optional<ShipmentV2> shipment = shipmentV2Repository.findOneByLoadID(loadId);
        return shipment.map(shipmentV2 -> new ApiResponse(HttpStatus.OK, "Successfully retrieved the stops.",
                        mapDataToShipmentStopBean(userTimeZone, isItemVisibility, shipmentV2)))
                .orElseGet(() -> new ApiResponse(HttpStatus.BAD_REQUEST, "Shipment not found"));
    }

    private List<ShipmentStopsDetailsBean> mapDataToShipmentStopBean(String userTimeZone, Boolean isItemVisibility, ShipmentV2 shipment) {
        List<ShipmentStopsDetailsBean> shipmentStopsDetailsBeans = new ArrayList<>();
        ShipmentStopsDetailsBean shipmentStopsDetailsBean = new ShipmentStopsDetailsBean();

        double distancePendingInKms = shipment.getDistancePendingInKms();
        double distanceTravelledInKms =  shipment.getDistanceTravelledInKms();
        double totalDistance = distancePendingInKms + distanceTravelledInKms;

        shipmentStopsDetailsBean.setDistanceTravelledInKms(distanceTravelledInKms);
        shipmentStopsDetailsBean.setDistancePendingInKms(distancePendingInKms);
        shipmentStopsDetailsBean.setTotalDistanceInKms(totalDistance);

        Double travelledDistancePercentage = Math.round((distanceTravelledInKms / totalDistance) * 100 * 10.0) / 10.0;
        shipmentStopsDetailsBean.setTravelledDistancePercentage(travelledDistancePercentage+"%");

        mapLoadReferencesToShipmentStopsDetails(shipmentStopsDetailsBean, shipment);
        shipmentStopsDetailsBean.setShipmentStopBeanList(getShipmentStopBeans(userTimeZone, isItemVisibility, shipment));
        shipmentStopsDetailsBeans.add(shipmentStopsDetailsBean);

        return shipmentStopsDetailsBeans;
    }

    private void mapLoadReferencesToShipmentStopsDetails(ShipmentStopsDetailsBean shipmentStopsDetailsBean, ShipmentV2 shipment) {
        shipment.getLoadReferences().stream()
                .filter(loadReference -> loadReference.getLoadReferenceType() != null)
                .forEach(loadReference -> {
                    switch (loadReference.getLoadReferenceType()) {
                        case KEY_DRIVER_NAME:
                            shipmentStopsDetailsBean.setDriverName(loadReference.getContent());
                            break;
                        case KEY_DRIVER_CONTACT:
                            shipmentStopsDetailsBean.setDriverContractNo(loadReference.getContent());
                            break;
                        case GLOBAL_ID:
                            shipmentStopsDetailsBean.setGlobalId(loadReference.getContent());
                            break;
                        default:
                            break;
                    }
                });
    }

    private List<ShipmentStopBean> getShipmentStopBeans(String userTimeZone, Boolean isItemVisibility, ShipmentV2 shipment) {
        return shipment.getStops().stream().map(shipmentStop -> {
            ShipmentStopBean shipmentStopBean = new ShipmentStopBean();
            shipmentStopBean.setStopSequence(shipmentStop.getStopNumber());
            shipmentStopBean.setStopType(shipmentStop.getStopType());
            shipmentStopBean.setEta(DateUtil.formatDate(DateUtil.convertDate(shipmentStop.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, shipmentStop.getEstimatedArrival().getTZId(), userTimeZone), DateUtil.dateTimeFormatter1));
            shipmentStopBean.setPta(DateUtil.formatDate(DateUtil.convertDate(shipmentStop.getPlannedArrival().getDateTime(), DateUtil.dateTimeFormatter2, shipmentStop.getPlannedArrival().getTZId(), userTimeZone), DateUtil.dateTimeFormatter1));
            shipmentStopBean.setCalculatedEta(shipmentStop.getCalculatedETA() != null ? (DateUtil.formatDate(DateUtil.convertDate(shipmentStop.getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter1))
                    : DateUtil.formatDate(DateUtil.convertDate(shipmentStop.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, shipmentStop.getEstimatedArrival().getTZId(), userTimeZone), DateUtil.dateTimeFormatter1));
            shipmentStopBean.setCalculatedEtd(shipmentStop.getCalculatedETD() != null ? DateUtil.formatDate(Objects.requireNonNull(DateUtil.convertDate(shipmentStop.getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone)), DateUtil.dateTimeFormatter1)
                    : DateUtil.formatDate(DateUtil.convertDate(shipmentStop.getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, shipmentStop.getEstimatedArrival().getTZId(), userTimeZone), DateUtil.dateTimeFormatter1));
            shipmentStopBean.setAta(shipmentStop.getActualArrival() == null ? "-" : DateUtil.formatDate(Objects.requireNonNull(DateUtil.convertDate(shipmentStop.getActualArrival().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone)), DateUtil.dateTimeFormatter1));
            shipmentStopBean.setAtd(shipmentStop.getActualDeparture() == null ? "-" : DateUtil.formatDate(Objects.requireNonNull(DateUtil.convertDate(shipmentStop.getActualDeparture().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone)), DateUtil.dateTimeFormatter1));
            List<OrdersDetailsSummaryBean> ordersDetailsSummaryBeanList = mapDataToOrderDetailsSummaryBean(shipmentStop.getStopContent().getPallet());
            shipmentStopBean.setOrderDetails(String.format("%.2f", ordersDetailsSummaryBeanList.stream()
                    .mapToDouble(weight -> Double.parseDouble(weight.getWeight())).sum()) + " "
                    + ordersDetailsSummaryBeanList.stream().map(OrdersDetailsSummaryBean::getMeasurement).distinct().collect(Collectors.joining()) + ", " +
                    ordersDetailsSummaryBeanList.stream()
                            .mapToDouble(volume -> Double.parseDouble(volume.getVolume())).sum() + " "
                    + (ordersDetailsSummaryBeanList.stream().map(OrdersDetailsSummaryBean::getVolumeMetric).distinct().collect(Collectors.joining())));
            if (isItemVisibility) {
                shipmentStopBean.setOrdersDetailsSummaryBeanList(mapDataToOrderDetailsSummaryBean(shipmentStop.getStopContent().getPallet()));
            }
            Optional<ShipmentLocation> locationOptional = Optional.ofNullable(shipmentStop.getLocation());
            locationOptional.ifPresent(locationDoc -> {
                shipmentStopBean.setLocationName(locationDoc.getSiteName());
                shipmentStopBean.setAddressDetails(locationDoc.getCity() + (Utility.isEmpty(locationDoc.getPostalCode()) ? "" : "-" + locationDoc.getPostalCode()));
            });
            return shipmentStopBean;
            }).toList();
    }

    private List<OrdersDetailsSummaryBean> mapDataToOrderDetailsSummaryBean(List<Pallet> pallets) {
        List<LocationDoc> destLocationList = locationRepository.findBySiteIdIn(pallets.stream().map(Pallet::getDestLocationID).toList());
        return pallets.stream().flatMap(pallet -> pallet.getPalletContents().stream())
                .map(palletContent -> {
                    OrdersDetailsSummaryBean ordersDetailsSummaryBean = new OrdersDetailsSummaryBean();
                    ordersDetailsSummaryBean.setId(palletContent.getProductID());
                    ordersDetailsSummaryBean.setName(palletContent.getProductName());
                    ordersDetailsSummaryBean.setQuantity(palletContent.getQuantity());
                    ordersDetailsSummaryBean.setMeasurement(palletContent.getNetWeightUOM());
                    ordersDetailsSummaryBean.setWeight(palletContent.getNetWeight());
                    ordersDetailsSummaryBean.setVolumeMetric(palletContent.getNetVolumeUOM());
                    ordersDetailsSummaryBean.setVolume(palletContent.getNetVolume());
                    ordersDetailsSummaryBean.setPalletId(palletContent.getPalletId());
                    Arrays.stream(palletContent.getMaterialReference())
                            .filter(materialReference -> materialReference != null && materialReference.getMaterialReferenceType() != null)
                            .forEach(materialReference -> {
                                switch (materialReference.getMaterialReferenceType()) {
                                    case "CustomerItemNumber":
                                        ordersDetailsSummaryBean.setCustomerItemNumber(materialReference.getContent());
                                        break;
                                    case "Commodity":
                                        ordersDetailsSummaryBean.setCommodity(materialReference.getContent());
                                        break;
                                    default:
                                }
                            });
                    ordersDetailsSummaryBean.setShipToLocation(
                            destLocationList.stream().filter(locationDoc -> locationDoc.getSiteId().equals(pallets.stream().map(Pallet::getDestLocationID)
                                    .findFirst().orElse(null))).map(LocationDoc::getSiteName).findFirst().orElse(null));
                    return ordersDetailsSummaryBean;
                }).toList();
    }


    @Override
    public ApiResponse getEvents(String loadId, Integer seqNo, HttpServletRequest request, boolean isList, boolean isFromParcelVisibility) {
        String userTimeZone;
        User user = userRepository.findOneByUserIdIgnoreCase(tokenUtilService.getUserId(request)).orElse(new User());
        userTimeZone = Utility.getTimeZone(user);
        if (!ObjectUtils.isEmpty(loadId)) {
            Page<ShipmentStatusDoc> events;
            if (isFromParcelVisibility) {
                events = !isList ? (shipmentStatusRepository.findByLoadIdAndStatusCodeIdNotOrderByCreatedDateDesc(loadId, StatusEnum.INTRANSIT.name(), Utility.pageable(0, 2))) : (new PageImpl<>(shipmentStatusRepository.findByLoadIdAndStatusCodeIdNotOrderByCreatedDateDesc(loadId, StatusEnum.INTRANSIT.name())));
            } else {
                if (!isList) {
                    if (seqNo != null)
                        events = shipmentStatusRepository.findByLoadIdAndStatusCodeIdNotAndStopSequenceOrderByCreatedDateDesc(loadId, StatusEnum.INTRANSIT.name(), seqNo, Utility.pageable(0, 2));
                    else
                        events = shipmentStatusRepository.findByLoadIdAndStatusCodeIdNotOrderByCreatedDateDesc(loadId, StatusEnum.INTRANSIT.name(), Utility.pageable(0, 2));
                } else {
                    events = (seqNo != null ? new PageImpl<>(shipmentStatusRepository.findByLoadIdAndStopSequenceOrderByCreatedDateAsc(loadId, seqNo))
                            : new PageImpl<>(shipmentStatusRepository.findByLoadIdOrderByEventDateOtmGLogDateAsc(loadId)));
                }
            }
            if (events.isEmpty()) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Please provide valid id");
            }
            List<EventSummaryBean> eventSummaryBeanList = getEventSummaryBeans(isFromParcelVisibility, events, userTimeZone);
            if (isList) {
                List<EventSummaryBean> statusBeanViewAll = eventSummaryBeanList.stream().sorted(Comparator.comparing(s -> parseShipmentStatus(s, userTimeZone))).collect(toList());
                Collections.reverse(statusBeanViewAll);
                return new ApiResponse(HttpStatus.OK, "Event summary list fetched successfully", statusBeanViewAll);
            }
            return new ApiResponse(HttpStatus.OK, "Event summary page data fetched successfully", eventSummaryBeanList);
        }
        return new ApiResponse(HttpStatus.BAD_REQUEST, "Please provide load id");
    }

    private static List<EventSummaryBean> getEventSummaryBeans(boolean isFromParcelVisibility, Page<ShipmentStatusDoc> events, String userTimeZone) {
        return events.getContent().stream().map(shipmentStatusDoc -> {
            EventSummaryBean eventSummaryBean = new EventSummaryBean();
            eventSummaryBean.setEventCode(isFromParcelVisibility && shipmentStatusDoc.getParcelRefNum().equalsIgnoreCase(PARCEL_REF_NUM)
                    ? shipmentStatusDoc.getQuickCodeGid() : shipmentStatusDoc.getStatusCodeId());
            eventSummaryBean.setEventDescription(shipmentStatusDoc.getStatusCodeId() != null && shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())
                    ? shipmentStatusDoc.getStatusReasonCodeGid() : shipmentStatusDoc.getEventDescription());
            eventSummaryBean.setCoordinates(shipmentStatusDoc.getLocation() != null ? shipmentStatusDoc.getLocation().getX() + "-" + shipmentStatusDoc.getLocation().getY() : null);
            eventSummaryBean.setEventDate(!ObjectUtils.isEmpty(userTimeZone) ? DateUtil.formatDate(shipmentStatusDoc.getEventDateAsLocalDate(userTimeZone), DateUtil.dateTimeFormatter1) : null);
            eventSummaryBean.setStopNo(shipmentStatusDoc.getStopSequence() != 0 ? shipmentStatusDoc.getStopSequence() : null);
            eventSummaryBean.setRemarks(shipmentStatusDoc.getRemarks() != null ? shipmentStatusDoc.getRemarks() : null);
            eventSummaryBean.setAddress(shipmentStatusDoc.getAddress() != null ? shipmentStatusDoc.getAddress() : null);
            return eventSummaryBean;
        }).toList();
    }

    private static LocalDateTime parseShipmentStatus(EventSummaryBean EventSummaryBean, String userTimeZone) {
        if (EventSummaryBean.getEventDate() != null) {
            return LocalDateTime.parse(EventSummaryBean.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        } else {
            return LocalDateTime.parse(DateUtil.formatDate(DateUtil.convertDate(new Date(), null, userTimeZone), DateUtil.dateFormate1), DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        }
    }

    @Override
    public ApiResponse getOrdersV3(String loadId, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(loadId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid loadId or orderId");
        }
        Optional<ShipmentV2> shipment = shipmentV2Repository.findOneByLoadID(loadId);

        if (shipment.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "SHIPMENT IS NOT PRESENT");
        }

        if (shipment.get().getOrders().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "NO SHIPMENT ORDERS PRESENT");
        }

        List<OrderV2> orders = fetchOrders(shipment.get());

        return new ApiResponse(HttpStatus.OK, "Success", orders);
    }

    private List<OrderV2> fetchOrders(ShipmentV2 shipment) {
        List<OrderV2Doc> orderDocs = ordersV2Repository.findByShipmentId(shipment.getLoadID());
        if (orderDocs.isEmpty()) {
            try {
                return loadExistingOrders(shipment.getOrders());
            } catch (Exception e) {
                logger.info("Failed to fetch existing orders for shipment");
                return Collections.emptyList();
            }
        } else {
            return orderDocs.stream().map(doc -> new OrderV2(
                            doc.getOrderId(),
                            doc.getBn(),
                            doc.getGlobalId(),
                            doc.getShipToLocationId(),
                            doc.getShipFromLocationId(),
                            doc.getTtUrl()))
                    .toList();
        }
    }

    public List<OrderV2> loadExistingOrders(Object json) {
        List<OrderV2> orders = new ArrayList<>();
        try {
            if (json instanceof JSONArray jsonArray && (!jsonArray.isEmpty())) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject doc = jsonArray.getJSONObject(i);
                        OrderV2 orderV2 = new OrderV2();
                        orderV2.setOrderId(doc.optString("orderId"));
                        orderV2.setBn(doc.optString("bn"));
                        orderV2.setGlobalId(doc.optString("globalId"));
                        orderV2.setShipToLocationId(doc.optString("shipToLocationId"));
                        orderV2.setShipFromLocationId(doc.optString("shipFromLocationId"));
                        orderV2.setTtUrl(doc.optString("ttUrl"));
                        orders.add(orderV2);
                    }

            }
        } catch (Exception e) {
            logger.error("An error occurred while processing JSON data", e);
        }
        return orders;
    }

    @Override
    public ApiResponse searchContainerAndShipments(String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String redirectPage, String globalId, String bookingRefNum, String deliveryNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String linerNumber, String logisticsContact, String sourceCountryCode, String destinationCountryCode, String atdFrom, String atdTo, String ataFrom, String ataTo, String etdFrom, String etdTo, String etaFrom, String etaTo, int pageIndex, int numberOfRecord, boolean lastMileDelivery, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        ApiResponse apiResponse;
        if (redirectPage.equalsIgnoreCase(MAP_CONTAINER_VISIBILITY.name())) {
            apiResponse = searchContainers(domainName, containerId, loadId, carrierName, hazardous, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, request);
            if (apiResponse.getStatus().is4xxClientError()) {
                return searchShipments(domainName, containerId, loadId, carrierName, hazardous, redirectPage, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, pageIndex, numberOfRecord, lastMileDelivery, request);
            } else return apiResponse;
        } else if (redirectPage.equalsIgnoreCase(CONTAINER_VISIBILITY_TABLE.name())) {
            apiResponse = searchContainers(domainName, containerId, loadId, carrierName, hazardous, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, request);
            if (apiResponse.getStatus().is4xxClientError())
                return searchShipments(domainName, containerId, loadId, carrierName, hazardous, redirectPage, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, pageIndex, numberOfRecord, lastMileDelivery, request);
            else return apiResponse;
        } else if (redirectPage.equalsIgnoreCase(PARCEL_VISIBILITY_TABLE.name())) {
            apiResponse = searchShipments(domainName, containerId, loadId, carrierName, hazardous, redirectPage, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, pageIndex, numberOfRecord, lastMileDelivery, request);
            if (apiResponse.getStatus().is4xxClientError()) {
                return searchContainers(domainName, containerId, loadId, carrierName, hazardous, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, atdFrom, atdTo, ataFrom, ataTo, etdFrom, etdTo, etaFrom, etaTo, request);
            } else return apiResponse;
        }
        return null;
    }

    public ApiResponse searchShipments(String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String redirectPage, String globalId, String bookingRefNum, String deliveryNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String linerNumber, String logisticsContact, String sourceCountryCode, String destinationCountryCode, String atdFrom, String atdTo, String ataFrom, String ataTo, String etdFrom, String etdTo, String etaFrom, String etaTo, int pageIndex, int numberOfRecord, boolean lastMileDelivery, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        if (Utility.isEmpty(redirectPage)) {
            loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.INVALID_REDIRECT_PAGE).actionEnum(ActionEnum.FINDING_SHIPMENT).loadId(loadId).build(), request);
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Provide valid search page name");
        }
        List<ShipmentTrackingBeanV2> list = new ArrayList<>();
        String userId = tokenUtilService.getUserId(request);
        if (Utility.isEmpty(userId)) {
            loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.USER_NOT_FOUND).actionEnum(ActionEnum.FINDING_SHIPMENT).loadId(loadId).build(), request);
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message", list);
        }
        User user = userRepository.findOneByUserIdIgnoreCase(userId).orElse(new User());
        String userTimeZone = Utility.getTimeZone(user);

        SearchResultPageConstant searchResultPageConstant = SearchResultPageConstant.valueOf(redirectPage);
        Query query = new Query();
        List<Criteria> criterias = searchShipmentsQueryBuilding(domainName, containerId, loadId, carrierName, hazardous, globalId, bookingRefNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, lastMileDelivery);
        List<String> rules = user.getRules();
        List<String> contactIds;
        Criteria criteria = new Criteria();
        switch (user.getRole()) {
            case ADMIN:
                break;
            case SUB_ADMIN:
                break;
            case CUSTOMER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getCustomerCriteria(criteria, rules, contactIds);
                break;
            case PLANNER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getPlannerCriteria(criteria, rules, contactIds);
                break;
            case CARRIER:
                if (rules != null && !rules.isEmpty()) {
                    criterias.add(Criteria.where("carrierID").in(user.getRules()));
                }
                break;
            default:
                loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.SHIPMENT_ERROR).actionEnum(ActionEnum.FINDING_SHIPMENT).loadId(loadId).build(), userId);
                return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", list);
        }
        Query queryAll = new Query();
        if (!criterias.isEmpty()) {
            queryAll = queryAll.addCriteria(new Criteria().andOperator(criterias.toArray(new Criteria[0])));
        }
        List<String> tlOrTruck = new ArrayList<>();
        tlOrTruck.add(TM_TL);
        tlOrTruck.add(TM_TRUCK);
        tlOrTruck.add(TM_LTL);
        tlOrTruck.add(GROUP_AGE);
        tlOrTruck.add(INTER_MODEL);
        if (searchResultPageConstant == MAP_CONTAINER_VISIBILITY || searchResultPageConstant == CONTAINER_VISIBILITY_TABLE) {
            query.addCriteria(Criteria.where("status").in(StatusEnum.getInOTMUpdateStatuses()));
        } else if (searchResultPageConstant == MAP_VEHICLE_VISIBILITY || searchResultPageConstant == TABLE_VEHICLE_VISIBILITY) {
            if (lastMileDelivery) {
                query.addCriteria(Criteria.where("status").in(StatusEnum.getLastMileDeliveryStatus()).and("mode").in(tlOrTruck));
            } else {
                query.addCriteria(Criteria.where("status").in(StatusEnum.getInOTMUpdateStatuses()).and("mode").in(tlOrTruck));
            }
        } else if (searchResultPageConstant == PARCEL_VISIBILITY_TABLE) {
            query.addCriteria(Criteria.where("status").in(StatusEnum.getExpressStatus()).and("mode").in(EXPRESS));
        } else if (searchResultPageConstant == COMPLETED) {
            query.addCriteria(Criteria.where("status").in(StatusEnum.getCompletedStatusesAsString()));
        } else if (searchResultPageConstant == MAP_ITEM_VISIBILITY) {
            query.addCriteria(Criteria.where("stops.stopContent.Pallet.0").exists(true).and("status").in(StatusEnum.getInTransitStatuses()).and("stops.isDelivered").is(false));
        }
        if (lastMileDelivery) {
            Optional<ShipmentV2> shipmentV2s = shipmentV2Repository.findOneByLoadID(loadId);
            ShipmentV2 shipmentV2 = shipmentV2s.orElse(null);
            if (shipmentV2 != null && shipmentV2.getOrders() != null) {
                List<String> collect = ordersV2Repository.findByShipmentId(shipmentV2.getLoadID()).stream().map(OrderV2Doc::getOrderId).toList();
                criteria.andOperator(Criteria.where("orders").in(collect));
                criteria.and("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("ContainerTracking").andOperator(Criteria.where("content").is("N")));
                criterias.add(criteria);

            }
        }
        if (!criterias.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterias.toArray(new Criteria[0])));
        }
        List<ShipmentV2> shipments = mongoTemplate.find(query, ShipmentV2.class);

        if (!ObjectUtils.isEmpty(deliveryNum)) {
            shipments = hasDeliveryNum(deliveryNum.trim(), shipments);
        }
        if (!ObjectUtils.isEmpty(atdFrom) && !ObjectUtils.isEmpty(atdTo)) {
            shipments = inBetweenRangeATD(atdFrom, atdTo, shipments, userTimeZone);
        }
        if (!ObjectUtils.isEmpty(ataFrom) && !ObjectUtils.isEmpty(ataTo)) {
            shipments = inBetweenRangeATA(ataFrom, ataTo, shipments, userTimeZone);
        }
        if (!ObjectUtils.isEmpty(etdFrom) && !ObjectUtils.isEmpty(etdTo)) {
            shipments = inBetweenRangeETD(etdFrom, etdTo, shipments, userTimeZone);
        }
        if (!ObjectUtils.isEmpty(etaFrom) && !ObjectUtils.isEmpty(etaTo)) {
            shipments = inBetweenRangeETA(etaFrom, etaTo, shipments, userTimeZone);
        }
        boolean isShipmentsExists = !shipments.isEmpty();
        if (!isShipmentsExists) {
            if (!ObjectUtils.isEmpty(deliveryNum) || !ObjectUtils.isEmpty(atdFrom) || !ObjectUtils.isEmpty(atdTo) || !ObjectUtils.isEmpty(ataFrom) || !ObjectUtils.isEmpty(ataTo) || !ObjectUtils.isEmpty(etdFrom) || !ObjectUtils.isEmpty(etdTo) || !ObjectUtils.isEmpty(etaFrom) || !ObjectUtils.isEmpty(etaTo)) {
                shipments = mongoTemplate.find(queryAll, ShipmentV2.class);
                if (!ObjectUtils.isEmpty(deliveryNum)) {
                    shipments = hasDeliveryNum(deliveryNum.trim(), shipments);
                }
                if (!ObjectUtils.isEmpty(atdFrom) && !ObjectUtils.isEmpty(atdTo)) {
                    shipments = inBetweenRangeATD(atdFrom, atdTo, shipments, userTimeZone);
                }
                if (!ObjectUtils.isEmpty(ataFrom) && !ObjectUtils.isEmpty(ataTo)) {
                    shipments = inBetweenRangeATA(ataFrom, ataTo, shipments, userTimeZone);
                }
                if (!ObjectUtils.isEmpty(etdFrom) && !ObjectUtils.isEmpty(etdTo)) {
                    shipments = inBetweenRangeETD(etdFrom, etdTo, shipments, userTimeZone);
                }
                if (!ObjectUtils.isEmpty(etaFrom) && !ObjectUtils.isEmpty(etaTo)) {
                    shipments = inBetweenRangeETA(etaFrom, etaTo, shipments, userTimeZone);
                }
            } else {
                ShipmentV2 shipmentV2 = mongoTemplate.findOne(queryAll, ShipmentV2.class);
                if (shipmentV2 != null) {
                    shipments.add(shipmentV2);
                }
            }
        }
        long totalRecords;
        query.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        totalRecords = mongoTemplate.count(query, ShipmentV2.class);
        query.with(PageRequest.of(pageIndex, numberOfRecord));
        List<ShipmentTrackingBeanV2> shipmentTrackingBeanV2s = mapDataToShipmentTrackingBean(userTimeZone, shipments, user);
        if (shipmentTrackingBeanV2s.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Shipment cannot be parsed");
        } else {
            return new PageableResponse.PageableResponseBuilder(HttpStatus.OK).withMessage("Success").withData(checkForWatchList(shipmentTrackingBeanV2s, user)).withPageInfo(pageIndex, numberOfRecord, totalRecords).build();
        }
    }

    @SneakyThrows
    private ApiResponse searchContainers(String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String globalId, String bookingRefNum, String deliveryNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String linerNumber, String logisticsContact, String sourceCountryCode, String destinationCountryCode, String atdFrom, String atdTo, String ataFrom, String ataTo, String etdFrom, String etdTo, String etaFrom, String etaTo, HttpServletRequest request) {
        final Map<String, LinkedList<ContainerShipmentBean>> containerMap = new LinkedHashMap<>();
        String userId = tokenUtilService.getUserId(request);
        if (Utility.isEmpty(userId)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.login.user_not_exist_message", containerMap);
        }
        if (!ObjectUtils.isEmpty(loadId)) {
            Query query = new Query();
            Criteria criteria = new Criteria();
            criteria.and("loadID").regex(loadId.trim(), "i");
            query.addCriteria(criteria);
            List<ShipmentV2> shipmentV2s = mongoTemplate.find(query, ShipmentV2.class);
            long count = shipmentV2s.stream().filter(shipmentV2 -> shipmentV2.getMode().equalsIgnoreCase(EXPRESS)).count();
            if (count > 0) {
                return new ApiResponse(HttpStatus.BAD_REQUEST, "Cannot find data");
            }
        }

        User user = userRepository.findOneByUserIdIgnoreCase(userId).orElse(new User());
        String userTimeZone = Utility.getTimeZone(user);

        Query query = new Query();
        query.addCriteria(Criteria.where("status").in(StatusEnum.getInTransitStatuses()));
        query.addCriteria(Criteria.where("shipmentsV2").exists(true));

        List<String> rules = user.getRules();
        List<String> contactIds;
        Criteria criteria = new Criteria();
        criteria.and("shipmentsV2").exists(true);
        criteria.and("status").in(StatusEnum.getInTransitStatuses());
        switch (user.getRole()) {
            case ADMIN:
                break;
            case SUB_ADMIN:
                break;
            case CUSTOMER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getCustomerCriteriaContainer(criteria, rules, contactIds);
                break;
            case PLANNER:
                contactIds = !ObjectUtils.isEmpty(user.getContactId()) ? Arrays.asList(user.getContactId().split(",")) : new ArrayList<>();
                criteria = getPlannerCriteriaContainer(criteria, rules, contactIds);
                break;
            case CARRIER:
                if (rules != null && !rules.isEmpty()) {
                    criteria.and("shipmentsV2.carrierId").in(user.getRules());
                }
                break;
            default:
                loggerService.saveLog(Log.builder().localDateTime(LocalDateTime.now()).type(MessageTypeEnum.ERROR).message(Constants.SHIPMENT_ERROR).actionEnum(ActionEnum.FINDING_SHIPMENT).loadId(loadId).build(), userId);
                return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", null);
        }

        addContainerCriteria(criteria, domainName, containerId, loadId, carrierName, hazardous, globalId, bookingRefNum, deliveryNum, customerName, customerId, shipper, originCity, originPort, destinationPort, status, shipTo, shipFrom, truckNumber, trailerNumber, linerNumber, logisticsContact, sourceCountryCode, destinationCountryCode, query);

        List<Container> containers = mongoTemplate.find(query, Container.class);

        List<Container> containerList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(deliveryNum)) {
            containerList = containers.stream().filter(container -> hasDeliveryNum(deliveryNum, container)).toList();
        }
        if (!ObjectUtils.isEmpty(atdFrom) && !ObjectUtils.isEmpty(atdTo)) {
            containers.removeIf(c -> inBetweenRangeATD(atdFrom, atdTo, c.getShipmentsV2(), userTimeZone).isEmpty());
        }
        if (!ObjectUtils.isEmpty(ataFrom) && !ObjectUtils.isEmpty(ataTo)) {
            containers.removeIf(c -> inBetweenRangeATA(ataFrom, ataTo, c.getShipmentsV2(), userTimeZone).isEmpty());
        }
        if (!ObjectUtils.isEmpty(etdFrom) && !ObjectUtils.isEmpty(etdTo)) {
            containers.removeIf(c -> inBetweenRangeETD(etdFrom, etdTo, c.getShipmentsV2(), userTimeZone).isEmpty());
        }
        if (!ObjectUtils.isEmpty(etaFrom) && !ObjectUtils.isEmpty(etaTo)) {
            containers.removeIf(c -> inBetweenRangeETA(etaFrom, etaTo, c.getShipmentsV2(), userTimeZone).isEmpty());
        }
        if (!containerList.isEmpty()) {
            containers = containerList;
        }

        query.with(Sort.by(Sort.Direction.DESC, "deliveryPta"));
        if (containerList.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.shipment.no_record_found", containerMap);
        }
        List<Container> newContainerList = new ArrayList<>();
        containers.stream().collect(Collectors.groupingBy(Container::getShipContainerId)).forEach((shipmentId, containers1) -> newContainerList.addAll(containers1));
        newContainerList.stream()
                .filter(sameContainer -> {
                    List<ShipmentV2> shipments = sortContainerShipments(sameContainer.getShipmentsV2().isEmpty() ? null : sameContainer.getShipmentsV2());
                    return shipments.stream().allMatch(shipment -> ObjectUtils.isEmpty(globalId) || ObjectUtils.isEmpty(bookingRefNum) || isValidShipment(globalId, bookingRefNum, shipment, true));
                })
                .forEach(sameContainer -> {
                    List<ContainerShipmentBean> containerShipmentBean = mapDataToContainerShipmentBean(userTimeZone, sortContainerShipments(sameContainer.getShipmentsV2()));
                    String conNumber = sameContainer.getShipContainerId() == null ? sameContainer.getContainerNumber() : sameContainer.getContainerNumber() + " " + sameContainer.getShipContainerId();
                    containerMap.computeIfAbsent(conNumber, k -> new LinkedList<>()).addAll(containerShipmentBean);
                });
        return new ApiResponse(HttpStatus.OK, "Success", containerMap);
    }

    private static void addContainerCriteria(Criteria criteria, String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String globalId, String bookingRefNum, String deliveryNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String linerNumber, String logisticsContact, String sourceCountryCode, String destinationCountryCode, Query query) throws PageRedirectionException {
        if (!ObjectUtils.isEmpty(domainName)) {
            criteria.and("shipmentsV2.partition").regex(domainName.trim(), "i");
        }
        if (!ObjectUtils.isEmpty(containerId)) {
            criteria.and("containerNumber").regex(containerId.trim(), "i");
        }
        if (!ObjectUtils.isEmpty(loadId)) {
            criteria.and("shipmentsV2.loadID").regex(loadId.trim(), "i");
        }

        if (!ObjectUtils.isEmpty(carrierName)) {
            criteria.and("shipmentsV2.carrierName").regex(carrierName.trim(), "i");
        }
        if (hazardous != null) {
            criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType").is("HazardousFlag").and("shipmentsV2.loadReferences.content").regex(String.valueOf(hazardous), "i"));
        }
        if (!ObjectUtils.isEmpty(globalId)) {
            criteria.and("shipmentsV2.loadReferences.loadReferenceType").is(GLOBAL_ID).and("shipmentsV2.loadReferences.content").regex(globalId.trim(), "i");
        }
        if (!ObjectUtils.isEmpty(bookingRefNum)) {
            criteria.and("shipmentsV2.loadReferences.loadReferenceType").is(BOOKING_REF_NUM).and("shipmentsV2.loadReferences.content").regex(bookingRefNum.trim(), "i");
        }
        if (!ObjectUtils.isEmpty(customerName)) {
            criteria.and("shipmentsV2.destination.siteName").regex(customerName, "i");
        }
        if (!ObjectUtils.isEmpty(customerId)) {
            criteria.and("shipmentsV2.destination.siteId").regex(customerId, "i");
        }
        if (!ObjectUtils.isEmpty(shipper)) {
            criteria.and("shipmentsV2").elemMatch(Criteria.where("shipper.location.siteName").regex(shipper, "i"));
        }
        if (!ObjectUtils.isEmpty(originCity)) {
            criteria.and("shipmentsV2.source.city").regex(originCity, "i");
        }
        if (!ObjectUtils.isEmpty(originPort)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("SOURCE_PORT").and("content").regex(originPort, "i"));
        }
        if (!ObjectUtils.isEmpty(destinationPort)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("DESTINATION_PORT").and("content").regex(destinationPort, "i"));
        }
        if (!ObjectUtils.isEmpty(status)) {
            criteria.and("shipmentsV2.status").regex(status, "i");
        }
        if (!ObjectUtils.isEmpty(shipTo)) {
            criteria.and("shipmentsV2.destination.siteId").regex(shipTo, "i");
        }
        if (!ObjectUtils.isEmpty(shipFrom)) {
            criteria.and("shipmentsV2.source.siteId").regex(shipFrom, "i");
        }
        if (!ObjectUtils.isEmpty(truckNumber)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("VehicleNumber").and("content").regex(truckNumber.trim(), "i"));
        }
        if (!ObjectUtils.isEmpty(trailerNumber)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("TrailerNumber").and("content").regex(trailerNumber.trim(), "i"));
        }
        if (!ObjectUtils.isEmpty(linerNumber)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("LinerNumber").and("content").regex(linerNumber.trim(), "i"));
        }
        if (!ObjectUtils.isEmpty(logisticsContact)) {
            criteria.and("shipmentsV2.loadReferences").elemMatch(Criteria.where("loadReferenceType").is("LogisticsContact").and("content").regex(logisticsContact.trim(), "i"));
        }
        if (!ObjectUtils.isEmpty(sourceCountryCode)) {
            criteria.and("shipmentsV2.source.countryCode").regex(sourceCountryCode, "i");
        }
        if (!ObjectUtils.isEmpty(destinationCountryCode)) {
            criteria.and("shipmentsV2.destination.countryCode").regex(destinationCountryCode, "i");
        }
        if (criteria == null && ObjectUtils.isEmpty(deliveryNum)) {
            throw new PageRedirectionException("Please select filter");
        }
        if (criteria != null) {
            query.addCriteria(criteria);
        }
    }


    @SneakyThrows
    private List<Criteria> searchShipmentsQueryBuilding(String domainName, String containerId, String loadId, String carrierName, Boolean hazardous, String globalId, String bookingRefNum, String customerName, String customerId, String shipper, String originCity, String originPort, String destinationPort, String status, String shipTo, String shipFrom, String truckNumber, String trailerNumber, String logisticsContact, String linerNumber, String sourceCountryCode, String destinationCountryCode, boolean lastMileDelivery) {
        List<Criteria> criteria = new ArrayList<>();
        if (!ObjectUtils.isEmpty(domainName)) {
            criteria.add(Criteria.where("partition").regex(domainName.trim(), "i"));
        }
        if (!ObjectUtils.isEmpty(containerId)) {
            criteria.add(Criteria.where("container.number").regex(containerId.trim(), "i"));
        }
        if (!lastMileDelivery && !ObjectUtils.isEmpty(loadId)) {
            criteria.add(Criteria.where("loadID").regex(loadId.trim(), "i"));
        }

        if (!ObjectUtils.isEmpty(carrierName)) {
            criteria.add(Criteria.where("carrierName").regex(carrierName.trim(), "i"));
        }
        if (hazardous != null) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("HazardousFlag").and("content").regex(String.valueOf(hazardous), "i")));
        }
        if (!ObjectUtils.isEmpty(globalId)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is(GLOBAL_ID).and("content").regex(globalId.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(bookingRefNum)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is(BOOKING_REF_NUM).and("content").regex(bookingRefNum.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(customerName)) {
            criteria.add(Criteria.where("destination.siteName").regex(customerName, "i"));
        }
        if (!ObjectUtils.isEmpty(customerId)) {
            criteria.add(Criteria.where("destination.siteId").regex(customerId, "i"));
        }
        if (!ObjectUtils.isEmpty(shipper)) {
            criteria.add(Criteria.where("shipper.location.siteName").regex(shipper, "i"));
        }
        if (!ObjectUtils.isEmpty(originCity)) {
            criteria.add(Criteria.where("source.city").regex(originCity, "i"));
        }
        if (!ObjectUtils.isEmpty(originPort)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("SOURCE_PORT").and("content").regex(originPort, "i")));
        }
        if (!ObjectUtils.isEmpty(destinationPort)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("DESTINATION_PORT").and("content").regex(destinationPort, "i")));
        }
        if (!ObjectUtils.isEmpty(status)) {
            criteria.add(Criteria.where("status").regex(status, "i"));
        }
        if (!ObjectUtils.isEmpty(shipTo)) {
            criteria.add(Criteria.where("destination.siteId").regex(shipTo, "i"));
        }
        if (!ObjectUtils.isEmpty(shipFrom)) {
            criteria.add(Criteria.where("source.siteId").regex(shipFrom, "i"));
        }
        if (!ObjectUtils.isEmpty(truckNumber)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("VehicleNumber").and("content").regex(truckNumber.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(trailerNumber)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("TrailerNumber").and("content").regex(trailerNumber.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(linerNumber)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("LinerNumber").and("content").regex(linerNumber.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(logisticsContact)) {
            criteria.add(Criteria.where("loadReferences").elemMatch(Criteria.where("loadReferenceType").is("LogisticsContact").and("content").regex(logisticsContact.trim(), "i")));
        }
        if (!ObjectUtils.isEmpty(sourceCountryCode)) {
            criteria.add(Criteria.where("source.countryCode").regex(sourceCountryCode, "i"));
        }
        if (!ObjectUtils.isEmpty(destinationCountryCode)) {
            criteria.add(Criteria.where("destination.countryCode").regex(destinationCountryCode, "i"));
        }
        return criteria;
    }

    private List<ShipmentV2> hasDeliveryNum(String deliveryNum, List<ShipmentV2> shipments) {
        return shipments.stream()
                .filter(shipment ->
                        shipment.getStops().stream()
                                .flatMap(stop -> stop.getStopContent().getPallet().stream())
                                .anyMatch(pallet ->
                                        pallet.getOrder().contains(deliveryNum)
                                )
                )
                .distinct()
                .toList();
    }

    private List<ShipmentV2> inBetweenRangeETA(String fromDate, String toDate, List<ShipmentV2> shipments, String userTimeZone) {
        return shipments.stream().filter(s -> {
            int stopSize = s.getStops().size();
            if (stopSize > 0) {
                if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getCalculatedETA()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getCalculatedETA().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getCalculatedETA().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getEstimatedArrival()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getEstimatedArrival().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getEstimatedArrival().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else return false;
            } else return false;
        }).toList();
    }

    private List<ShipmentV2> inBetweenRangeETD(String fromDate, String toDate, List<ShipmentV2> shipments, String userTimeZone) {
        return shipments.stream().filter(s -> {
            int stopSize = s.getStops().size();
            if (stopSize > 0) {
                if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getCalculatedETD()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getCalculatedETD().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getCalculatedETD().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getEstimatedDeparture()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getEstimatedDeparture().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getEstimatedDeparture().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else return false;
            } else return false;
        }).toList();
    }

    private List<ShipmentV2> inBetweenRangeATA(String fromDate, String toDate, List<ShipmentV2> shipments, String userTimeZone) {
        return shipments.stream().filter(s -> {
            int stopSize = s.getStops().size();
            if (stopSize > 0) {
                if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getActualArrival()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getActualArrival().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getActualArrival().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else return false;
            } else return false;

        }).toList();
    }

    private List<ShipmentV2> inBetweenRangeATD(String fromDate, String toDate, List<ShipmentV2> shipments, String userTimeZone) {
        return shipments.stream().filter(s -> {
            int stopSize = s.getStops().size();
            if (stopSize > 0) {
                if (!ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getActualDeparture()) && !ObjectUtils.isEmpty(s.getStops().get(stopSize - 1).getActualDeparture().getDateTime())) {
                    return isBetweenRange(fromDate, toDate, DateUtil.formatDate(DateUtil.convertDate(s.getStops().get(stopSize - 1).getActualDeparture().getDateTime(), DateUtil.dateTimeFormatter2, null, userTimeZone), DateUtil.dateTimeFormatter2));
                } else return false;
            } else return false;

        }).toList();
    }

    @SneakyThrows
    private Boolean isBetweenRange(String fromDate, String toDate, String shipmentDate) {
        Date convertedFromDate = new SimpleDateFormat("yyyyMMddhhmmss").parse(fromDate);
        Date convertedToDate = new SimpleDateFormat("yyyyMMddhhmmss").parse(toDate);
        Date convertedShipmentDate = new SimpleDateFormat("yyyyMMddhhmmss").parse(shipmentDate);
        return convertedShipmentDate.after(convertedFromDate) && convertedShipmentDate.before(convertedToDate);
    }

    private boolean hasDeliveryNum(String deliveryNum, Container container) {
        return container.getShipmentsV2().stream()
                .flatMap(shipment -> shipment.getStops().stream())
                .filter(shipmentStopV2 -> shipmentStopV2.getStopContent() != null &&
                        shipmentStopV2.getStopContent().getPallet() != null)
                .flatMap(shipmentStopV2 -> shipmentStopV2.getStopContent().getPallet().stream())
                .anyMatch(pallet -> pallet.getOrder().contains(deliveryNum) &&
                        container.getShipmentsV2().stream().noneMatch(shipment -> container.getShipmentsV2().contains(shipment)));
    }

    //completed shipments
    ShipmentTrackingV2 shipmentTrackingV2=new ShipmentTrackingV2();

    @Override
    public ApiResponse getCompletedShipments(int tab, int pageIndex, int numberOfRecord, String containerId, String loadId, String carrierId, String customerId, Boolean hazardous, HttpServletRequest request, String globalId, String deleveryOrderNo, String bookingRefNo, int days, String domainName) {
        List<ShipmentV2Bean> shipmentV2List=shipmentV2Repository.findAll().stream().filter(s->s.getStatus()!=null && s.getStatus().equals(CLOSED)).map(shipmentTrackingV2::shipmentV2ToBean).collect(toList());
        return new ApiResponse(HttpStatus.OK,shipmentV2List);
    }

    @Override
    public ApiResponse getStatusCount(HttpServletRequest request) {

        StatusCountBean countBean = new StatusCountBean();
        List<String> modes = new ArrayList<>();
        modes.add(TM_LTL);
        modes.add(TM_TL);
        modes.add(GROUP_AGE);
        modes.add(INTER_MODEL);
        modes.add(TM_TRUCK);
        Stream.of(StatusEnum.values()).forEach(statusEnum -> {
            if (statusEnum.name().equalsIgnoreCase(StatusEnum.ARRIVING_EARLY.name()))
                countBean.setArrivingEarly(shipmentV2Repository.countByStatusAndModeIn(StatusEnum.ARRIVING_EARLY.name(), modes));
            if (statusEnum.name().equalsIgnoreCase(StatusEnum.ONTIME.name()))
                countBean.setOnTime(shipmentV2Repository.countByStatusAndModeIn(StatusEnum.ONTIME.name(), modes));
            if (statusEnum.name().equalsIgnoreCase(StatusEnum.DELAYED.name()))
                countBean.setLate(shipmentV2Repository.countByStatusAndModeIn(StatusEnum.DELAYED.name(), modes));
            if (statusEnum.name().equalsIgnoreCase(StatusEnum.NOT_STARTED.name()))
                countBean.setUntracked(shipmentV2Repository.countByStatusAndModeIn(StatusEnum.NOT_STARTED.name(), modes));
        });

        return new ApiResponse.ApiResponseBuilder().setStatus(HttpStatus.OK).setData(countBean).build();
    }
}