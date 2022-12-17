package com.example.bilkenthalisahaapp.appObjects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;
import java.util.UUID;

public class Player implements Comparable<Player> {

    private int position;
    private String matchID;
    private String userID;
    private MatchRating matchRating = new MatchRating();;
    private Team team;
    private boolean owner;

    public Player() {

    }

    public Player(String userID, int position, String matchID, Team team,boolean isOwner) {
        this.userID = userID;
        this.owner = owner;
        if(team == Team.TEAM_B) {
            position = -position;
        }
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
        return owner;
    }

    public MatchRating getMatchRating() {
        return matchRating;
    }

    public Team getTeam() {
        return team;
    }

    public String getUserID() {
        return userID;
    }

    public void vote(String voterUserId, int rating){
        matchRating.setVote(voterUserId, rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return this.matchID.equals(player.matchID) && this.userID.equals(player.userID);
    }

    @Override
    public int hashCode() {
        return this.userID.hashCode();
    }

    @Override
    public int compareTo(Player player) {
        return this.userID.compareTo( player.userID );
    }
}
