package com.spotsensesdk.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotsensesdk.R;
import com.spotsensesdk.utils.GlobalMethods;
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.spotSenseGeofencing.SpotSense;


public class MainActivity extends AppCompatActivity implements GetSpotSenseData {


    private String ChannelId = "ChannelId";
    SpotSense spotSense;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddSpotSenseGeo();

    }


    public void AddSpotSenseGeo() {
        spotSense = new SpotSense(this, "clientIds", "ClientSecret", MainActivity.this);//sagar ifuturz
        spotSense.start();

    }

    @Override
    public void getSpotSenseBeaconData(String beaconTransactions, String beaconName) {
        if (beaconTransactions.equalsIgnoreCase("Enter")) {
            GlobalMethods.sendNotification(MainActivity.this, 14, ChannelId, beaconName, "Welcome Zak using beacone", MainActivity.class, R.drawable.notification, R.drawable.notification);
        } else {
            GlobalMethods.sendNotification(MainActivity.this, 15, ChannelId, beaconName, "Visit Again Zak  using beacone", MainActivity.class, R.drawable.notification, R.drawable.notification);
        }

    }

    @Override
    public void getSpotSenseGeofencingData(String GeofenceTransactions, /*ArrayList<String> GeofenceTransactionsRequestedId,*/ String geofenceName) {

        if (GeofenceTransactions.equalsIgnoreCase("Exited")) {
            GlobalMethods.sendNotification(MainActivity.this, 13, ChannelId, geofenceName, "Visit Again Zak", MainActivity.class, R.drawable.notification, R.drawable.notification);
        } else {
            GlobalMethods.sendNotification(MainActivity.this, 12, ChannelId, geofenceName, "Welcome Zak", MainActivity.class, R.drawable.notification, R.drawable.notification);
        }

    }

}
