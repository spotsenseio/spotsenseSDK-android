package com.app.spotsense.utils.spotSenseGeofencing

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.app.spotsense.LocationUpdatesService
import com.app.spotsense.interfaces.GetSpotSenseData
import com.app.spotsense.network.model.responseModel.GetBeaconRulesResponseModel
import com.app.spotsense.network.model.responseModel.GetRulesResponseModel
import com.app.spotsense.utils.SpotSenseConstants
import com.app.spotsense.utils.SpotSenseGlobalMethods
import com.google.android.gms.location.Geofence
import org.json.JSONObject

class SpotSenseGeo(
    private val context: Context,
    mSpotSenseGeofenceList: List<GetRulesResponseModel.RulesBean>,
    mBeaconRulesBeans: List<GetBeaconRulesResponseModel.BeaconRulesBean>,
    Notification_ID: Int,
    CHANNEL_ID: String,
    notificationMessage: String,
    smallIcon: Int,
    largeIcon: Int,
    showNotification: Boolean,
    getSpotSenseData: GetSpotSenseData?
) {
    private val mSpotSenseGeofenceList: List<GetRulesResponseModel.RulesBean>
    private val mBeaconRulesBeansList: List<GetBeaconRulesResponseModel.BeaconRulesBean>

    init {
        this.mSpotSenseGeofenceList = mSpotSenseGeofenceList
        mBeaconRulesBeansList = mBeaconRulesBeans
        SpotSenseConstants.CHANNEL_ID = CHANNEL_ID
        SpotSenseConstants.Notification_ID = Notification_ID
        SpotSenseConstants.NOTIFICATION_MESSAGE = notificationMessage
        SpotSenseConstants.smallIcon = smallIcon
        SpotSenseConstants.largeIcon = largeIcon
        SpotSenseConstants.showNotification = showNotification
        SpotSenseConstants.getSpotSenseData = getSpotSenseData
        mGeofenceList.clear()
        mBeaconeList.clear()
        populateGeofenceList()
        populateBeaconList()
    }

    private fun startMyServices() {
        if (isMyServiceRunning(LocationUpdatesService::class.java)) {
            val stopIntent = Intent(context, LocationUpdatesService::class.java)
            stopIntent.action = "stopfroeground"
            context.startService(stopIntent)
            return
        }
        Log.e("checkrunning", "" + isMyServiceRunning(LocationUpdatesService::class.java))
        Log.e("myversions", "" + Build.VERSION.SDK_INT)
        context.startForegroundService(Intent(context, LocationUpdatesService::class.java))
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.getClassName()) {
                return true
            }
        }
        return false
    }

    private fun populateBeaconList() {
        for (i in mBeaconRulesBeansList.indices) {
            val isEnabled: Boolean = mBeaconRulesBeansList[i].isEnabled
            Log.e(
                "notificatuionnambeacon",
                "" + mBeaconRulesBeansList[i].beaconName + "" + mBeaconRulesBeansList[i].isEnabled
            )
//            if (isEnabled) {
            mBeaconeList.add(mBeaconRulesBeansList[i])
            Log.e("TEST_CHECK", "populateBeaconList: $mBeaconeList")
//            }
        }
    }

    private fun populateGeofenceList() {
        for (i in mSpotSenseGeofenceList.indices) {
            val isEnabled: Boolean = mSpotSenseGeofenceList[i].isEnabled
            Log.e(
                "notificatuionnam",
                "" + mSpotSenseGeofenceList[i].geofenceName + "" + mSpotSenseGeofenceList[i].isEnabled
            );
//            if (isEnabled) {
            val jo = JSONObject()
            try {
                jo.put("id", mSpotSenseGeofenceList[i].id)
                jo.put("name", mSpotSenseGeofenceList[i].geofenceName)
            } catch (e: Exception) {
            }
            Log.e("notificatuionnam", "" + mSpotSenseGeofenceList[i].geofenceName)
            mGeofenceList.add(
                Geofence.Builder() // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(jo.toString()) // Set the circular region of this geofence.
                    .setCircularRegion(
                        mSpotSenseGeofenceList[i].geofence?.center?.lat!!,
                        mSpotSenseGeofenceList[i].geofence?.center?.longX!!,
                        mSpotSenseGeofenceList[i].geofence?.radiusSize!!.toFloat()
                    ) // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    //exdur  .setExpirationDuration(mSpotSenseGeofenceList.get(i).getExpiration_in_milliseconds())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE) // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                    ) // Create the geofence.
                    .build()
            )

            Log.e("TEST_CHECK", "populateGeofenceList: $mGeofenceList")
//            }
        }
        //     Log.e("geofencesize",""+mGeofenceList.size());
    }

    fun addSpotSenseGeofences() {
        if (mGeofenceList.size > 0) startMyServices()
        else if (mBeaconeList.size > 0) startMyServices()
        else Toast.makeText(context, "No Geofence and beacon Available", Toast.LENGTH_LONG).show()
    }

    companion object {
        var mGeofenceList: ArrayList<Geofence> = ArrayList()
        var mBeaconeList: ArrayList<GetBeaconRulesResponseModel.BeaconRulesBean> = ArrayList()
    }
}