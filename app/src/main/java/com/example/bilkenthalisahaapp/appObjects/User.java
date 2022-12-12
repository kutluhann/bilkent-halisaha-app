package com.example.bilkenthalisahaapp.appObjects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    private UUID userID;
    private String fullName;
    private String userName;

    private ArrayList<Player> players;
    private int numberOfMissedMatches;
    private int numberOfAttendedMatches;
    private int numberOfMVPRewards;
    //private double averageRating; this variable may not be there
    private String profilePictureURL;

    public User(UUID userID, String fullName, String userName) {
        this.userID = userID;
        this.fullName = fullName;
        this.userName = userName;

        players = new ArrayList<Player>();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
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
    public double calculateAverageRating(){
        double average=0;
        for(int i=0; i<players.size() ; i++){
            //average+= player.get(i).getMatchRating().getAverageRating()
        }

        average = average/players.size();

        return average;
    }

    public void joinMatch(UUID matchID , int position, Team team){

        Player player = new Player(position,matchID,team,false);
        players.add(player);

        //use firebase

    }

    public void createMatch(Location location , Timestamp time, int numberOfPlayersPerTeam, int myPosition){
        //Match match = new Match();
        //Adding the properties of match from the parameters of the method.
        //Player p = new Player(myPosition,match.getID(), new Team(), true);
        // players.add(p);

    }
}