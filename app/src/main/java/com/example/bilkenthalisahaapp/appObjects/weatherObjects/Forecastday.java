package com.example.bilkenthalisahaapp.appObjects.weatherObjects;

import java.util.List;

public class Forecastday {
    String date;
    long date_epoch;
    List<Hour> hour;

    public String getDate() {
        return date;
    }

    public long getDate_epoch() {
        return date_epoch;
    }

    public List<Hour> getHour() {
        return hour;
    }
}