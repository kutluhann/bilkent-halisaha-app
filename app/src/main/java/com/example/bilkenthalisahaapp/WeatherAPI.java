package com.example.bilkenthalisahaapp;

import com.example.bilkenthalisahaapp.appObjects.weatherObjects.ForecastWeather;
import com.example.bilkenthalisahaapp.appObjects.weatherObjects.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    String BASE_URL = "https://api.weatherapi.com/";
    String KEY = "94ed41117b2746219cf203005220812";
    @GET("v1/current.json")
    Call<Weather> getCurrentWeather(@Query("key") String key, @Query("q") String city, @Query("aqi") String aqi);

    @GET("v1/forecast.json")
    Call<ForecastWeather> getForecastWeather(@Query("key") String key, @Query("q") String city, @Query("days") String days, @Query("aqi") String aqi, @Query("alerts") String alerts);
}
