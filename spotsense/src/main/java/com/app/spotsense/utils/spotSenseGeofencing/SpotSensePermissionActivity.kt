package com.app.spotsense.utils.spotSenseGeofencing

import android.Manifest.permission
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.security.Permissions

class SpotSensePermissionActivity : Activity() {
    private var btAdapter: BluetoothAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }

    private fun requestPermission() {
        val mPermissions = ArrayList<String>()
        mPermissions.add(permission.ACCESS_FINE_LOCATION)
        mPermissions.add(permission.BLUETOOTH_ADMIN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) mPermissions.add(permission.BLUETOOTH_SCAN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) mPermissions.add(permission.BLUETOOTH_CONNECT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) mPermissions.add(permission.POST_NOTIFICATIONS)
        ActivityCompat.requestPermissions(
            this,
            mPermissions.toTypedArray(),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun enabledLocation() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) { setupScanner() }
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this@SpotSensePermissionActivity,
                        REQUEST_LOCATION
                    )
                } catch (sendEx: SendIntentException) {
// Ignore the error.
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted) {
                    val manager = getSystemService(LOCATION_SERVICE) as LocationManager
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        enabledLocation()
                    } else {
                        setupScanner()
                    }
                } else {
                    Log.e("isgpsproviderenabled11", "true")
                    launchEvent(false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            setupScanner()
        } else if (requestCode == REQUEST_LOCATION && resultCode == RESULT_CANCELED) {
            launchEvent(false)
        } else if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                launchEvent(true)
            } else {
                setupScanner()
            }
        }
    }

    val eventName = "your.package.goes.here.EVENT"
    private fun launchEvent(permissionStatus: Boolean) {
        val eventIntent = Intent(eventName)
        eventIntent.putExtra("permissionStatus", permissionStatus)
        this.sendBroadcast(eventIntent)
        finish()
    }

    //blututh permission
    private fun setupScanner() {
        Log.d(TAG, "Setting up scanner...")
        var manager: BluetoothManager? = null
        manager = applicationContext
            .getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = manager.adapter
        requestBluetoothOn()
    }

    private fun requestBluetoothOn() {
        if (btAdapter == null || !btAdapter!!.isEnabled) {
            Log.d(TAG, "Bluetooth not enabled, requesting permission.")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) return
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            launchEvent(true)
        }
    }

    companion object {
        private const val TAG = "MonitoringActivity"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        const val REQUEST_LOCATION = 130
        const val REQUEST_ENABLE_BLUETOOTH = 1
    }
}