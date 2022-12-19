package com.example.bilkenthalisahaapp.appObjects.weatherObjects;

public class Hour {
    long time_epoch;
    String time;
    Condition condition;
    double temp_c;

    public long getTime_epoch() {
        return time_epoch;
    }

    public String getTime() {
        return time;
    }

    public Condition getCondition() {
        return condition;
    }

    public double getTemp_c() {
        return temp_c;
    }
}
