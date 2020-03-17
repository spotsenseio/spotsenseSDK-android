/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotsense;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.spotsense.utils.Utils;
import com.spotsense.utils.sportSenseGeofencing.GeofenceHandler;
import com.spotsense.utils.spotSenceBeacon.BeaconHandler;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service /*implements BeaconConsumer*/ {

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    // private PendingIntent mGeofencePendingIntent;
    private Context context;

    // private GeofencingClient mGeofencingClient;

    private BeaconHandler beaconHandler;
    private GeofenceHandler geofenceHandler;

    public LocationUpdatesService() {
    }


   /* OnCompleteListener completeListener = new OnCompleteListener() {
        @Override
        public void onComplete(Task task) {

            // mPendingGeofenceTask = PendingGeofenceTask.NONE;
            if (task.isSuccessful()) {
                updateGeofencesAdded(!getGeofencesAdded());
//                setButtonsEnabledState();

//                int messageId = getGeofencesAdded() ? R.string.geofences_added :
//                        R.string.geofences_removed;

                int messageId = R.string.geofences_added;

//                Toast.makeText(this, context.getString(messageId), Toast.LENGTH_SHORT).show();

                SpotSenseAlertDialogUtils.showToast(context, context.getString(messageId));
            } else {
                // Get the status code for the error and log it using a user-friendly message.
                String errorMessage = SpotSenseGeofenceErrorMessages.getErrorString(context, task.getException());
                Timber.e(errorMessage);
            }

        }
    };*/

    @Override
    public void onCreate() {


        context = getApplicationContext();


        Log.e("servicestatus", "true created");

        ///geo fencing

      /*  if (SpotSenseConstants.getSpotSenseData == null) {
            Log.e("servicestatus", "true nul");
            SpotSence spotSence = new SpotSence(getApplicationContext(), "lidba0lbjl2Zgh4fg9rUED0D7nDn2Igp", "9NeAKLbmbkFXpEFOk8q2D1rb-IduEZXNzUX-aaeoFYgsguqI_kIfP3gFsQtBpH18", new GetSpotSenseData() {
                @Override
                public void getSpotSenseGeofencingData(String GeofenceTransactions, ArrayList<String> GeofenceTransactionsRequestedId, String geofenceTransitionDetails) {

                }
            }, LocationUpdatesService.class);

            spotSence.start();
        } else {
            Log.e("servicestatus", "true notnull");
*/

        geofenceHandler = GeofenceHandler.getInstance();
        geofenceHandler.initGeofence(LocationUpdatesService.this);

   /*     mGeofencingClient = LocationServices.getGeofencingClient(context);
        if (mGeofenceList.size() > 0) {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(completeListener);
        }

*/
        ///geo fencing


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();
        beaconHandler = BeaconHandler.getInstance();
        beaconHandler.initBeaconManager(LocationUpdatesService.this);
        //setbeaconManagerData();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setSound(null, null);//created by sagar

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
        startForeground(NOTIFICATION_ID, getNotification());
        requestLocationUpdates();
        //beacone codes

        //beacone code end


        //}
    }

/*
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, false);
    }*/
    ///geo fence method


  /*  private GeofencingRequest getGeofencingRequest() {
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
*/

    /*   private PendingIntent getGeofencePendingIntent() {
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
   */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("servicestatus", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }
    ///geo fence method end


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals("stopfroeground")) {
            if (Build.VERSION.SDK_INT >= 26) {
                stopForeground(true);
            }
            stopSelf();
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(new Intent(context, LocationUpdatesService.class));
            } else {
                context.startService(new Intent(context, LocationUpdatesService.class));
            }
            // your start service code
        }
        Log.i("servicestatus", "Service onstartcommand");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);


        ///geofence code
        if (geofenceHandler == null) {
            geofenceHandler = GeofenceHandler.getInstance();
        }
        if (geofenceHandler.getGeoFenceClient() == null) {
            geofenceHandler.initGeofence(LocationUpdatesService.this);
        }
        if (beaconHandler == null) {
            beaconHandler = BeaconHandler.getInstance();
        }
        if (beaconHandler.getBeaconManager() == null) {
            beaconHandler = BeaconHandler.getInstance();
            beaconHandler.initBeaconManager(LocationUpdatesService.this);
        }

/*
        if (mGeofencingClient == null) {

            mGeofencingClient = LocationServices.getGeofencingClient(context);
            if (mGeofenceList.size() > 0) {
                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(completeListener);
            }
        }*/


        //end geofence code

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("servicestatus", "onBind");
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        startForeground(NOTIFICATION_ID, getNotification());
        //   stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("servicestatus", "onRebind");

        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        startForeground(NOTIFICATION_ID, getNotification());
        //   stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        Log.e("servicestatus", "onUnbind");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        Log.e("servicestatus", "onDestroy");

        geofenceHandler.onDestroys();
        // removeSpotSenseGeoGeofences();
        beaconHandler.onDestroys();
//        beaconManager.unbind(this);
        if (mServiceHandler != null) {
            mServiceHandler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = Utils.getLocationText(mLocation);

        // Extra to help us figure out if we afrrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
/*                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)*/
                //removedbysagar.setContentText(text)
                .setVibrate(new long[]{0L}).
                        setSound(null).setContentTitle("Spotsence Running")
                // .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.notification)
                //removedbysagar .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    /*   private void removeSpotSenseGeoGeofences() {
           if (mGeofenceList.size() > 0) {
               mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(completeListener);
           }
       }
   */
    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            //  mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}