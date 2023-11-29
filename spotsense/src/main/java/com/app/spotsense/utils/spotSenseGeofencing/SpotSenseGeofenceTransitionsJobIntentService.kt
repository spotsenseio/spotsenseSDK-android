/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.spotsense.utils.spotSenseGeofencing

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.core.app.JobIntentService
import com.app.spotsense.R
import com.app.spotsense.network.APIHandler
import com.app.spotsense.network.APIHandler.Companion.getApiServices
import com.app.spotsense.network.ResponseCallback
import com.app.spotsense.network.model.responseModel.GetRulesResponseModel
import com.app.spotsense.utils.SpotSenseConstants
import com.app.spotsense.utils.SpotSenseGlobalMethods
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense.Companion.clientID
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense.Companion.token
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

/**
 * Listener for geofence transition changes.
 *
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
class SpotSenseGeofenceTransitionsJobIntentService : JobIntentService() {
    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    protected override fun onHandleWork(intent: Intent) {
        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage: String =
                SpotSenseGeofenceErrorMessages.getErrorString(this, geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition: Int? = geofencingEvent?.getGeofenceTransition()

        // Test that the reported transition was of interest.
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {

                // Get the geofences that were triggered. A single event can trigger multiple geofences.
                val triggeringGeofences: MutableList<Geofence>? =
                    geofencingEvent.triggeringGeofences

                // Get the transition details as a String.
                val geofenceTransitionDetails =
                    triggeringGeofences?.let {
                        getGeofenceTransitionDetails(
                            geofenceTransition,
                            it
                        )
                    }


                //  ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
                if (triggeringGeofences != null) {
                    for (geofence in triggeringGeofences) {
                        try {
                            val jo = JSONObject(geofence.getRequestId())
                            //  triggeringGeofencesIdsList.add(jo.getString("name"));
                            doEnter(jo.getString("id"))
                            if (SpotSenseConstants.getSpotSenseData != null) {
                                SpotSenseConstants.getSpotSenseData!!.getSpotSenseGeofencingData(
                                    "" + getTransitionString(
                                        geofenceTransition
                                    ), jo.getString("name")
                                )
                            } else {
                                SpotSenseGlobalMethods.sendNotification(
                                    getApplicationContext(),
                                    SpotSenseConstants.Notification_ID,
                                    SpotSenseConstants.CHANNEL_ID,
                                    jo.getString("name"),
                                    SpotSenseConstants.NOTIFICATION_MESSAGE /*, SpotSenseConstants.sClass*/,
                                    SpotSenseConstants.smallIcon,
                                    SpotSenseConstants.largeIcon
                                )
                            }
                        } catch (e: JSONException) {
                            Log.e("jsonerrors", "true1" + e.getLocalizedMessage())
                            e.printStackTrace()
                        }

                        //04-01-2020                triggeringGeofencesIdsList.add(geofence.getRequestId());
                    }
                }
                if (geofenceTransitionDetails != null) {
                    Log.i(TAG, geofenceTransitionDetails)
                }
                Log.e("geofenceTransition", "" + geofenceTransitionDetails)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val triggeringGeofences: MutableList<Geofence>? =
                    geofencingEvent.getTriggeringGeofences()

                // Get the transition details as a String.
                //String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
                if (triggeringGeofences != null) {
                    for (geofence in triggeringGeofences) {
                        try {
                            val jo = JSONObject(geofence.getRequestId())
                            //  triggeringGeofencesIdsList.add(jo.getString("name"));
                            doExit(jo.getString("id"))
                            if (SpotSenseConstants.getSpotSenseData != null) {
                                SpotSenseConstants.getSpotSenseData!!.getSpotSenseGeofencingData(
                                    "" + getTransitionString(
                                        geofenceTransition
                                    ), jo.getString("name")
                                )
                            } else {
                                SpotSenseGlobalMethods.sendNotification(
                                    getApplicationContext(),
                                    SpotSenseConstants.Notification_ID,
                                    SpotSenseConstants.CHANNEL_ID,
                                    jo.getString("name"),
                                    SpotSenseConstants.NOTIFICATION_MESSAGE /*, SpotSenseConstants.sClass*/,
                                    SpotSenseConstants.smallIcon,
                                    SpotSenseConstants.largeIcon
                                )
                            }
                        } catch (e: JSONException) {
                            Log.e("jsonerrors", "true2" + e.getLocalizedMessage())
                            e.printStackTrace()
                        }
                    }
                }
            }

            else -> {
                // Log the error.
                Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition))
            }
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {
        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList = ArrayList<String>()
        for (geofence in triggeringGeofences) {
            try {
                val jo = JSONObject(geofence.getRequestId())
                triggeringGeofencesIdsList.add(jo.getString("name"))
            } catch (e: JSONException) {
                Log.e(TAG, "getGeofenceTransitionDetails: $e")
                e.printStackTrace()
            }
            //triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        val triggeringGeofencesIdsString: String = TextUtils.join(", ", triggeringGeofencesIdsList)
        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
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
        val requestCall: Call<GetRulesResponseModel?> =
            token?.let { getApiServices(it)?.enter("" + clientID, "" + ruleId, body) }!!
        apiRequest.commonAPI(getApplicationContext(), requestCall, object : ResponseCallback {
            override fun onSuccess(`object`: Any?, name: String?) {
                Log.e("apiresulenter", "success")
            }

            override fun onFail(`object`: Any?) {
                Log.e("apiresulrule", "failed")
            }
        }, SpotSenseConstants.DO_ENTER)
    }

    fun doExit(ruleId: String) {
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
            token?.let { getApiServices(it)?.exit("" + clientID, "" + ruleId, body) }!!
        apiRequest.commonAPI(getApplicationContext(), requestCall, object : ResponseCallback {
            override fun onSuccess(`object`: Any?, name: String?) {
                Log.e("apiresulrule", "success")
            }

            override fun onFail(`object`: Any?) {
                Log.e("apiresulExit", "failed")
            }
        }, SpotSenseConstants.DO_EXIT)
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private fun getTransitionString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> getString(R.string.geofence_transition_entered)
            Geofence.GEOFENCE_TRANSITION_EXIT -> getString(R.string.geofence_transition_exited)
            else -> getString(R.string.unknown_geofence_transition)
        }
    }

    companion object {
        private const val JOB_ID = 573
        private const val TAG = "SpotSenseGeofenTransIS"

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                JobIntentService.enqueueWork(
                    context,
                    SpotSenseGeofenceTransitionsJobIntentService::class.java,
                    JOB_ID,
                    intent
                )
            }
        }
    }
}