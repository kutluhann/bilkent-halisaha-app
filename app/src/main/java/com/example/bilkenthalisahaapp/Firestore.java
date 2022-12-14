package com.example.bilkenthalisahaapp;



import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;

public class Firestore {

    private static ArrayList<Match> matches = new ArrayList<Match>();

    public static void createMatch( Match match, User user ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long time = match.getTime().getSeconds();

        //because there will be three stadiums add stadium name to the database key
        db.collection("matches").document( match.getMatchId()).set(match);

    }

    public static void updateMatch( Match match ) {
        //final int MIN_MATCH_RANGE_AS_SECONDS = 60*60; //only for each hour, there can be added matches

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long time = match.getTime().getSeconds();
        //time = time - time % MIN_MATCH_RANGE_AS_SECONDS;

        //because there will be three stadiums add stadium name to the database key
        db.collection("matches").document( match.getMatchId()).set(match);

    }

    public static void updateProfilePicture(String userId, String path) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .update("profilePictureURL", path);
    }

    public static void addPlayerToMatch(Player player , Match match){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());
                matchRef.update("players", FieldValue.arrayUnion( player ));
                matchRef.update("userIds", FieldValue.arrayUnion( player.getUserID() ));

    }

    public static void removePlayerFromMatch(Player player , Match match){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());
        matchRef.update("players", FieldValue.arrayRemove( player ));
        matchRef.update("userIds", FieldValue.arrayRemove( player.getUserID() ));
    }

    public static void removeMatch( Match match ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());
        matchRef.delete();
    }

    public static void votePlayer( String userId, Player ) {
        //TO DO
    }

    public static void refreshAvailableHours(int day, int month, int year, String stadiumName, AddMatch addMatchFragment ) {
        //there might be utc bug

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0);

        final long DAY_AS_SECONDS = 24*60*60;
        ZoneOffset istanbulZoneOffset = CommonMethods.ISTANBUL_ZONE_ID.getRules().getOffset(localDateTime);
        long indexTime = localDateTime.toEpochSecond( istanbulZoneOffset );
        long limit = indexTime + DAY_AS_SECONDS;

        Timestamp indexTimestamp = new Timestamp(indexTime, 0);
        Timestamp limitTimestamp = new Timestamp(limit, 0);

        Query query;
        query = db.collection("matches")
                .whereEqualTo("location", stadiumName )
                .orderBy("time", Query.Direction.ASCENDING)
                .startAt(indexTimestamp)
                .endBefore(limitTimestamp);

        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Match> matchesOfDay = new ArrayList<Match>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Match newMatch = document.toObject( Match.class );
                                matchesOfDay.add( newMatch );
                            }
                            ArrayList<String> availableHours = findAvailableHours( matchesOfDay, localDateTime );
                            addMatchFragment.handleAvailableTimesChange( availableHours );

                        } else {
                            String error = task.getException().toString();
                            Log.e("firestore", error);
                        }
                    }
                });

    }



    private static ArrayList<String> findAvailableHours(ArrayList<Match> matchesOfDay, LocalDateTime dateTimeOfDay) {
        ArrayList<String> allHours;
        LocalDateTime currentDateTime = LocalDateTime.now();

        if( dateTimeOfDay.isBefore(currentDateTime) && dateTimeOfDay.plusDays(1).isBefore(currentDateTime) == false ) {
            Instant nowInstant = currentDateTime.toInstant( ZoneOffset.of("+03:00") );
            int currentHour = nowInstant.atZone( CommonMethods.ISTANBUL_ZONE_ID ).get( ChronoField.HOUR_OF_DAY );

            allHours = CommonMethods.getHoursUntilDayEnd(currentHour + 1);
        } else if( dateTimeOfDay.isBefore(currentDateTime) ) {
            allHours = new ArrayList<String>();
        } else {
            allHours = CommonMethods.getAllHours();
        }

        for( Match match : matchesOfDay ) {
            Timestamp matchTimestamp = match.getTime();
            long matchEpochTime = matchTimestamp.getSeconds();
            String formattedCurrentTime = CommonMethods.generateTimeString(matchEpochTime);
            allHours.remove(formattedCurrentTime);
        }

        return allHours;
    }

    public static void createUser( User user ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String path = user.getUserID();
        db.collection("users").document( path ).set(user);
    }

    public static User createUserAndSave() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        String firstName = mAuth.getCurrentUser().getDisplayName();
        String lastName = "";
        User user = new User( userId, firstName, lastName );
        Firestore.createUser(user);
        return user;
    }

    public static ArrayList<Match> getMatches() {
        return matches;
    }
}
