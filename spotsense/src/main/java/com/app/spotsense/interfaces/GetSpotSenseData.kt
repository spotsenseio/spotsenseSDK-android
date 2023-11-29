package com.app.spotsense.interfaces

interface GetSpotSenseData {
    fun getSpotSenseGeofencingData(geofenceTransactions: String?, geofenceName: String?)
    fun getSpotSenseBeaconData(beaconTransactions: String?, beaconName: String?)
}