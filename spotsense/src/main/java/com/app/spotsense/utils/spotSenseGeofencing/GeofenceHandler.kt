package com.app.spotsense.utils.spotSenseGeofencing

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.app.spotsense.utils.SpotSenseConstants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class GeofenceHandler {
    private var mGeofencePendingIntent: PendingIntent? = null
    private var context: Context? = null
    var geoFenceClient: GeofencingClient? = null

    fun initGeofence(context: Context?) {
        this.context = context
        setGeoFenceData()
    }

    private fun setGeoFenceData() {
        geoFenceClient = LocationServices.getGeofencingClient(context!!)
        if (SpotSenseGeo.mGeofenceList?.size!! > 0) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) return
            geoFenceClient!!.addGeofences(geofencingRequest, geofencePendingIntent!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) updateGeofencesAdded(!geofencesAdded)
                    else {
                        val errorMessage =
                            SpotSenseGeofenceErrorMessages.getErrorString(context!!, task.exception)
                        Log.e("errorMessage", "" + errorMessage)
                    }
                }
        }
    }

    private fun removeSpotSenseGeoGeofences() {
        if (SpotSenseGeo.mGeofenceList!!.size > 0) {
            geoFenceClient!!.removeGeofences(geofencePendingIntent!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) updateGeofencesAdded(!geofencesAdded)
                    else {
                        val errorMessage =
                            context?.let {
                                SpotSenseGeofenceErrorMessages.getErrorString(
                                    it,
                                    task.exception
                                )
                            }
                        Log.e("errorMessage", "" + errorMessage)
                    }
                }
        }
    }

    fun onDestroys() {
        removeSpotSenseGeoGeofences()
    }

    private val geofencePendingIntent: PendingIntent?
        get() {
            // Reuse the PendingIntent if we already have it.
            if (mGeofencePendingIntent != null) {
                return mGeofencePendingIntent
            }
            val intent = Intent(context, SpotSenseGeofenceBroadcastReceiver::class.java)
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // addGeofences() and removeGeofences().
            mGeofencePendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            return mGeofencePendingIntent
        }
    private val geofencingRequest: GeofencingRequest
        get() {
            val builder = GeofencingRequest.Builder()

            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

            // Add the geofences to be monitored by geofencing service.
            SpotSenseGeo.mGeofenceList?.let { builder.addGeofences(it as List<Geofence>) }

            // Return a GeofencingRequest.
            return builder.build()
        }

    private fun updateGeofencesAdded(added: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, added)
            .apply()
    }

    private val geofencesAdded: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SpotSenseConstants.GEOFENCES_ADDED_KEY, false)

    companion object {
        private var geofenceHandler_instance: GeofenceHandler? = null
        val instance: GeofenceHandler?
            get() {
                if (geofenceHandler_instance == null) geofenceHandler_instance = GeofenceHandler()
                return geofenceHandler_instance
            }
    }
}