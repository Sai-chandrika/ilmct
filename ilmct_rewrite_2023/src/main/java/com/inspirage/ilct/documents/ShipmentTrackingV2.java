package com.inspirage.ilct.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inspirage.ilct.dto.AlertsBean;
import com.inspirage.ilct.dto.OrderV2;
import com.inspirage.ilct.dto.PageShipmentBean;
import com.inspirage.ilct.dto.ShipmentV2Bean;
import com.inspirage.ilct.dto.bean.LocationResBean;
import com.inspirage.ilct.dto.bean.ShipmentTrackItemBeanV2;

import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import com.inspirage.ilct.dto.here.weather.Alerts;
import com.inspirage.ilct.dto.here.weather.Observation;
import com.inspirage.ilct.util.DateUtil;
import com.inspirage.ilct.util.SearchResultPageConstant;
import com.inspirage.ilct.util.Utility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.inspirage.ilct.util.Constants.KEY_HAZARDOUS_TYPE;
import static com.inspirage.ilct.util.Constants.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShipmentTrackingV2 {

    private String id;
    private String loadId;
    private LatLng current;
    private LatLng source;
    private LatLng destination;
    private String carrierId;
    private MasterTruckType truckType;
    private boolean isHighTemperatureAlert = false;
    private boolean isHighSpeedAlert = false;
    private boolean isLowFuelAlert = false;
    private LinkedList<LocationResBean> locations = new LinkedList<>();
    private String weatherAlerts;
    private String highTemperature;
    private String lowTemperature;
    private String humidity;
    private String pickUp, delivery, status;
    private String pickupEta;
    private int eventType;
    private String pickupPta;
    private String deliveryEta;
    private String deliveryPta;
    private String deliveryPromisedEta;
    private String driverName;
    private String driverContact;
    private String truckNo;
    private boolean isHazardous;
    private String shipper;
    private String consignee;
    private String ff;
     private String hazardousValue;
    private String containerNumber;
    private String transportMode;
    private String vessel;
    private String orderId;
    private String timeZone;
    private String currentStatus;
    private String currentSpeed;
    private String currentFuel;
    private String currentTemp;
    private Double rotateAngle;
    private List<ShipmentTrackItemBeanV2> shipmentTrackItems = new LinkedList<>();
    private String lastSeen;
    private String lastSeen_colorCode;
    private String globalId;
    private String bnNo;
    private String deliveryOrderNo;
    private String containerStatus;

    private Boolean isChinaShipment = Boolean.FALSE;
    private String pickupCountryCode;

    private PageShipmentBean shipmentV2;
    private String carrierName;
    private Integer noOfDeliveryStops;
    private String trailerNumber;
    private Double totalWeight;

    private List<AlertsBean> alerts = new ArrayList<>();
    private List<SpecialServicesBean> specialServices = new ArrayList<>();

    private Boolean isWatchlistAdded = Boolean.FALSE;
    private ProgressBar progressBar;
    private Double weightPercentage;
    private String inTransit;
    private Boolean isLastMileDelivery = Boolean.FALSE;
    public ShipmentTrackingV2(String userTimeZone, Units.TemperatureUnit temperatureUnit,
                                  Units.DistanceUnit distanceUnit, ShipmentV2 shipment,
                                  List<MasterTruckType> masterTruckTypeList, MasterTruckType defaultTruckType,
                                  Map<String, LocationDoc> locationDocMap, List<ShipmentStatusDoc> shipmentStatusIntransitList, String redirectPage, List<OrderV2Doc> orderV2Docs) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (shipment != null) {
            PageShipmentBean bean = new PageShipmentBean();
            bean.setContainer(shipment.getContainer());
            bean.setDestination(shipment.getDestination());
            bean.setDistancePendingInKms(shipment.getDistancePendingInKms());
            bean.setDistanceTravelledInKms(shipment.getDistanceTravelledInKms());
            bean.setFileDocuments(shipment.getFileDocuments());
            bean.setLoadID(shipment.getLoadID());
            bean.setLoadReferences(shipment.getLoadReferences());
            bean.setSource(shipment.getSource());
            if (shipment.getStatus() != null)
                bean.setStatus(shipment.getStatus().name());
            bean.setStops(shipment.getStops());
            bean.setTtURL(shipment.getTtURL());
            bean.setWeightPercentage(shipment.getWeightPercentage());
            bean.setMode(shipment.getMode());

            List<OrderV2> orders = new ArrayList<>();
            if (orderV2Docs != null) {
                for (OrderV2Doc doc : orderV2Docs) {
                    if (doc.getShipmentId().contains(shipment.getLoadID())) {
                        OrderV2 orderV2 = new OrderV2();
                        orderV2.setOrderId(doc.getOrderId());
                        orderV2.setBn(doc.getBn());
                        orderV2.setGlobalId(doc.getGlobalId());
                        orderV2.setShipToLocationId(doc.getShipToLocationId());
                        orderV2.setShipFromLocationId(doc.getShipFromLocationId());
                        orderV2.setTtUrl(doc.getTtUrl());
                        orders.add(orderV2);
                    }
                }
                bean.setOrderV2s(orders);
            }

            setShipmentV2(bean);
            setCarrierName(shipment.getCarrierName());
            setId(shipment.getId());
            setTotalWeight(convertTotalWeight(shipment));
            setLoadId(shipment.getLoadID());
            List<String> orderNumbers = new ArrayList<>();
            for (ShipmentStopV2 shipmentStopV2 : shipment.getStops()) {
                if (shipmentStopV2.getStopContent() != null && shipmentStopV2.getStopContent().getPallet() != null) {
                    for (Pallet pallet : shipmentStopV2.getStopContent().getPallet()) {
                        orderNumbers.addAll(pallet.getOrder());
                    }
                }
            }
            setDeliveryOrderNo(orderNumbers.stream().distinct().collect(Collectors.joining(",")));
            if (shipment.getContainer() != null) setContainerNumber(shipment.getContainer().getNumber());
            setCarrierId(shipment.getCarrierID());
            if (shipment.getDestination() != null) {
                setDelivery(shipment.getDestination().getSiteName());
            }
            if (shipment.getSource() != null) {
                setPickUp(shipment.getSource().getSiteName());
            }
            if (shipment.getStatus() != null)
                setStatus(shipment.getStatus().name());
            if (masterTruckTypeList != null) {
                Optional<MasterTruckType> truckType = masterTruckTypeList.stream().filter(s -> s.getType().equals(shipment.getLoadMeasure().getTruckType())).findAny();
                if (truckType.isPresent()) setTruckType(truckType.get());
                else {
                    if (defaultTruckType != null)
                        setTruckType(defaultTruckType);
                }
            }
            setTransportMode(shipment.getMode());
            setCurrent(shipment.getCurrent());

            if (getCurrent() == null) {
                if (shipment.getSource() != null && locationDocMap != null && locationDocMap.get(shipment.getSource().getSiteId()) != null)
                    setCurrent(locationDocMap.get(shipment.getSource().getSiteId()).getLocation());
            }

            if (!Utility.isEmpty(shipment.getLoadMeasure()) && !Utility.isEmpty(shipment.getLoadMeasure().getWeightUtilization())) {
                setWeightPercentage(Double.valueOf(df.format(Utility.parseDoubleOrNull(shipment.getLoadMeasure().getWeightUtilization()) * 100D)));
            } else if (!Utility.isEmpty(shipment.getLoadMeasure()) && Utility.isEmpty(shipment.getLoadMeasure().getWeightUtilization())) {
                if (!Utility.isEmpty(this.getTruckType().getActualTruckWeight())) {
                    setWeightPercentage(Double.valueOf(df.format(this.getTotalWeight() * 100D / this.getTruckType().getActualTruckWeight())));
                } else setWeightPercentage(0D);
            } else setWeightPercentage(0D);
            if (shipment.getLoadReferences() != null && !shipment.getLoadReferences().isEmpty()) {
                shipment.getLoadReferences().forEach(loadReference -> {
                    if (loadReference.getLoadReferenceType() != null)
                        switch (loadReference.getLoadReferenceType()) {
                            case KEY_TRUCK_NO:
                                setTruckNo(loadReference.getContent());
                                break;
                            case KEY_DRIVER_CONTACT:
                                setDriverContact(loadReference.getContent());
                                break;
                            case KEY_DRIVER_NAME:
                                setDriverName(loadReference.getContent());
                                break;
                            case KEY_LEG_TYPE:
                                if (ObjectUtils.isNotEmpty(loadReference.getContent()))
                                    break;
                            case KEY_HAZARDOUS_TYPE:
                                if (!ObjectUtils.isEmpty(loadReference.getContent())) {
                                    setHazardousValue(loadReference.getContent());
                                    setHazardous(true);
                                } else {
                                    setHazardous(false);
                                    setHazardousValue(null);
                                }
                                break;
                            case KEY_CONTAINER_ID:
                                setContainerNumber(loadReference.getContent());
                                break;
                            case KEY_BM:
                                break;
                            case KEY_CN:
                                break;
                            case GLOBAL_ID:
                                setGlobalId(loadReference.getContent());
                                break;
                            case "BN":
                                if (ObjectUtils.isNotEmpty(shipment.getLoadType()) && shipment.getLoadType().equalsIgnoreCase("OUTBOUND")) {
                                    setBnNo(loadReference.getContent());
                                }
                                break;
                            case "PO":
                            case "PO_NUMBER":
                                if (ObjectUtils.isNotEmpty(shipment.getLoadType()) && shipment.getLoadType().equalsIgnoreCase("INBOUND")) {
                                    setBnNo(loadReference.getContent());
                                }
                                break;
                            case "VesselNumber":
                                setVessel(loadReference.getContent());
                                break;
                            case "TrailerNumber":
                                setTrailerNumber(loadReference.getContent());
                                break;
                        }
                });
            }
            AtomicInteger noOfDeliveryStops = new AtomicInteger();
            if (shipment.getStops() != null && !shipment.getStops().isEmpty()) {
                if (shipment.getStops() != null && !shipment.getStops().isEmpty()) {

                    if (locationDocMap != null) {
                        if (shipment.getSource() != null) {
                            LocationDoc doc = locationDocMap.get(shipment.getSource().getSiteId());
                            if (doc != null) {
                                setSource(doc.getLocation());
                            }
                        }
                        if (shipment.getDestination() != null) {
                            LocationDoc doc = locationDocMap.get(shipment.getDestination().getSiteId());
                            if (doc != null) {
                                setDestination(doc.getLocation());
                            }
                        }
                    }
                }
                shipment.getStops().forEach(shipmentStopV2 -> {
                    LocationDoc doc = locationDocMap.get(shipmentStopV2.getLocation().getSiteId());
                    switch (shipmentStopV2.getStopType()) {
                        case "P":
                            setTimeZone(userTimeZone);
                            setPickupEta(DateUtil.formatDate(DateUtil.convertDate(
                                    shipmentStopV2.getEstimatedArrival().getDateTime(),
                                    DateUtil.dateTimeFormatter2,
                                    shipmentStopV2.getEstimatedArrival().getTZId(),
                                    userTimeZone), DateUtil.dateTimeFormatter1));
                            setPickupPta(DateUtil.formatDate(DateUtil.convertDate(
                                    shipmentStopV2.getPlannedArrival().getDateTime(),
                                    DateUtil.dateTimeFormatter2,
                                    shipmentStopV2.getPlannedArrival().getTZId(),
                                    userTimeZone), DateUtil.dateTimeFormatter1));
                            break;
                        case "D":
                            noOfDeliveryStops.getAndIncrement();
                            if (shipmentStopV2.getCalculatedETA() != null) {
                                setDeliveryEta(DateUtil.formatDate(DateUtil.convertDate(
                                        shipmentStopV2.getCalculatedETA().getDateTime(),
                                        DateUtil.dateTimeFormatter2,
                                        shipmentStopV2.getCalculatedETA().getTZId(),
                                        userTimeZone), DateUtil.dateTimeFormatter1));
                            }
                            setDeliveryPta(DateUtil.formatDate(DateUtil.convertDate(
                                    shipmentStopV2.getPlannedArrival().getDateTime(),
                                    DateUtil.dateTimeFormatter2,
                                    shipmentStopV2.getPlannedArrival().getTZId(),
                                    userTimeZone), DateUtil.dateTimeFormatter1));
                            setDeliveryPromisedEta(DateUtil.formatDate(DateUtil.convertDate(
                                    shipmentStopV2.getEstimatedArrival().getDateTime(),
                                    DateUtil.dateTimeFormatter2,
                                    shipmentStopV2.getEstimatedArrival().getTZId(),
                                    userTimeZone), DateUtil.dateTimeFormatter1));
                            break;
                    }
                    if (doc != null)
                        locations.add(new LocationResBean(this, doc, shipmentStopV2.getStopType()));
                });
            }
            setNoOfDeliveryStops(noOfDeliveryStops.get());
            if (shipment.getConsignee() != null && shipment.getConsignee().getLocation() != null) {
                setConsignee(shipment.getConsignee().getLocation().getSiteName());
            }
            if (shipment.getShipper() != null && shipment.getShipper().getLocation() != null) {
                setShipper(shipment.getShipper().getLocation().getSiteName());
            }

            if (shipment.getForwarder() != null && shipment.getForwarder().getLocation() != null) {
                setFf(shipment.getForwarder().getLocation().getSiteName());
            }
            if (shipment.getWeatherAlert() != null) {
                Alerts alerts=shipment.getWeatherAlert();
                Optional<Observation> observation = Optional.ofNullable(alerts.getObservation() != null && !alerts.getObservation().isEmpty()
                        ? alerts.getObservation().get(0) : null);
                this.setWeatherAlerts((alerts.getAlerts() == null || alerts.getAlerts().isEmpty()) ? null
                        : alerts.getAlerts().stream().map(a -> a.getDescription()).collect(Collectors.joining("<br>")));

                this.setHighTemperature(observation
                        .map(o -> temperatureUnit.getTemperatureDisplayUnit(o.getHighTemperature())).orElse(null));
                this.setLowTemperature(observation
                        .map(o -> temperatureUnit.getTemperatureDisplayUnit(o.getLowTemperature())).orElse(null));

                this.setHumidity(observation.map(o -> o.getHumidity() + " %").orElse(null));
            }

            setOrderId(getBnNo());
            setCurrentTemp(temperatureUnit != null
                    ? temperatureUnit.getTemperatureDisplayUnit(shipment.getCurrentTemp()) : null);
            setCurrentSpeed(distanceUnit != null ? distanceUnit.getSpeedDisplayUnit(shipment.getCurrentSpeed()) : null);
            setCurrentFuel(shipment.getCurrentFuel());
            setIsChinaShipment(shipment.getIsChinaShipment());
            if (shipment.getSource() != null && shipment.getSource().getCountryCode() != null) {
                setPickupCountryCode(shipment.getSource().getCountryCode());
            }

            if (redirectPage != null && (redirectPage.equalsIgnoreCase(SearchResultPageConstant.MAP_ITEM_VISIBILITY.name()) || redirectPage.equalsIgnoreCase(SearchResultPageConstant.MAP_CONTAINER_VISIBILITY.name()))) {
                setShipmentTrackItems(getShipmentTrackItems(shipment, Boolean.FALSE, locationDocMap));
            }
            df.setRoundingMode(RoundingMode.UP);
            if (!this.shipmentV2.getStops().isEmpty()) {
                List<ShipmentStopV2> progressStops = this.shipmentV2.getStops();
                ProgressBar progressBar = new ProgressBar();
                if (transportMode != null && (this.transportMode.equalsIgnoreCase(TM_TL) || this.transportMode.equalsIgnoreCase(TM_LTL) ||
                        this.transportMode.equalsIgnoreCase(GROUP_AGE) || this.transportMode.equalsIgnoreCase(INTER_MODEL) || this.transportMode.equalsIgnoreCase(TM_TRUCK))) {
                    Double distanceTravelled = 0.0;
                    Double distancePending = 0.0;
                    if (this.shipmentV2.getDistanceTravelledInKms() != null) {
                        distanceTravelled = this.shipmentV2.getDistanceTravelledInKms();
                    }
                    if (this.shipmentV2.getDistancePendingInKms() != null) {
                        distancePending = this.shipmentV2.getDistancePendingInKms();
                    }
                    double totalDistance = distanceTravelled + distancePending;
                    Double distancePercentage = 0.0;
                    Double totalStopDistance = progressStops.stream().mapToDouble(ShipmentStopV2::getDistanceInKms).sum();
                    if (totalDistance != 0.0 && totalDistance > shipment.getDistancePendingInKms()) {
                        distancePercentage = (totalDistance - shipment.getDistancePendingInKms()) * 100 / totalDistance;
                        if (totalStopDistance == 0.0) {
                            progressStops.forEach(s -> {
                                if (s.getStopType().equalsIgnoreCase("D")) {
                                    s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(100 / (progressStops.size() - 1))));
                                } else s.setTotalDistanceInKmsPercentage(0.0);
                            });
                        } else
                            progressStops.forEach(s -> s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(s.getDistanceInKms() * 100 / totalStopDistance))));
                    } else if (totalDistance != 0.0 && totalDistance <= shipment.getDistancePendingInKms()) {
                        if (totalStopDistance == 0.0) {
                            progressStops.forEach(s -> {
                                if (s.getStopType().equalsIgnoreCase("D")) {
                                    s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(100 / (progressStops.size() - 1))));
                                } else s.setTotalDistanceInKmsPercentage(0.0);
                            });
                        } else
                            progressStops.forEach(s -> s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(s.getDistanceInKms() * 100 / totalStopDistance))));
                    } else if (totalDistance == 0.0) {
                        if (totalStopDistance == null || totalStopDistance == 0.0) {
                            progressStops.forEach(s -> {
                                if (s.getStopType().equalsIgnoreCase("D")) {
                                    s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(100 / (progressStops.size() - 1))));
                                } else s.setTotalDistanceInKmsPercentage(0.0);
                            });
                        }

                    }
                    if (progressStops.get(progressStops.size() - 1).isDelivered()) {
                        distancePercentage = 100.0;
                    }
                    progressBar.setStops(progressStops);
                    if(distancePercentage<=100) {
                        progressBar.setDistanceTravelledPercentage(Double.valueOf(df.format(distancePercentage)));
                    }else{
                        progressBar.setDistanceTravelledPercentage(Double.valueOf(df.format(100)));
                    }
                } else {
                    progressStops.forEach(s -> {
                        if (s.getStopType().equalsIgnoreCase("D")) {
                            s.setTotalDistanceInKmsPercentage(Double.parseDouble(df.format(100 / (progressStops.size() - 1))));
                        } else s.setTotalDistanceInKmsPercentage(0.0);
                    });
                    progressBar.setStops(progressStops);
                    if (progressStops.get(progressStops.size() - 1).isDelivered()) {
                        progressBar.setDistanceTravelledPercentage(100.0);
                    } else progressBar.setDistanceTravelledPercentage(0.0);
                }
                setProgressBar(progressBar);
            }
            setInTransit(setInTransitValue(loadId, shipmentStatusIntransitList));
        }
    }

    private String setInTransitValue(String loadID, List<ShipmentStatusDoc> shipmentStatusIntransitList) {
        if (shipmentStatusIntransitList != null) {
            Optional<ShipmentStatusDoc> shipmentStatusDocOptional = shipmentStatusIntransitList.stream().filter(shipmentStatusDoc -> shipmentStatusDoc.getLoadId().equalsIgnoreCase(loadID)).findAny();
            return shipmentStatusDocOptional.map(ShipmentStatusDoc::getStatusReasonCodeGid).orElse(null);
        }
        return null;
    }


    private Double convertTotalWeight(ShipmentV2 shipmentV2) {
        if (!Utility.isEmpty(shipmentV2.getLoadMeasure()) && !Utility.isEmpty(shipmentV2.getLoadMeasure().getTotalWeightUOM())) {
            switch (shipmentV2.getLoadMeasure().getTotalWeightUOM()) {
                case "LB":
                    return Utility.parseDoubleOrNull(shipmentV2.getLoadMeasure().getTotalWeight()) * 0.45D;
                case "MTON":
                    return Utility.parseDoubleOrNull(shipmentV2.getLoadMeasure().getTotalWeight()) * 1000D;
                default:
                case "KG":
                    return Utility.parseDoubleOrNull(shipmentV2.getLoadMeasure().getTotalWeight());
            }
        } else return 0D;
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
                            shipmentStopV2.getStopContent().getPallet().forEach(pallet -> {
                                intransitItemIdList.add(pallet.getPalletID());
                            });
                        }
                    } else {
                        if (shipmentStopV2.getStopContent().getPallet() != null) {
                            shipmentStopV2.getStopContent().getPallet().forEach(pallet -> {
                                intransitItemIdList.add(pallet.getPalletID());
                            });
                        }
                    }
                }
            });
        }
        intransitItemIdList.forEach(palletId -> {
            if (palletId != null && !palletId.isEmpty()) {
                shipment.getStops().stream().filter(shipmentStopV2 -> !shipmentStopV2.isDelivered() && shipmentStopV2.getStopType().equalsIgnoreCase("D")).forEach(shipmentStopV2 -> {
                    shipmentStopV2.getStopContent().getPallet().forEach(pallet -> {
                        if (pallet.getPalletID().equalsIgnoreCase(palletId.trim())) {
                            beanList.addAll(ShipmentTrackItemBeanV2.toShipmentTrackItemBeanV2(shipment, pallet,
                                    locationDocMap));
                        }
                    });
                });
            }
        });
        return beanList.stream().distinct().collect(Collectors.toList());
    }

    public ShipmentV2Bean shipmentV2ToBean(ShipmentV2 shipment){
        ShipmentV2Bean shipmentV2Bean=new ShipmentV2Bean();
        shipmentV2Bean.setLoadID(shipment.getLoadID());
        if (shipment.getLoadReferences() != null && !shipment.getLoadReferences().isEmpty()) {
            shipment.getLoadReferences().forEach(loadReference -> {
                if (loadReference.getLoadReferenceType() != null) {
                    if (loadReference.getLoadReferenceType().equals(KEY_TRUCK_NO)) {
                        shipmentV2Bean.setTruckNo(loadReference.getContent());
                    }
                }
            });
        }
        shipmentV2Bean.setShipper(shipment.getSource().getSiteName());
        shipmentV2Bean.setConsignee(shipment.getDestination().getSiteName());
        shipmentV2Bean.setStatus(shipment.getStatus());
        Double distancePendingInKms = shipment.getDistancePendingInKms();
        Double distanceTravelledInKms = shipment.getDistanceTravelledInKms();
        distancePendingInKms = (distancePendingInKms != null) ? distancePendingInKms : 0.0;
        distanceTravelledInKms = (distanceTravelledInKms != null) ? distanceTravelledInKms : 0.0;
        double totalDistance = distancePendingInKms + distanceTravelledInKms;
        if (totalDistance != 0.0) {
            double travelledDistancePercentage = Math.round((distanceTravelledInKms / totalDistance) * 100 * 10.0) / 10.0;
            if (travelledDistancePercentage != 0.0) {
                shipmentV2Bean.setTravelledDistancePercentage(travelledDistancePercentage + "%");
            }
            shipmentV2Bean.setTotalDistance(totalDistance);
        }
        if (distanceTravelledInKms != 0.0) {
            shipmentV2Bean.setDistanceTravelledInKms(distanceTravelledInKms);
        }
        return shipmentV2Bean;
    }
}


