package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.ApplicationSettings;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class PropertiesService {

    //  @Value("${session-timeout}")
    private final Integer SESSION_TIMEOUT;

    //  @Value("${here-maps.app-id}")
    private final String hearMap_API_ID;

    // @Value("${here-maps.app-code}")
    private final String hereMap_API_CODE;

    //  @Value("${here-maps.app-id-china}")
    private final String hearMap_API_ID_CHINA;

    //  @Value("${here-maps.app-code-china}")
    private final String hereMap_API_CODE_CHINA;


    //   @Value("${routing-urls.here-maps}")
    private final String hereMapEtaUrl;

    //   @Value("${routing-urls.here-maps-cre}")
    private final String hereMapCREEtaUrl;

    //   @Value("${routing-urls.here-maps-weather}")
    private final String hereMap_Weather_URL;


    //   @Value("${shipment-delayed-time}")
    private final Integer shipmentDelayedTime;


    //   @Value("${tracking-api.flightstats-appId}")
    private final String flightstatsAppId;

    //    @Value("${tracking-api.flightstats-appKey}")
    private final String flightstatsAppKey;

    //   @Value("${tracking-api.flightstats-flight-status}")
    private final String flightstatsFlightStatusURL;

    //   @Value("${tracking-api.flightstats-flight-track}")
    private final String flightstatsFlightTrackURL;


    //   @Value("${otm-server.ip-address}")
    private final String ALLOWED_IP;

    //   @Value("${otm-server.x-auth-key}")
    private final String X_API_KEY_VALUE;

    //   @Value("${otm-server.user-name}")
    private final String USER_NAME;

    //   @Value("${otm-server.password}")
    private final String PASSWORD;


    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    //   @Value("${identifiers.shimpent-status-identifier}")
    private String shipmentStatusIdentifier;


    //   @Value("${purge-data.shipment-status}")
    private final Integer purgeShipmentStatus;
    //    @Value("${purge-data.shipment_transmission_log}")
    private final Integer purgeShipmentTransmissionLogs;
    //    @Value("${purge-data.shipments}")
    private final Integer purgeShipments;
    //    @Value("${purge-data.flight_shipment_status}")
    private final Integer purgeFlightShipmentStatus;


    //@Value("${map.latitude}")
    private Float latitude;
    //@Value("${map.longitude}")
    private Float longitude;
    //@Value("${map.zoom-level}")
    private Integer zoomLevel;

    //@Value("${enable-encryption}")
    private Boolean enableSecurity=Boolean.TRUE;

    //   @Value("${geo-fence-distance}")
    private final Integer geoFenceDistance;

    //   @Value("${shipment-short-delayed-time}")
    private final Integer shipmentShortDelayedTime;

    //    @Value("${shipment-avg-delayed-time}")
    private final Integer shipmentAvgDelayedTime;

    //    @Value("${shipment-long-delayed-time}")
    private final Integer shipmentLongDelayedTime;

    //   @Value("${track-trace-url}")
    private final String trackTraceUrl;

    //   @Value("${otm-push.url}")
    private final String otmPushUrl;

    //   @Value("${otm-push.user-name}")
    private final String otmPushUserName;

    //   @Value("${otm-push.password}")
    private final String otmPushPassword;

    //   @Value("${shipment-pickup-activity-time}")
    private final Long shipmentPickupActivityTime;

    //   @Value("${shipment-activity-time}")
    private final Long shipmentActivityTime;

    //   @Value("${routing-urls.here-maps-china}")
    private final String hereMapEtaUrlForChina;

    //@Value("${map.china-latitude}")
    private Float chinaLatitude;
    //@Value("${map.china-longitude}")
    private Float chinaLongitude;
    @Autowired
    ApplicationSettingsService applicationSettingsService;

    public PropertiesService(ApplicationSettingsService applicationSettingsService) throws ApplicationSettingsNotFoundException {
        this.applicationSettingsService = applicationSettingsService;
        ApplicationSettings applicationSettings = applicationSettingsService.getApplicationSetting();
        this.SESSION_TIMEOUT = applicationSettings.getSessionTimeout() != null ? Integer.valueOf(applicationSettings.getSessionTimeout()) : null;
        this.hearMap_API_ID = applicationSettings.getHereMap_API_ID();
        this.hereMap_API_CODE = applicationSettings.getHereMap_API_CODE();
        this.hearMap_API_ID_CHINA = applicationSettings.getHereMap_API_ID_CHINA();
        this.hereMap_API_CODE_CHINA = applicationSettings.getHereMap_API_CODE_CHINA();
        this.hereMapEtaUrl = applicationSettings.getHereMapEtaUrl();
        this.hereMapCREEtaUrl = applicationSettings.getHereMapCre();
        this.hereMap_Weather_URL = applicationSettings.getHereMapWeatherURL();
        this.shipmentDelayedTime = applicationSettings.getShipmentDelayedTime();
        this.flightstatsAppId = applicationSettings.getFlightStatusAppId();
        this.flightstatsAppKey = applicationSettings.getFlightStatusAppKey();
        this.flightstatsFlightStatusURL = applicationSettings.getFlightStatusFlightStatus();
        this.flightstatsFlightTrackURL = applicationSettings.getFlightStatusFlightTrack();
        this.ALLOWED_IP = applicationSettings.getIpAddress();
        this.X_API_KEY_VALUE = applicationSettings.getXAuthKey();
        this.USER_NAME = applicationSettings.getUserName();
        this.PASSWORD = applicationSettings.getPassword();
        this.purgeShipmentStatus = applicationSettings.getPurgeShipmentStatus();
        this.purgeShipmentTransmissionLogs = applicationSettings.getPurgeShipmentTransmissionLog();
        this.purgeShipments = applicationSettings.getPurgeShipments();
        this.purgeFlightShipmentStatus = applicationSettings.getPurgeFlightShipmentStatus();
        this.geoFenceDistance = applicationSettings.getGeoFenceDistance();
        this.shipmentShortDelayedTime = applicationSettings.getShipmentShortDelayedTime();
        this.shipmentAvgDelayedTime = applicationSettings.getShipmentAvgDelayedTime();
        this.shipmentLongDelayedTime = applicationSettings.getShipmentLongDelayedTime();
        this.trackTraceUrl = applicationSettings.getTrackTraceUrl();
        this.otmPushUrl = applicationSettings.getOtmUrl();
        this.otmPushUserName = applicationSettings.getOtmUsername();
        this.otmPushPassword = applicationSettings.getOtmPassword();
        this.shipmentPickupActivityTime = Long.valueOf(applicationSettings.getShipmentPickupActivityTime());
        this.shipmentActivityTime = Long.valueOf(applicationSettings.getShipmentActivityTime());
        this.hereMapEtaUrlForChina = applicationSettings.getHereMapEtaUrlForChina();
    }

        public Map<String, Object> getCredentialsAndConfigurations(User user) {
            Map<String, Object> map = new HashMap<>();
            map.put("appId", this.hearMap_API_ID);
            map.put("appCode", this.hereMap_API_CODE);
            map.put("latitude", this.getLatitude());
            map.put("longitude", this.getLongitude());
            map.put("zoomLevel", this.getZoomLevel());
            map.put("chinaAppId", this.hearMap_API_ID_CHINA);
            map.put("chinaAppCode", this.hereMap_API_CODE_CHINA);
            map.put("chinaLatitude", this.chinaLatitude);
            map.put("chinaLongitude", this.chinaLongitude);
            map.put("shortLeadTime", this.shipmentShortDelayedTime);
            map.put("avgLeadTime", this.shipmentAvgDelayedTime);
            map.put("longLeadTime", this.shipmentLongDelayedTime);
            map.put("delayedTime", this.shipmentDelayedTime);
            if (user != null) {
                if ((user.getBaseMapCountry() != null) && (user.getBaseMapCountry().equalsIgnoreCase("CH") || user.getBaseMapCountry().equalsIgnoreCase("CHN"))) {
                    map.put("defaultAppId", this.hearMap_API_ID_CHINA);
                    map.put("defaultAppCode", this.hereMap_API_CODE_CHINA);
                    map.put("currentMapApi", "CHN");
                } else {
                    map.put("defaultAppId", this.hearMap_API_ID);
                    map.put("defaultAppCode", this.hereMap_API_CODE);
                    map.put("currentMapApi", "GLOBAL");
                }
            }
            return map;
        }
    }


