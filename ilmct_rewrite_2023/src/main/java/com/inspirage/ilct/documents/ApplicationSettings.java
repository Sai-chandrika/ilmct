package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.bean.ApplicationStatus;
import com.inspirage.ilct.dto.bean.rewrite.AlertsBean;
import com.inspirage.ilct.dto.bean.rewrite.SpecialServicesBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "applicationSettings")
public class ApplicationSettings extends BaseDoc {
    private ApplicationStatus applicationStatus;
    private String sessionTimeout;//1
    // tracking pi
    private String flightStatusAppId;
    private String flightStatusAppKey;
    private String flightStatusFlightStatus;
    private String flightStatusFlightTrack;
    // otm server
    private String ipAddress;
    private String xAuthKey;
    private String userName;
    private String password;
    // shipments details
    private Integer shipmentDelayedTime;
    private Integer shipmentShortDelayedTime;
    private Integer shipmentAvgDelayedTime;
    private Integer shipmentLongDelayedTime;
    private Integer shipmentPickupActivityTime;
    private Integer shipmentActivityTime;
    private Integer geoFenceDistance;
    private String trackTraceUrl;
    //otm push
    private String otmUrl;
    private String otmUsername;
    private String otmPassword;
    //purge data
    private Integer purgeShipmentStatus;
    private Integer purgeShipmentTransmissionLog;
    private Integer purgeShipments;
    private Integer purgeFlightShipmentStatus;
    // data type data configuration
    private Integer dataTrackShipmentStatus;
    private Integer dataTransmissionLog;
    private Integer dataShipments;
    private Integer dataFlightStatus;
    // shipment auto closure
    private Integer numberOfDays;
    private String autoSchedularTime;
    private String purgeDataSchedularTime;
    // create alert
    private List<AlertsBean> alerts = new LinkedList<>();
    // create special service
    private List<SpecialServicesBean> specialServices = new LinkedList<>();
    //shipment auto closure
    private Integer noOfDaysFromCurrentDays;
    private Boolean isActive = Boolean.TRUE;
    //Here Map Properties
    public String hereMapEtaUrl;
    public String hereMapEtaUrlForChina;
    public String hereMap_API_ID;
    public String hereMap_API_CODE;
    public String hereMap_API_ID_CHINA;
    public String hereMap_API_CODE_CHINA;
    public String hereMapWeatherURL;
    public String hereMapCre;
    private String localtionToStoreFile;
    private String hereMapsGlobalGeoCodeUrl = "https://geocoder.api.here.com/search/6.2/geocode.json?";
    private String hereMapsChinaGeoCodeUrl = "https://geocoder.hereapi.cn/6.2/geocode.json?";
}
