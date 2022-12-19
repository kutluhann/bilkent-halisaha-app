package com.example.bilkenthalisahaapp;

import com.example.bilkenthalisahaapp.appObjects.weatherObjects.ForecastWeather;
import com.example.bilkenthalisahaapp.appObjects.weatherObjects.Forecastday;
import com.example.bilkenthalisahaapp.appObjects.weatherObjects.Hour;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastAPI {
    private static ForecastAPI instance = null;
    private ForecastWeather forecastWeather = null;
    private ArrayList<Hour> hours = null;

    private ForecastAPI() {
        Call<ForecastWeather> call = RetrofitClient.getInstance().getApi().getForecastWeather(WeatherAPI.KEY, "Ankara", "3", "no", "no");

        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                try {
                    forecastWeather = response.body();
                    hours = new ArrayList<Hour>();

                    for (Forecastday forecastday : forecastWeather.getForecast().getForecastday()) {
                        for (Hour hour : forecastday.getHour()) {
                            hours.add(hour);
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
            }
        });
    }

    public static synchronized ForecastAPI getInstance() {
        if (instance == null) {
            instance = new ForecastAPI();
        }
        return instance;
    }

    public ForecastWeather getForecastWeather() {
        return forecastWeather;
    }

    public Hour getHour(long epoch) {
        if (hours != null) {
            for (Hour hour: hours) {
                if (hour.getTime_epoch() == epoch) {
                    return hour;
                }
            }
        }
        return null;
    }
}
