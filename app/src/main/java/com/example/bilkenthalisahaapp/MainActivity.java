package com.example.bilkenthalisahaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<DayModel> dayModels = new ArrayList<DayModel>();
    ArrayList<MatchModel> matchModels = new ArrayList<MatchModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.dateRecyclerView);

        setDayModels();

        Day_RecyclerAdapter adapter = new Day_RecyclerAdapter(this, dayModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setDayModels() {
        String[] dates = getResources().getStringArray(R.array.dates);
        setMatchModels();

        for (int i = 0; i < dates.length; i++) {
            dayModels.add(new DayModel(dates[i], matchModels));
        }
    }

    private void setMatchModels() {
        String[] locations = getResources().getStringArray(R.array.locations);
        String[] times = getResources().getStringArray(R.array.times);
        String[] capacities = getResources().getStringArray(R.array.capacities);

        for (int i = 0; i < locations.length; i++) {
            matchModels.add(new MatchModel(locations[i], times[i], capacities[i]));
        }
    }
}