package com.spotsense.data.network.model;

public class GeoFenceDatabaseModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeofenceDate() {
        return geofenceDate;
    }

    public void setGeofenceDate(String geofenceDate) {
        this.geofenceDate = geofenceDate;
    }

    String name;

    public GeoFenceDatabaseModel(String name, String geofenceDate) {
        this.name = name;
        this.geofenceDate = geofenceDate;
    }

    String geofenceDate;
}
