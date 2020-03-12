package com.spotsense.interfaces;

import java.util.ArrayList;

public interface GetSpotSenseData {
    public void getSpotSenseGeofencingData(String GeofenceTransactions, ArrayList<String> GeofenceTransactionsRequestedId, String geofenceTransitionDetails);

    public void getSpotSenceBeaconData(String GeofenceTransactions, String geofenceTransitionDetails);

}
