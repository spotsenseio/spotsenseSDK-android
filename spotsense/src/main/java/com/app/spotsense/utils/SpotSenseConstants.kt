package com.app.spotsense.utils

import com.app.spotsense.interfaces.GetSpotSenseData

object SpotSenseConstants {
    const val STR_INTERNET_ALERT_TITLE = "Network Error!"
    const val STR_INTERNET_ALERT_MESSAGE = "Please check your Internet connection."
    private const val PACKAGE_NAME = "com.spotsensesdklib"
    const val GEOFENCES_ADDED_KEY = "$PACKAGE_NAME.GEOFENCES_ADDED_KEY"
    var CHANNEL_ID = "SpotSense"
    var Notification_ID = 0
    var NOTIFICATION_MESSAGE = "SpotSense MEssage"
    var smallIcon = 0
    var largeIcon = 0
    var showNotification = false
     var getSpotSenseData: GetSpotSenseData? = null

    // API base URL
    const val SPOT_SENSE_URL = "https://3o7us23hzl.execute-api.us-west-1.amazonaws.com/roor/"
    const val GET_APP_INFO = "getAppInfo"
    const val USER_EXITS = "userExist"
    const val USER_CREATE = "userCreate"
    const val GET_RULES = "getRules"
    const val DO_EXIT = "doEnter"
    const val DO_ENTER = "doExit"
    const val DO_ENTER_BEACON = "doExit"
    const val DO_EXIT_BEACON = "doExit"
    const val GET_BEACON_RULES = "beaconRules"
}