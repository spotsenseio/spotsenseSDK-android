package com.spotsense.interfaces;

import java.util.ArrayList;

public interface GetSpotSenseData {
    public void getSpotSenseGeofencingData(String GeofenceTransactions,String geofenceName);

    public void getSpotSenceBeaconData(String beaconTransactions, String beaconName);

}
