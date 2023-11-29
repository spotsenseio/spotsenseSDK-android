/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.spotsense

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.spotsense.utils.Utils
import com.app.spotsense.utils.spotSenseBeacon.BeaconHandler
import com.app.spotsense.utils.spotSenseGeofencing.GeofenceHandler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 *
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 *
 *
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
class LocationUpdatesService : Service() /*implements BeaconConsumer*/ {
    private val mBinder: IBinder = LocalBinder()

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var mChangingConfiguration = false
    private var mNotificationManager: NotificationManager? = null

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null

    // private PendingIntent mGeofencePendingIntent;
    private var context: Context? = null

    // private GeofencingClient mGeofencingClient;
    private var beaconHandler: BeaconHandler? = null
    private var geofenceHandler: GeofenceHandler? = null

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

    override fun onCreate() {
        context = applicationContext
        Log.e("servicestatus", "true created")

        ///geo fencing

        /*  if (SpotSenseConstants.getSpotSenseData == null) {
            Log.e("servicestatus", "true nul");
            SpotSense spotSense = new SpotSense(getApplicationContext(), "lidba0lbjl2Zgh4fg9rUED0D7nDn2Igp", "9NeAKLbmbkFXpEFOk8q2D1rb-IduEZXNzUX-aaeoFYgsguqI_kIfP3gFsQtBpH18", new GetSpotSenseData() {
                @Override
                public void getSpotSenseGeofencingData(String GeofenceTransactions, ArrayList<String> GeofenceTransactionsRequestedId, String geofenceTransitionDetails) {

                }
            }, LocationUpdatesService.class);

            spotSense.start();
        } else {
            Log.e("servicestatus", "true notnull");
*/
        geofenceHandler = GeofenceHandler.instance
        geofenceHandler?.initGeofence(this@LocationUpdatesService)

        /*     mGeofencingClient = LocationServices.getGeofencingClient(context);
        if (mGeofenceList.size() > 0) {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(completeListener);
        }
*/
        ///geo fencing
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { onNewLocation(it) }
            }
        }
        createLocationRequest()
        lastLocation
        beaconHandler = BeaconHandler.instance
        beaconHandler?.initBeaconManager(this@LocationUpdatesService)
        //setbeaconManagerData();
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        val name: CharSequence = getString(R.string.app_name)
        // Create the channel for the notification
        val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.setSound(null, null) //created by sagar

        // Set the Notification Channel for the Notification Manager.
        mNotificationManager!!.createNotificationChannel(mChannel)
        startForeground(NOTIFICATION_ID, notification)
        requestLocationUpdates()
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
    override fun onTaskRemoved(rootIntent: Intent) {
        Log.e("servicestatus", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    ///geo fence method end
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action != null && intent.getAction() == "stopfroeground") {
            stopForeground(true)
            stopSelf()
            context!!.startForegroundService(
                Intent(
                    context,
                    LocationUpdatesService::class.java
                )
            )
            // your start service code
        }
        Log.i("servicestatus", "Service onstartcommand")
        val startedFromNotification: Boolean = intent.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )


        ///geofence code
        if (geofenceHandler == null) {
            geofenceHandler = GeofenceHandler.instance
        }
        if (geofenceHandler?.geoFenceClient == null) {
            geofenceHandler?.initGeofence(this@LocationUpdatesService)
        }
        if (beaconHandler == null) {
            beaconHandler = BeaconHandler.instance
        }
        if (beaconHandler?.getBeaconManager() == null) {
            beaconHandler = BeaconHandler.instance
            beaconHandler?.initBeaconManager(this@LocationUpdatesService)
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
            removeLocationUpdates()
            stopSelf()
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("servicestatus", "onBind")
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()")
        startForeground(NOTIFICATION_ID, notification)
        //   stopForeground(true);
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        Log.e("servicestatus", "onRebind")

        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()")
        startForeground(NOTIFICATION_ID, notification)
        //   stopForeground(true);
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i(TAG, "Last client unbound from service")
        Log.e("servicestatus", "onUnbind")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service")
            startForeground(NOTIFICATION_ID, notification)
        }
        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        Log.e("servicestatus", "onDestroy")
        geofenceHandler?.onDestroys()
        // removeSpotSenseGeoGeofences();
        beaconHandler?.onDestroys()
        //        beaconManager.unbind(this);
        if (mServiceHandler != null) {
            mServiceHandler!!.removeCallbacksAndMessages(null)
        }
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        Utils.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback!!, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mLocationCallback?.let { mFusedLocationClient?.removeLocationUpdates(it) }
            Utils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private val notification: Notification
        /**
         * Returns the [NotificationCompat] used as part of the foreground service.
         */
        get() {
            val intent = Intent(this, LocationUpdatesService::class.java)
            val text: CharSequence = Utils.getLocationText(mLocation)

            // Extra to help us figure out if we afrrived in onStartCommand via the notification or not.
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

            // The PendingIntent that leads to a call to onStartCommand() in this service.
            val servicePendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // The PendingIntent to launch activity.
            val activityPendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(), PendingIntent.FLAG_IMMUTABLE
            )
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this) /*                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)*/
                //removedbysagar.setContentText(text)
                .setVibrate(longArrayOf(0L)).setSound(null)
                .setContentTitle("spotSense Running") // .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.notification) //removedbysagar .setTicker(text)
                .setWhen(System.currentTimeMillis())

            // Set the Channel ID for Android O.
            builder.setChannelId(CHANNEL_ID) // Channel ID
            return builder.build()
        }
    private val lastLocation: Unit
        /*   private void removeSpotSenseGeoGeofences() {
           if (mGeofenceList.size() > 0) {
               mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(completeListener);
           }
       }
   */@SuppressLint("MissingPermission") get() {
            try {
                mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.w(TAG, "Failed to get location.")
                    }
                }
            } catch (unlikely: SecurityException) {
                Log.e(TAG, "Lost location permission.$unlikely")
            }
        }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        mLocation = location

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            //  mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager: ActivityManager = context.getSystemService(
            ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.getClassName()) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private const val PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice"
        private val TAG = LocationUpdatesService::class.java.simpleName

        /**
         * The name of the channel for notifications.
         */
        private const val CHANNEL_ID = "channel_01"
        const val ACTION_BROADCAST = PACKAGE_NAME + ".broadcast"
        const val EXTRA_LOCATION = PACKAGE_NAME + ".location"
        private const val EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
                ".started_from_notification"

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The identifier for the notification displayed for the foreground service.
         */
        private const val NOTIFICATION_ID = 12345678
    }
}