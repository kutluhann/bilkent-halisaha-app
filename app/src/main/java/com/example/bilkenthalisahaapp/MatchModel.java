package com.example.bilkenthalisahaapp;

public class MatchModel {
    String location;
    String time;
    String capacity;

    public MatchModel(String location, String time, String capacity) {
        this.location = location;
        this.time = time;
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getCapacity() {
        return capacity;
    }
}
