package com.app.spotsensesdk.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.spotsense.interfaces.GetSpotSenseData
import com.app.spotsense.utils.spotSenseGeofencing.SpotSense
import com.app.spotsensesdk.R
import com.app.spotsensesdk.utils.GlobalMethods

class MainActivity : AppCompatActivity(), GetSpotSenseData {
    private val channelID = "ChannelId"
    private var spotSense: SpotSense? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addSpotSenseGeo()
    }

    private fun addSpotSenseGeo() {
//        spotSense = SpotSense(this, "clientIds", "ClientSecret", this@MainActivity) //sagar ifuturz
        spotSense = SpotSense(
            this,
            getString(R.string.testClientID),
            getString(R.string.testClientSecret),
            this@MainActivity
        )
        spotSense!!.start()
    }

    override fun getSpotSenseGeofencingData(geofenceTransactions: String?, geofenceName: String?) {
        if (geofenceTransactions.equals("Exited",ignoreCase = true)) GlobalMethods.sendNotification(
            this@MainActivity, 13, channelID, geofenceName, "Visit Again Zak",
            MainActivity::class.java, R.drawable.notification, R.drawable.notification
        ) else GlobalMethods.sendNotification(
            this@MainActivity, 12, channelID, geofenceName, "Welcome Zak",
            MainActivity::class.java, R.drawable.notification, R.drawable.notification
        )
    }

    override fun getSpotSenseBeaconData(beaconTransactions: String?, beaconName: String?) {
        if (beaconTransactions.equals("Enter", ignoreCase = true)) GlobalMethods.sendNotification(
            this@MainActivity, 14, channelID, beaconName, "Welcome Zak using beacone",
            MainActivity::class.java, R.drawable.notification, R.drawable.notification
        ) else GlobalMethods.sendNotification(
            this@MainActivity, 15, channelID, beaconName, "Visit Again Zak  using beacone",
            MainActivity::class.java, R.drawable.notification, R.drawable.notification
        )
    }
}