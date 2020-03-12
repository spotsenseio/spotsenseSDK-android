package com.spotsencesdk.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spotsencesdk.R;
import com.spotsencesdk.ui.adapter.GeofenceDataAdapter;
import com.spotsencesdk.utils.GlobalMethods;
import com.spotsense.data.network.model.GeoFenceDatabaseModel;
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.sportSenseGeofencing.SpotSence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetSpotSenseData {


    private String ChannelId = "ChannelId";


    SpotSence spotSence;
    RecyclerView recyclerView;
    private GeofenceDataAdapter mAdapter;
    List<GeoFenceDatabaseModel> mydata = new ArrayList<>();
    TextView tvWelcome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydata.clear();
        recyclerView = findViewById(R.id.rvData);
        tvWelcome = findViewById(R.id.tvWelcome);
        AddSpotSenceGeo();
        mydata.addAll(SpotSence.getLocationArray());
        if (mydata.size() > 0) {
            tvWelcome.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        mAdapter = new GeofenceDataAdapter(mydata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }


    public void AddSpotSenceGeo() {
        spotSence = new SpotSence(this, "clientIds", "ClientSecrate", MainActivity.this/*, MainActivity.class*/);//sagar ifuturz
        spotSence.start();

    }


    @Override
    public void getSpotSenceBeaconData(String GeofenceTransactions, String geofenceTransitionDetails) {
        Log.e("beaconTranscation", "s" + GeofenceTransactions);
        if (GeofenceTransactions.equalsIgnoreCase("Enter")) {
            GlobalMethods.sendNotification(getApplicationContext(), 12, ChannelId, geofenceTransitionDetails, "Welcome Zak using beacone", MainActivity.class, R.drawable.notification, R.drawable.notification);
        } else {
            GlobalMethods.sendNotification(getApplicationContext(), 13, ChannelId, geofenceTransitionDetails, "Visit Again Zak  using beacone", MainActivity.class, R.drawable.notification, R.drawable.notification);
        }

        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm a");
            String date = df.format(Calendar.getInstance().getTime());

            mydata.add(new GeoFenceDatabaseModel(geofenceTransitionDetails, date));


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvWelcome.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    mAdapter.notifyDataSetChanged();
                    // Stuff that updates the UI

                }
            });

        } catch (Exception e) {
            Log.e("onerror", "" + e.getLocalizedMessage());
        }

    }

    @Override
    public void getSpotSenseGeofencingData(String GeofenceTransactions, ArrayList<String> GeofenceTransactionsRequestedId, String geofenceTransitionDetails) {
        Log.e("GeofenceTransactions : ", "" + GeofenceTransactions);

        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm a");
            String date = df.format(Calendar.getInstance().getTime());

            mydata.add(new GeoFenceDatabaseModel(geofenceTransitionDetails, date));


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvWelcome.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    mAdapter.notifyDataSetChanged();
                    // Stuff that updates the UI

                }
            });

        } catch (Exception e) {
            Log.e("onerror", "" + e.getLocalizedMessage());
        }
        if (GeofenceTransactions.equalsIgnoreCase("Exited")) {
            GlobalMethods.sendNotification(getApplicationContext(), 13, ChannelId, geofenceTransitionDetails, "Visit Again Zak", MainActivity.class, R.drawable.notification, R.drawable.notification);
        } else {
            GlobalMethods.sendNotification(getApplicationContext(), 12, ChannelId, geofenceTransitionDetails, "Welcome Zak", MainActivity.class, R.drawable.notification, R.drawable.notification);
        }

    }


}
