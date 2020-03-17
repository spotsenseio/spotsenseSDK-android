package com.spotsense.utils.sportSenseGeofencing;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

 public class SpotSencePermissionActivity extends Activity {
    protected static final String TAG = "MonitoringActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    final static int REQUEST_LOCATION = 130;
    static final int REQUEST_ENABLE_BLUETOOTH = 1;


    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);

    }

    private void enabledLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                setupScanner();

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(SpotSencePermissionActivity.this,
                                REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
// Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            enabledLocation();
                        } else {

                            setupScanner();

                        }
                    }
                    else {
                        Log.e("isgpsproviderenabled11", "true");
                        launchEvent(false);
                    }
                }


                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {

            setupScanner();
        } else if (requestCode == REQUEST_LOCATION && resultCode == RESULT_CANCELED) {

            launchEvent(false);
        } else if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {

                launchEvent(true);
            } else {
                setupScanner();
            }
        }
    }

    final String eventName = "your.package.goes.here.EVENT";

    private void launchEvent(boolean permissionStatus) {
        Intent eventIntent = new Intent(eventName);
        eventIntent.putExtra("permissionStatus", permissionStatus);
        this.sendBroadcast(eventIntent);
        finish();
    }


    //blututh permission

    private void setupScanner() {
        Log.d(TAG, "Setting up scanner...");
        BluetoothManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            manager = (BluetoothManager) getApplicationContext()
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            btAdapter = manager.getAdapter();
        }


        requestBluetoothOn();
    }

    private void requestBluetoothOn() {
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth not enabled, requesting permission.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            launchEvent(true);
        }
    }


}
