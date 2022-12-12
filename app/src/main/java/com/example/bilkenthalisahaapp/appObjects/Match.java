package com.example.bilkenthalisahaapp.appObjects;


import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class Match implements Comparable<Match> {

    private String location;
    private Timestamp time;
    private int teamSize;
    private int maxTeamSize;
    private String matchID;

    public Match() {

    }

    public Match(String location, Timestamp time, int teamSize, int maxTeamSize) {
        this.location = location;
        this.time = time;
        this.teamSize = teamSize;
        this.maxTeamSize = maxTeamSize;
        this.matchID = time.toString()+location.toString();

    }

    public String getLocation() {
        return location;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public Timestamp getTime() {
        return time;
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
                ", teamSize='" + teamSize + '\'' +
                ", maxTeamSize='" + maxTeamSize + '\'' +
                '}';
    }
}