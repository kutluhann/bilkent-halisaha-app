package com.example.bilkenthalisahaapp.appObjects;


import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Match implements Comparable<Match> {

    private String matchId;
    private String location;
    private Timestamp time;
    private int maxTeamSize;
    private ArrayList<Player> players;
    private ArrayList<String> userIds;


    public Match() {

    }

    public static String formatStadiumName( String text ) {
        String output = text.toLowerCase(Locale.ENGLISH).replace(" ", "");
        return output;
    }


    public Match(String location, Timestamp time, int maxTeamSize) {
        this.location = location;
        this.time = time;
        this.maxTeamSize = maxTeamSize;

        this.matchId = time.getSeconds() + "-" +  formatStadiumName(location);
        this.players = new ArrayList<Player>();
        this.userIds = new ArrayList<String>();

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getLocation() {
        return location;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public Timestamp getTime() {
        return time;
    }

    //only for initialization
    public void addLocalPlayer(Player player) {
        this.players.add(player);
        this.userIds.add(player.getUserID());
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Match match = (Match) obj;
            if(this.time.getSeconds() == match.time.getSeconds()) {
                return true;
            }
        } finally {
            return false;
        }
    }

    @Override
    public int compareTo(Match match) {
        if(  this.time.getSeconds() < match.time.getSeconds() ) {
            return -1;
        } else if (this.time.getSeconds() == match.time.getSeconds()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Match{" +
                "location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", teamSize='" + players.size() + '\'' +
                ", maxTeamSize='" + maxTeamSize + '\'' +
                '}';
    }


}