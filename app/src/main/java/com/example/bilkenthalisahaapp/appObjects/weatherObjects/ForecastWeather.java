package com.example.bilkenthalisahaapp.appObjects.weatherObjects;

public class ForecastWeather {
    Location location;
    Current current;
    Forecast forecast;

    public Location getLocation() {
        return location;
    }

    public Current getCurrent() {
        return current;
    }

    public Forecast getForecast() {
        return forecast;
    }
}