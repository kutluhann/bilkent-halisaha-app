package com.example.bilkenthalisahaapp;


import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class Firestore {

    private static ArrayList<Match> matches = new ArrayList<Match>();

    public static void updateMatch( Match match ) {
        //final int MIN_MATCH_RANGE_AS_SECONDS = 60*60; //only for each hour, there can be added matches

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long time = match.getTime().getSeconds();
        //time = time - time % MIN_MATCH_RANGE_AS_SECONDS;

        //because there will be three stadiums add stadium name to the database key
        db.collection("matches").document( time + "" ).set(match);

    }
    

    public static ArrayList<Match> getMatches() {
        return matches;
    }
}
