package com.example.bilkenthalisahaapp.appObjects;

import java.util.HashMap;

public class MatchRating {

    private double averageRating;
    private Boolean attended;
    private HashMap<String,Integer> givenRatingsByPlayer = new HashMap<String,Integer>();

    public MatchRating(){

    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setVote( String voterId, int rating ) {
        final int NOT_ATTENDED_RATING = 0;
        givenRatingsByPlayer.put( voterId, rating );
        this.averageRating = calculateRating();
        this.attended = calculateAttended();
    }

    public int getGivenRatingByPlayer(Player player ) {
        return this.givenRatingsByPlayer.get( player.getUserID() );
    }

    public boolean isUserVoted(String userId) {
        return this.givenRatingsByPlayer.getOrDefault(userId, -1) != -1;
    }

    public double calculateRating(){
        int totalRating = 0;
        double result;
        int voterCount = 0;

        for(int rating: givenRatingsByPlayer.values()){
            if( rating > 0 ){
                totalRating += rating;
                voterCount++;
            }
        }
        result = (double) totalRating / voterCount;
        return result;
    }

    public boolean calculateAttended() {
        int attendedVote = 0;
        int notAttendedVote = 0;
        for(int rating: givenRatingsByPlayer.values()){
            if( rating > 0 ){
                attendedVote++;
            } else {
                notAttendedVote++;
            }
        }
        return attendedVote > notAttendedVote;
    }

    public Boolean getAttended() {
        return attended;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public HashMap<String, Integer> getGivenRatingsByPlayer() {
        return givenRatingsByPlayer;
    }

}
