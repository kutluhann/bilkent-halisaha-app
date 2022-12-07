package com.example.bilkenthalisahaapp;

import java.util.ArrayList;

public class DayModel {
    String date;
    ArrayList<MatchModel> matchModels;

    public DayModel(String date, ArrayList<MatchModel> matchModels) {
        this.date = date;
        this.matchModels = matchModels;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<MatchModel> getMatchModels() {
        return matchModels;
    }
}
