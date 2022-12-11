package com.example.bilkenthalisahaapp.appObjects;

import java.util.HashMap;

public class MatchRating {

    private double averageRating;
    private HashMap<String,Integer> givenRatingsByPlayer;

    public MatchRating(){
        givenRatingsByPlayer = new HashMap<String,Integer>();
    }

    public void getRatingGivenByPlayer(){
        //There may not be need for that method.
    }

    public double calculateRating(){
        for(int temp: givenRatingsByPlayer.values()){
            averageRating += temp;
        }
        averageRating= averageRating/ givenRatingsByPlayer.values().size();
        return averageRating;
    }

}
