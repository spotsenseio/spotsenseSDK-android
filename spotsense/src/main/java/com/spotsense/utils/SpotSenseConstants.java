package com.spotsense.utils;

import com.spotsense.interfaces.GetSpotSenseData;

public class SpotSenseConstants {
    public static final String STR_INTERNET_ALERT_TITLE = "Network Error!";
    public static final String STR_INTERNET_ALERT_MESSAGE = "Please check your Internet connection.";

    private static final String PACKAGE_NAME = "com.spotsensesdklib";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static String CHANNEL_ID = "SpotSense";
    public static int Notification_ID = 0;
    public static String NOTIFICATION_MESSAGE = "SpotSense MEssage";

    public static int smallIcon = 0;
    public static int largeIcon = 0;
    public static boolean showNotification = false;

    public static GetSpotSenseData getSpotSenseData;
    // API base URL
    public static String SPOT_SENCE_URL = "https://3o7us23hzl.execute-api.us-west-1.amazonaws.com/roor/";
    public static String GET_APP_INFO = "getAppInfo";
    public static String USER_EXITS = "userExist";
    public static String USER_CREATE = "userCreate";
    public static String GET_RULES = "getRules";
    public static String DO_EXIT = "doEnter";
    public static String DO_ENTER = "doExit";
    public static String DO_ENTER_BEACON = "doExit";
    public static String DO_EXIT_BEACON = "doExit";
    public static String GET_BEACON_RULES = "beaconRules";


}
