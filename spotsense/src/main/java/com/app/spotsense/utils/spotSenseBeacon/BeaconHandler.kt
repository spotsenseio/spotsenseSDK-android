package com.app.spotsense.utils.spotSenseBeacon

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.app.spotsense.network.APIHandler
import com.app.spotsense.network.APIHandler.Companion.getApiServices
import com.app.spotsense.network.ResponseCallback
import com.app.spotsense.network.model.responseModel.GetRulesResponseModel
import com.app.spotsense.utils.SpotSenseConstants
import com.app.spotsense.utils.SpotSenseConstants.DO_ENTER_BEACON
import com.app.spotsense.utils.SpotSenseConstants.DO_EXIT_BEACON
import com.app.spotsense.utils.SpotSenseGlobalMethods
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense.Companion.clientID
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense.Companion.token
import com.app.spotsense.utils.spotSenseGeofencing.SpotSenseGeo.Companion.mBeaconeList
import okhttp3.MediaType
import okhttp3.RequestBody
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

class BeaconHandler {
    private var beaconManager: BeaconManager? = null
    var context: Context? = null
    fun getBeaconManager(): BeaconManager? {
        return beaconManager
    }

    fun initBeaconManager(context: Context?) {
        this.context = context
        setBeaconManagerData(context)
    }

    private var beaconConsumer: BeaconConsumer = object : BeaconConsumer {
        override fun onBeaconServiceConnect() {
            beaconManager?.removeAllMonitorNotifiers();
            try {
                if (mBeaconeList.size > 0) {
                    for (i in 0 until mBeaconeList.size) {
                        try {
                            val stringId = mBeaconeList[i].namespace!!.lowercase()
                            val namespaceId = Identifier.parse(stringId)
//                            val namespaceId = Identifier.parse("0a82ed1e-5043-49f9-9b77-c0765a17c9ab")
                            val region = Region(
                                mBeaconeList[i].beaconName,
                                namespaceId,
                                null,
                                null
                            )
                            beaconManager?.startMonitoring(region)
//                            beaconManager?.addMonitorNotifier(mn)
                        } catch (e: Exception) {
                            Log.e(TAG, "onBeaconServiceConnect: $e")
                            Toast.makeText(
                                context,
                                "namespace not valid please enter valid namespace data otherwise beacon not work",
                                Toast.LENGTH_LONG
                            ).show()
                            // onDestroys();
                            continue
                        }
                    }
                    beaconManager?.addMonitorNotifier(mn)
                    beaconManager?.addRangeNotifier { beacons, region ->
                        Log.e("TEST_CHECK", "onBeaconServiceConnect: $beacons")
                    }
                    Log.e(TAG, "onBeaconServiceConnect: ")
                    Log.e("mbeaconstatus", "greaterthen0")
                } else {
                    Log.e("mbeaconstatus", "notgreaterthen0")
                }
            } catch (e: Exception) {
                Log.e("errorrererer", "I have just switched from seeing/not seeing beacons: ")
                e.printStackTrace()
            }
        }

        override fun getApplicationContext(): Context {
            return context!!
        }

        override fun unbindService(serviceConnection: ServiceConnection) {
            context!!.unbindService(serviceConnection)
        }

        override fun bindService(
            intent: Intent,
            serviceConnection: ServiceConnection,
            i: Int
        ): Boolean {
            return context!!.bindService(intent, serviceConnection, i)
        }
    }

    private fun setBeaconManagerData(context: Context?) {
        //deviceToBeaconMap.clear();
        beaconManager = BeaconManager.getInstanceForApplication(context!!)
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
//        beaconManager!!.beaconParsers.addAll(beaconManager!!.beaconParsers)
        BeaconManager.setDebug(true)
        //        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager!!.foregroundBetweenScanPeriod = 25000L
        beaconManager!!.foregroundScanPeriod = 10000L
        beaconManager!!.backgroundScanPeriod = 10000
        beaconManager!!.backgroundBetweenScanPeriod = 10000L
        beaconManager!!.backgroundMode = false
        //        beaconManager.setBackgroundScanPeriod(10000L);
        //      beaconManager.setBackgroundBetweenScanPeriod(25000L);
        BeaconManager.setAndroidLScanningDisabled(true)
        beaconManager?.bindInternal(beaconConsumer)
    }

    //beacon codes
    var mn: MonitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(region: Region) {
            Log.e("TEST_CHECK", "didEnterRegion: ${region.uniqueId}")
            var beaconName = ""
            try {
                val jo = JSONObject()
                jo.put("name",region.uniqueId)
                jo.put("id",region.id1)
                beaconName = jo.getString("name")
                if (deviceToBeaconMap.containsKey(jo.getString("id"))) {
                    deviceToBeaconMap[jo.getString("id")] = System.currentTimeMillis()
                } else {
                    deviceToBeaconMap[jo.getString("id")] = System.currentTimeMillis()
                    doEnter(jo.getString("id"))
                    if (SpotSenseConstants.getSpotSenseData != null) {
                        SpotSenseConstants.getSpotSenseData?.getSpotSenseBeaconData(
                            "Enter",
                            "Enterd: $beaconName using Beacon"
                        )
                    } else {
                        SpotSenseGlobalMethods.sendNotification(
                            context!!,
                            SpotSenseConstants.Notification_ID,
                            SpotSenseConstants.CHANNEL_ID,
                            "you entered in" + beaconName + "using beacon",
                            SpotSenseConstants.NOTIFICATION_MESSAGE /*, SpotSenseConstants.sClass*/,
                            SpotSenseConstants.smallIcon,
                            SpotSenseConstants.largeIcon
                        )
                    }
                    Log.e(
                        TAG + "enter",
                        "I just saw an beacon for the first time!" + "asasasuuui" + "getUniqueId" + region.uniqueId + "getId1" + region.id1 + "getId2" + region.id2 + "getId3" + region.id3 + "desk" + region.describeContents()
                    )
                }
            } catch (e: JSONException) {
                Log.e(TAG, "didEnterRegion: $e")
                e.printStackTrace()
            }
        }

        override fun didExitRegion(region: Region) {
            Log.e("TEST_CHECK", "didExitRegion: ")
            Log.e("exitbeconwithdelay", "calledsasasa")
            var beaconName = ""
            try {
                Log.e("exitbeconwithdelay", "called11212")

                val jo = mBeaconeList.find { it.namespace!!.lowercase() == region.id1.toString().lowercase() }
                beaconName = jo!!.beaconName.toString()
                //doExit(jo.getString("id"));
                exitBeaconWithdelay(jo!!.namespace.toString().lowercase(), region)
                Log.e("exitbeconwithdelay", "called434343")
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e(TAG, "didExitRegion: $e")
            }
        }

        override fun didDetermineStateForRegion(state: Int, region: Region) {
            Log.e("TEST_CHECK", "didDetermineStateForRegion: ")
            Log.e(TAG, "I have just switched from seeing/not seeing beacons: $state")
        }
    }

    fun exitBeaconWithdelay(id: String, region: Region) {
        val handler = Handler()
        Log.e("exitbeconwithdelay", "called")
        handler.postDelayed({
            Log.e("exitbeconwithdelay", "1" + deviceToBeaconMap.containsKey(id))
            if (deviceToBeaconMap.containsKey(id)) {
                Log.e("exitbeconwithdelay", "2")
                val time = System.currentTimeMillis()
                Log.e("times", "" + time)
                Log.e("times2", "" + deviceToBeaconMap[id])
                Log.e("times3", "" + (time - deviceToBeaconMap[id]!!))
                if (time - deviceToBeaconMap[id]!! >= 15000) {
                    Log.e("exitbeconwithdelay", "3")
                    deviceToBeaconMap.remove(id)
                    exitLogic(region)
                    Log.e("exitbeconwithdelay", "4")
                }
                Log.e("exitbeconwithdelay", "5")
            }
            Log.e("exitbeconwithdelay", "6")
            //Do something after 100ms
        }, 10000)
    }

    private fun exitLogic(region: Region) {
        var beaconName = ""
        try {
            val jo = mBeaconeList.find { it.namespace!!.lowercase() == region.id1.toString().lowercase() }
            beaconName = region.uniqueId.toString()
            doExit(jo!!.id.toString())
        } catch (e: JSONException) {
            Log.e(TAG, "exitLogic: $e")
            e.printStackTrace()
        }
        if (SpotSenseConstants.getSpotSenseData != null) {
            SpotSenseConstants.getSpotSenseData?.getSpotSenseBeaconData(
                "Exit",
                "Exited: $beaconName using Beacon"
            )
        } else {
            SpotSenseGlobalMethods.sendNotification(
                context!!,
                SpotSenseConstants.Notification_ID,
                SpotSenseConstants.CHANNEL_ID,
                "you exit from" + beaconName + "using beacon",
                SpotSenseConstants.NOTIFICATION_MESSAGE /*, SpotSenseConstants.sClass*/,
                SpotSenseConstants.smallIcon,
                SpotSenseConstants.largeIcon
            )
        }
        Log.e(
            TAG + "exit",
            "I no longer see an beacon" + region.uniqueId + "sasa" + region.uniqueId + "Asasasasa" + region.id1 + "asasasa" + region.id2 + "Asasasa" + region.id3
        )
    }

    fun doEnter(ruleId: String) {
        val apiRequest = APIHandler()
        val jo = JSONObject()
        try {
            jo.put("userID", "" + clientID + "-" + SpotSense.deviceID)
        } catch (e: JSONException) {
            Log.e(TAG, "doEnter: $e")
            e.printStackTrace()
        }
        val body: RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toString())
        val requestCall: Call<GetRulesResponseModel?> = token?.let { getApiServices(it)?.beaconEnter("" + clientID, "" + ruleId, body) }!!
        apiRequest.commonAPI(context, requestCall, object : ResponseCallback {
            override fun onSuccess(`object`: Any?, name: String?) {
                Log.e("TEST_CHECK", "success")
            }

            override fun onFail(`object`: Any?) {
                Log.e("TEST_CHECK", "failed")
            }
        }, DO_ENTER_BEACON)
    }

    private fun doExit(ruleId: String) {
        val apiRequest = APIHandler()
        val jo = JSONObject()
        try {
            jo.put("userID", "" + clientID + "-" + SpotSense.deviceID)
        } catch (e: JSONException) {
            Log.e(TAG, "doExit: $e")
            e.printStackTrace()
        }
        val body: RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toString())
        val requestCall: Call<GetRulesResponseModel?> =
            token?.let { getApiServices(it)?.beaconExit("" + clientID, "" + ruleId, body) }!!
        apiRequest.commonAPI(context, requestCall, object : ResponseCallback {
            override fun onSuccess(`object`: Any?, name: String?) {
                Log.e("apiresulrule", "success")
            }

            override fun onFail(`object`: Any?) {
                Log.e("apiresulExit", "failed")
            }
        }, DO_EXIT_BEACON)
    }

    fun onDestroys() {
        //deviceToBeaconMap.clear();
        //beaconManager.removeAllMonitorNotifiers();
        beaconManager?.unbindInternal(beaconConsumer)
    }

    companion object {
        private val TAG = BeaconHandler::class.java.simpleName
        var deviceToBeaconMap: MutableMap<String, Long> = HashMap()
        private var beaconHandler_instance: BeaconHandler? = null
        val instance: BeaconHandler?
            get() {
                if (beaconHandler_instance == null) {
                    beaconHandler_instance = BeaconHandler()
                }
                return beaconHandler_instance
            }
    }
}