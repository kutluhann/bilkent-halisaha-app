package com.example.bilkenthalisahaapp.appObjects.weatherObjects;

public class Location {
    String name, region, country, tz_id, localtime;
    long localtime_epoch;
    double lat, lon;

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getTz_id() {
        return tz_id;
    }

    public String getLocaltime() {
        return localtime;
    }

    public long getLocaltime_epoch() {
        return localtime_epoch;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}