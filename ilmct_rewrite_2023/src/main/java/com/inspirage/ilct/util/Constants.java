package com.inspirage.ilct.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String ACTIVE_YES = "Y";
    public static final int PAGE_SIZE = 100;
    public static final int DEFAULT_DISTANCE = 100; // units - km
    public static final String TOPIC_SENDER = "/ilct_send";
    public static final String NOTIFICTION_SENDER = "/ilct_notification/";
    public static final String KEY_HAZARDOUS_TYPE = "HazardousType";
    public static final String INVALID_ROLE = "Invalid role entered";
    public static final String ROLE_SAVED = "Role saved successfully";
    public static final String ROLES_FOUND = "Roles found successfully";

    public static String USER_NOT_FOUND_MESSAGE = "User not found";
    public static String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials, Please Check";
    public static String ACCOUNT_ACTIVATE_MESSAGE = "Account not activated, please activate through OTP";
    public static String USER_PASSWORD_REQUIRED = "UserId and Password Required";
    public static final String SEPARATOR_DOMAIN_VS_LOADID = ".";
    public static final String GLOBAL_ERROR_MESSAGE = "Something went wrong!";
    public static final String SHIPMENT_PARSING_ERROR = "Cannot parse shipment xml";
    public static final String SHIPMENT_STATUS_TRANSLATION_ERROR = "Converted shipment status cannot be null";
    public static final String TRANSMISSION_ERROR = "Converted transmission cannot be null";
    public static final String ALERTS_SAVED = "Alerts saved successfully";
    public static final String ALERTS_UPDATED = "Alerts updated successfully";
    public static final String NO_ALERTS = "No alerts found with ids";
    public static final String APPLICATION_SETTINGS_SAVED = "Application settings saved successfully";
    public static final String APPLICATION_SETTINGS_UPDATED = "Application settings was updated successfully";
    public static final String APPLICATION_ID_ERROR = "No application properties was found with corresponding id";
    public static final String FILE_SAVED = "File saved successfully";
    public static final String FILE_INVALID = "File trying to save is invalid";
    public static final String FILE_ID_INVALID = "File id given is invalid";
    public static final String FILE_NOT_FOUND = "File cannot be found";
    public static final String FILE_NAME_INVALID = "File name is invalid";
    public static final String MONITOR_SAVED = "Monitor saved successfully";
    public static final String MONITOR_UPDATED = "Monitor updated successfully with id : ";
    public static final String MONITOR_DELETED = "Monitor deleted successfully with id : ";
    public static final String MONITOR_FOUND = "Monitor found with id : ";
    public static final String MONITOR_NOT_FOUND = "Monitor not found with id : ";
    public static final String NO_MONITOR = "Given monitor id is empty or null";
    public static final String USER_NOT_FOUND = "User cannot be found";
    public static final String SHIPMENT_NOT_FOUND = "Shipment cannot be found";
    public static final String SHIPMENT_FOUND = "Shipments found successfully";
    public static final String SHIPMENT_ERROR = "Shipment cannot be parsed";
    public static final String INVALID_REDIRECT_PAGE = "Provide valid search page name";
    public static final String INVALID_FILTER = "Invalid filter provided";
    public static final String LOAD_NOT_FOUND = "Load id cannot be found";
    public static final String INVALID_MONITOR_QUERY = "Corresponding query is invalid";
    public static final String FILE_LOCATION_NOT_FOUND = "Location for saving file is not valid or not found";
    public static final String ALERTS_DELETED = "Alerts was deleted successfully";
    public static final String SHIPMENT_CLOSING_ERROR = "Closing shipment failed";
    public static final String EVENTS_FOUND = "Events found successfully";
    public static final String SHIPMENT_STATUS_PARSING_ERROR = "Cannot parse shipment status xml";
    public static final String SHIPMENT_IOT_STATUS_ERROR = "Cannot save IOT shipment";
    // Http Status Codes (special scenarios)
    public static int SESSION_EXPIRED = 101;
    public static int TRANSACTION_FAILED = 102;
    public static int PRODUCT_NOT_DELIVERABLE = 103;
    public static int DEFAULT_ADDRESS_DELETE = 104;
    public static int USER_EMAIL_REQUIRED = 105;
    public static int STOCK_NOT_AVAILABLE = 106;
    public static int INVALID_TOKEN = 107;
    public static int ACCOUNT_LOCKED = 108;
    // public static final int DELAY_TIME=90;

    public static int DASHBOARD_YET_TO_COMPLETE_TIME = (60 * 3);// in minutes
    public static int DASHBOARD_SPEED_LESS_THEN = 20;// in kmph
    public static int DASHBOARD_FUEL_BELOW = 10;// in Liter

    // Shipment related keys
    public static final String KEY_DRIVER_CONTACT = "DriverPhone";
    public static final String KEY_TRUCK_NO = "VehicleNumber";

    public static final String KEY_TRAILER_NO = "TrailerNumber";
    public static final String KEY_DRIVER_NAME = "DriverName";
    public static final String KEY_LEG_TYPE = "LegType";
    public static final String KEY_CONSIGNEE = "CONSIGNEE";
    public static final String KEY_SHIPPER = "SHIPPER";
    public static final String KEY_BM = "BM";
    public static final String KEY_CN = "CN";
    public static final String KEY_FF = "FORWARDER";
    public static final String KEY_CHA = "CHA";
    public static final String KEY_TRAIN_NUMBER = "TrainNumber";
    public static final String KEY_WY = "WY";
    public static final String KEY_GPS_ENABLED = "GPSEnabled";
    public static final String VALUE_MULTI_LEG = "MULTI LEG";
    public static final String KEY_FLIGHT_NUMBER = "FlightNumber";
    public static final String VALUE_SINGLE_LEG = "SINGLE LEG";
    public static final String KEY_CONTAINER_ID = "CONTAINER_NUMBER";
    public static final String TM_TL = "TL";
    public static final String TM_LTL = "LTL";
    public static final String GROUP_AGE = "GROUPAGE";
    public static final String INTER_MODEL = "INTRMDL";
    public static final String TM_AIR = "AIR";
    public static final String TM_RAIL = "RAIL";
    public static final String TM_VESSEL = "VESSEL";
    public static final String TM_VESSEL_CO = "VESSEL-CO";
    public static final String KEY_SOURCE_AIRPORT = "SRC_AIRPORT_CODE";
    public static final String KEY_DESTINATION_AIRPORT = "DEST_AIRPORT_CODE";
    public static final String GLOBAL_ID = "GID";
    private static final String BOOKING_REF_NUM = "BN";

    public static final String EXPRESS = "EXPRESS";


    public static final String KEY_STATUS_COMPLETED = "CLOSED";
    public static final String KEY_STOP_DELIVERED = "D1";
    public static final String REGEX_EMAIL_VALIDATION = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static final String VALUE_EVENT_TYPE_IOT = "IOT";

    public static final String ACTUAL_ROUTE_COLOR = "#D8097D";

    public static final String GVIT_STATUS = "GVIT";
    //Last seen indication colors
    public static final String LASTSEEN_LABELCOLOR_RED = "RED";
    public static final String LASTSEEN_LABELCOLOR_GREEN = "GREEN";

    public static final String PARCEL_REF_NUM = "DHL_TRACKING_EVENT";
    public static final String KEY_STOP_GATE_IN = "X3";
    public static final String KEY_STOP_GATE_OUT = "AF";

    public static final String KEY_DELIVERY_STOP_ARRIVAL = "X1";
    public static final String TRACKINGPROVIDERETASTATUSES1 = "X6";
    public static final String TRACKINGPROVIDERETASTATUSES2 = "AG";

    public static final List<String> TRACKINGPROVIDERETASTATUSES = Arrays.asList("X6", "AG");

    public static final List<String> CARRIAGETYPES = Arrays.asList("PRE CARRIAGE", "MAIN CARRIAGE", "ON CARRIAGE");

    public static final String TM_TRUCK = "TRUCK";

    public static List<String> getGVITStatusList() {
        List<String> statusList = new ArrayList<String>();
        statusList.add(GVIT_STATUS);
        statusList.add("FENCE_IN");
        statusList.add("FENCE_OUT");
        return statusList;
    }

    public static List<String> getILMCTStatusList() {
        List<String> statusList = new ArrayList<String>();
        statusList.add(GVIT_STATUS);
        statusList.add(KEY_STOP_GATE_IN);
        statusList.add(KEY_STOP_GATE_OUT);
        statusList.add(KEY_DELIVERY_STOP_ARRIVAL);
        statusList.add(KEY_STOP_DELIVERED);
        statusList.add(KEY_STATUS_COMPLETED);
        statusList.add("X6");
        statusList.add("AG");
        return statusList;
    }



}
