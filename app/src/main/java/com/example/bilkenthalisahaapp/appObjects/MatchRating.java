package com.example.bilkenthalisahaapp.appObjects;

import java.util.HashMap;

public class MatchRating {

    private double averageRating;
    private HashMap<String,Integer> givenRatingsByPlayer = new HashMap<String,Integer>();



    public MatchRating(){

    }

    public int getGivenRatingByPlayer( Player player ) {
        return this.givenRatingsByPlayer.get( player.getUserID() );
    }

    public double calculateRating(){
        int totalRating = 0;
        int result;

        for(int rating: givenRatingsByPlayer.values()){
            totalRating += rating;
        }
        result = totalRating / givenRatingsByPlayer.values().size();
        return result;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public HashMap<String, Integer> getGivenRatingsByPlayer() {
        return givenRatingsByPlayer;
    }

}
