package com.spotsencesdk.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotsencesdk.R;
import com.spotsencesdk.utils.GlobalMethods;
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.sportSenseGeofencing.SpotSence;


public class MainActivity extends AppCompatActivity implements GetSpotSenseData {


    private String ChannelId = "ChannelId";
    SpotSence spotSence;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddSpotSenceGeo();

    }


    public void AddSpotSenceGeo() {
        spotSence = new SpotSence(this, "clientIds", "ClientSecrate", MainActivity.this);//sagar ifuturz
        spotSence.start();

    }

    @Override
    public void getSpotSenceBeaconData(String beaconTransactions, String beaconName) {
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
