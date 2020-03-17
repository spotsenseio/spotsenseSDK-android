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

package com.spotsense.utils.sportSenseGeofencing;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.spotsense.R;
import com.spotsense.data.network.APIHandler;
import com.spotsense.data.network.ResponseCallback;
import com.spotsense.data.network.model.responseModel.GetRulesResponseModel;
import com.spotsense.utils.SpotSenseConstants;
import com.spotsense.utils.SpotSenseGlobalMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;

import static com.spotsense.data.network.APIHandler.getApiServices;
import static com.spotsense.utils.SpotSenseConstants.DO_ENTER;
import static com.spotsense.utils.SpotSenseConstants.DO_EXIT;
import static com.spotsense.utils.sportSenseGeofencing.SpotSence.clientID;
import static com.spotsense.utils.sportSenseGeofencing.SpotSence.token;

/**
 * Listener for geofence transition changes.
 * <p>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class SpotSenseGeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String TAG = "SpotSenseGeofenTransIS";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, SpotSenseGeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }

    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = SpotSenseGeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);


          //  ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
            for (Geofence geofence : triggeringGeofences) {
                try {
                    JSONObject jo = new JSONObject(geofence.getRequestId());
                  //  triggeringGeofencesIdsList.add(jo.getString("name"));
                    doEnter(jo.getString("id"));
                    if (SpotSenseConstants.getSpotSenseData != null) {
                        SpotSenseConstants.getSpotSenseData.getSpotSenseGeofencingData("" + getTransitionString(geofenceTransition), jo.getString("name"));
                    } else {
                        SpotSenseGlobalMethods.sendNotification(getApplicationContext(), SpotSenseConstants.Notification_ID, SpotSenseConstants.CHANNEL_ID, jo.getString("name"), SpotSenseConstants.NOTIFICATION_MESSAGE/*, SpotSenseConstants.sClass*/, SpotSenseConstants.smallIcon, SpotSenseConstants.largeIcon);
                    }
                } catch (JSONException e) {
                    Log.e("jsonerrors", "true1" + e.getLocalizedMessage());

                    e.printStackTrace();
                }

//04-01-2020                triggeringGeofencesIdsList.add(geofence.getRequestId());
            }
                     Log.i(TAG, geofenceTransitionDetails);
            Log.e("geofenceTransition",""+ geofenceTransitionDetails);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            //String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

            for (Geofence geofence : triggeringGeofences) {
                try {
                    JSONObject jo = new JSONObject(geofence.getRequestId());
                  //  triggeringGeofencesIdsList.add(jo.getString("name"));
                    doExit(jo.getString("id"));
                    if (SpotSenseConstants.getSpotSenseData != null) {
                        SpotSenseConstants.getSpotSenseData.getSpotSenseGeofencingData("" + getTransitionString(geofenceTransition), jo.getString("name"));
                    } else {
                        SpotSenseGlobalMethods.sendNotification(getApplicationContext(), SpotSenseConstants.Notification_ID, SpotSenseConstants.CHANNEL_ID, jo.getString("name"), SpotSenseConstants.NOTIFICATION_MESSAGE/*, SpotSenseConstants.sClass*/, SpotSenseConstants.smallIcon, SpotSenseConstants.largeIcon);
                    }
                } catch (JSONException e) {
                    Log.e("jsonerrors", "true2" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(int geofenceTransition, List<
            Geofence> triggeringGeofences) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            try {
                JSONObject jo = new JSONObject(geofence.getRequestId());
                triggeringGeofencesIdsList.add(jo.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    void doEnter(final String ruleId) {
        APIHandler apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("userID", "" + clientID + "-" + SpotSence.deviceID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<GetRulesResponseModel> requestCall = getApiServices(token).enter("" + clientID, "" + ruleId, body);
        apiRequest.CommonAPI(getApplicationContext(), requestCall, new ResponseCallback() {
            @Override
            public void onSuccess(Object object, String name) {
                Log.e("apiresulenter", "success");
            }

            @Override
            public void onFail(Object object) {
                Log.e("apiresulrule", "failed");

            }
        }, DO_ENTER);

    }

    void doExit(String ruleId) {
        APIHandler apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("userID", "" + clientID + "-" + SpotSence.deviceID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<GetRulesResponseModel> requestCall = getApiServices(token).exit("" + clientID, "" + ruleId, body);
        apiRequest.CommonAPI(getApplicationContext(), requestCall, new ResponseCallback() {
            @Override
            public void onSuccess(Object object, String name) {
                Log.e("apiresulrule", "success");
            }

            @Override
            public void onFail(Object object) {
                Log.e("apiresulExit", "failed");

            }
        }, DO_EXIT);

    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
