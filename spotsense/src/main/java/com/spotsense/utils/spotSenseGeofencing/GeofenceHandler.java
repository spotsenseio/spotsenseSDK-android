package com.spotsense.utils.spotSenseGeofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.spotsense.utils.SpotSenseConstants;

import static com.spotsense.utils.spotSenseGeofencing.SpotSenseGeo.mGeofenceList;

public class GeofenceHandler {


    private PendingIntent mGeofencePendingIntent;
    private Context context;

    private GeofencingClient mGeofencingClient;


    private static GeofenceHandler geofenceHandler_instance = null;

    public static GeofenceHandler getInstance() {
        if (geofenceHandler_instance == null)
            geofenceHandler_instance = new GeofenceHandler();

        return geofenceHandler_instance;
    }


    public void initGeofence(Context context) {
        this.context = context;
        setGeoFenceData();
    }


    public GeofencingClient getGeoFenceClient() {
        return mGeofencingClient;
    }

    public void setGeoFenceData() {
        mGeofencingClient = LocationServices.getGeofencingClient(context);
        if (mGeofenceList.size() > 0) {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(completeListener);
        }

    }

    private void removeSpotSenseGeoGeofences() {
        if (mGeofenceList.size() > 0) {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(completeListener);
        }
    }

    public void onDestroys() {
        removeSpotSenseGeoGeofences();

    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, SpotSenseGeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, false);
    }


    OnCompleteListener completeListener = new OnCompleteListener() {
        @Override
        public void onComplete(Task task) {

            if (task.isSuccessful()) {
                updateGeofencesAdded(!getGeofencesAdded());
            } else {
                String errorMessage = SpotSenseGeofenceErrorMessages.getErrorString(context, task.getException());
                Log.e("errorMessage",""+errorMessage);
            }

        }
    };


}
