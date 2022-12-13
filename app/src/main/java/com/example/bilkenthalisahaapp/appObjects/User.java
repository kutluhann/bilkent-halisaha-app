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

    /*
    public double calculateAverageRating(){
        double average=0;
        for(int i=0; i<players.size() ; i++){
            //average+= player.get(i).getMatchRating().getAverageRating()
        }

        average = average/players.size();

        return average;
    }
*/
    public void joinMatch(String matchID, int position, Team team){

        Player player = new Player(userID, position, matchID, team,false);
        //players.add(player);

        //use firebase

    }

    public void createMatch(Location location , Timestamp time, int numberOfPlayersPerTeam, int myPosition){
        //Match match = new Match();
        //Adding the properties of match from the parameters of the method.
        //Player p = new Player(myPosition,match.getID(), new Team(), true);
        // players.add(p);

    }
}
