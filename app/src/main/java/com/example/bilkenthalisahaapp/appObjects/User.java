package com.example.bilkenthalisahaapp.appObjects;

import com.example.bilkenthalisahaapp.Firestore;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    private String userID;
    private String name;
    private String surname;

    private ArrayList<String> matchIds;
    private int numberOfMissedMatches;
    private int numberOfAttendedMatches;
    private int numberOfMVPRewards;
    private double averageRating;
    private String profilePictureURL;
    private int voterCount;

    public User() {

    }

    public User(String userId, String name, String surname) {
        this.userID = userId;
        this.name = name;
        this.surname = surname;
        matchIds = new ArrayList<String>();
    }

    public ArrayList<String> getMatchIds() {
        return matchIds;
    }

    public String getUserID() {
        return userID;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getNumberOfMissedMatches() {
        return numberOfMissedMatches;
    }

    public int getNumberOfAttendedMatches() {
        return numberOfAttendedMatches;
    }

    public int getNumberOfMVPRewards() {
        return numberOfMVPRewards;
    }


    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public int getVoterCount() {
        return voterCount;
    }


    public double calculateAverageRating(){
        double average=0;

        // get all matchIDs and find all match ratings. Then add them to average and divide it to the voter count


        return average;
    }

    public void joinMatch(String matchID, int position, Team team){

        Player player = new Player(userID, position, matchID, team,false);
        //matchIds.add(matchID);
        //get the match object through using the matchID through firebase
        //match.addLocalPlayer(player);
        // This is the logic

        //Do all these with firebase, do not use local operations


    }

    public void createMatch(String location , Timestamp time, int numberOfPlayersPerTeam, int myPosition){


    }
}
