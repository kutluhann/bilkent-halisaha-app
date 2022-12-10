package com.example.bilkenthalisahaapp.appObjects;

public class Location {

    private String pitchName;
    private double latitude;
    private double longitude;

    public Location(String pitchName, double latitude, double longitude) {
        this.pitchName = pitchName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPitchName() {
        return pitchName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
