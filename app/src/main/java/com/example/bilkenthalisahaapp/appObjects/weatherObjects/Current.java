package com.example.bilkenthalisahaapp.appObjects.weatherObjects;

public class Current {
    long last_updated_epoch;
    String last_updated;
    Condition condition;
    double temp_c, uv;

    public long getLast_updated_epoch() {
        return last_updated_epoch;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public Condition getCondition() {
        return condition;
    }

    public double getTemp_c() {
        return temp_c;
    }

    public double getUv() {
        return uv;
    }
}