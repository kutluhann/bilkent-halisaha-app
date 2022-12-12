package com.example.bilkenthalisahaapp.appObjects;

import java.util.UUID;

public class Player {

    private int position;
    private UUID matchID;
    private MatchRating matchRating;
    private Team team;
    private boolean isOwner;
    private Boolean hasAttended;

    public Player(int position, UUID matchID, Team team,boolean isOwner) {
        this.isOwner = isOwner;
        this.position = position;
        this.matchID = matchID;
        this.team = team;


    }

    public int getPosition() {
        return position;
    }

    public UUID getMatchID() {
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

    public void vote(int rating, Player player){
        // change the hashmap in the (use firebase)

    }







}
