package com.example.bilkenthalisahaapp.appObjects;

import java.util.UUID;

public class Player {

    private int position;
    private String matchID;
    private String userID;
    private MatchRating matchRating;
    private Team team;
    private boolean isOwner;
    private Boolean hasAttended;

    public Player(String userID, int position, String matchID, Team team,boolean isOwner) {
        this.userID = userID;
        this.isOwner = isOwner;
        this.position = position;
        this.matchID = matchID;
        this.team = team;

    }

    public int getPosition() {
        return position;
    }

    public String getMatchID() {
        return matchID;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public MatchRating getMatchRating() {
        return matchRating;
    }

    public Team getTeam() {
        return team;
    }

    public Boolean getHasAttended() {
        return hasAttended;
    }

    public String getUserID() {
        return userID;
    }

    public void vote(int rating, Player player){
        // change the hashmap in the (use firebase)

    }







}
