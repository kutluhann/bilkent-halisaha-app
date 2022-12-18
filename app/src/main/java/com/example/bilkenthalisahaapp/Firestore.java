package com.example.bilkenthalisahaapp;



import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.appObjects.weatherObjects.Hour;
import com.example.bilkenthalisahaapp.interfaces.MatchUpdateHandleable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class Firestore {


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

    public static void addPlayerToMatch(Player player , Match match) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef = db.collection("matches").document(match.getMatchId());
        matchRef.update("players", FieldValue.arrayUnion(player));
        matchRef.update("userIds", FieldValue.arrayUnion(player.getUserID()));

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Match currentMatch = transaction.get(matchRef).toObject(Match.class);
                int indexOf = currentMatch.getPlayers().indexOf(player);
                if(indexOf > -1 ) return null; //error if already in

                //if time passed
                if(CommonMethods.isMatchPassed(currentMatch)) return  null;

                //if position is filled
                HashMap<Integer, Player> positionMap = currentMatch.generatePositionMap();
                if( positionMap.get(player.getPosition()) != null ) return null;

                transaction.update(matchRef, "players", FieldValue.arrayUnion(player));
                transaction.update(matchRef, "userIds", FieldValue.arrayUnion(player.getUserID()));

                // Success
                return null;

            }

        });
    }

    public static void removePlayerFromMatch(Player player , Match match){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());

        int indexOf = match.getPlayers().indexOf(player);
        if(indexOf > -1 ) {
            Player playerToRemove = match.getPlayers().get(indexOf);
            matchRef.update("players", FieldValue.arrayRemove( playerToRemove ));
            matchRef.update("userIds", FieldValue.arrayRemove( playerToRemove.getUserID() ));
        }

    }

    public static void changePositionOfPlayer(Player oldPlayer, Player newPlayer, Match match) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Match currentMatch = transaction.get( matchRef ).toObject(Match.class);
                int indexOf = currentMatch.getPlayers().indexOf( oldPlayer );
                Player currentPlayer = currentMatch.getPlayers().get(indexOf);

                //if time passed
                if(CommonMethods.isMatchPassed(currentMatch)) return null;

                //if position is filled
                HashMap<Integer, Player> positionMap = currentMatch.generatePositionMap();
                if( positionMap.get(newPlayer.getPosition()) != null ) return null;

                transaction.update( matchRef, "players", FieldValue.arrayRemove( currentPlayer ) );
                transaction.update( matchRef, "userIds", FieldValue.arrayRemove( currentPlayer.getUserID() ) );

                transaction.update( matchRef, "players", FieldValue.arrayUnion( newPlayer ) );
                transaction.update( matchRef, "userIds", FieldValue.arrayUnion( newPlayer.getUserID() ) );

                // Success
                return null;

            }
        });
    }

    public static void removeMatch( Match match ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef =  db.collection("matches").document(match.getMatchId());
        matchRef.delete();
    }


    public static void votePlayer( String voterUserId, Player refPlayer, int rating) {

        //TO DO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = refPlayer.getUserID();
        String matchId = refPlayer.getMatchID();
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference matchRef = db.collection("matches").document(matchId);

        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        User user = transaction.get(userRef).toObject(User.class);
                        Match match = transaction.get(matchRef).toObject(Match.class);
                        ArrayList<Player> players = match.getPlayers();
                        int playerPosition = Collections.binarySearch(players, refPlayer);
                        //to receive the current player
                        Player player = players.get(playerPosition);

                        //to handle attended change and number
                        Boolean oldAttended = player.getMatchRating().getAttended();
                        //get old mvp
                        Player oldMVP = match.calculateMVPPlayer();

                        //changed match data
                        player.vote(voterUserId, rating);

                        Boolean newAttended = player.getMatchRating().getAttended();
                        Player newMVP = match.calculateMVPPlayer();
                        DocumentReference mvpReference = null;
                        User mvpUser = null;

                        DocumentReference oldMvpReference = null;
                        User oldMvpUser = null;
                        if(newMVP != null) {
                            mvpReference = db.collection("users").document( newMVP.getUserID() );
                            mvpUser = transaction.get( mvpReference ).toObject( User.class );
                        }

                        if(oldMVP != null) {
                            oldMvpReference = db.collection("users").document( oldMVP.getUserID() );
                            oldMvpUser = transaction.get( oldMvpReference ).toObject( User.class );
                        }

                        if (oldAttended == null) {
                            if (newAttended == true) {
                                transaction.update(userRef, "numberOfAttendedMatches", user.getNumberOfAttendedMatches() + 1);
                            } else {
                                transaction.update(userRef, "numberOfMissedMatches", user.getNumberOfMissedMatches() + 1);
                            }
                        } else if (oldAttended != newAttended) {
                            //it means attendance data changed
                            if (newAttended == true) {
                                transaction.update(userRef, "numberOfAttendedMatches", user.getNumberOfAttendedMatches() + 1);
                                transaction.update(userRef, "numberOfMissedMatches", user.getNumberOfMissedMatches() - 1);
                            } else {
                                transaction.update(userRef, "numberOfAttendedMatches", user.getNumberOfAttendedMatches() - 1);
                                transaction.update(userRef, "numberOfMissedMatches", user.getNumberOfMissedMatches() + 1);
                            }
                        }
                        //can be updated here

                        //Handle MVP change

                        if (newMVP != null) {
                            if (oldMVP == null) {
                                //when there is no old mvp
                                transaction.update(mvpReference, "numberOfMVPRewards", mvpUser.getNumberOfMVPRewards() + 1);

                            } else if (oldMVP != newMVP) {
                                //when there is old mvp
                                transaction.update(mvpReference, "numberOfMVPRewards", mvpUser.getNumberOfMVPRewards() + 1);
                                transaction.update(oldMvpReference, "numberOfMVPRewards", oldMvpUser.getNumberOfMVPRewards() - 1);

                            }
                        }

                        double newAvgRating = (user.getAverageRating() * user.getVoterCount() + rating ) / (user.getVoterCount() + 1);
                        transaction.update( userRef, "averageRating", newAvgRating );
                        transaction.update( userRef, "voterCount", user.getVoterCount() + 1 );

                            //set Match
                            transaction.set( matchRef, match );

                            // Success
                            return null;

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "Transaction success!");
                        //toast
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Transaction failure.", e);
                        //toast
                    }
                });

    }

    private static void addWeatherToHours(ArrayList<String> hours, Calendar cal) {
        for(int i = 0; i < hours.size(); i++) {
            String hourString = hours.get(i);
            String formattedText;
            //is it working correct
            int indexOfDot = hourString.indexOf(".");
            int hour = Integer.parseInt(hourString.substring(0, indexOfDot));
            ZonedDateTime zonedDateTime = cal.toInstant().atZone(CommonMethods.ISTANBUL_ZONE_ID);
            zonedDateTime = zonedDateTime.plusHours(hour);
            zonedDateTime = ZonedDateTime.of(
                    zonedDateTime.getYear(), zonedDateTime.getMonthValue(),
                    zonedDateTime.getDayOfMonth(), zonedDateTime.getHour(),
                    0, 0, 0, CommonMethods.ISTANBUL_ZONE_ID );

            try {
                ForecastAPI forecastAPI = ForecastAPI.getInstance();
                long epochSeconds = zonedDateTime.toEpochSecond();
                Hour weatherHourData = forecastAPI.getHour( epochSeconds );
                String condition = weatherHourData.getCondition().getText();
                //bozuk durumlar için dene (farklı veri dönüşü ve internet olmaması ve yanlış link) (try atılabilir buraya catch'te de boşluk olur ya da formatted date değişir)
                formattedText = String.format("%s (%s)", hourString, condition);
            } catch (Exception e) {
                formattedText = hourString;
            }

            hours.set(i, formattedText );
        }
    }

    public static void refreshAvailableHours(Calendar cal, String stadiumName, AddMatch addMatchFragment ) {
        //there might be utc bug

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long epochSeconds = cal.toInstant().getEpochSecond();

        final long DAY_AS_SECONDS = 24*60*60;
        long indexTime = epochSeconds;
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
                            ArrayList<String> availableHours = findAvailableHours( matchesOfDay, cal );
                            addWeatherToHours(availableHours, cal);
                            addMatchFragment.handleAvailableTimesChange( availableHours );

                        } else {
                            String error = task.getException().toString();
                            Log.e("firestore", error);
                        }
                    }
                });

    }



    private static ArrayList<String> findAvailableHours(ArrayList<Match> matchesOfDay, Calendar cal) {
        ArrayList<String> allHours;
        ZonedDateTime currentZonedDateTime = CommonMethods.getZonedDateTime(System.currentTimeMillis() / 1000);
        ZonedDateTime matchZonedDateTime = CommonMethods.getZonedDateTime(cal.toInstant().toEpochMilli() / 1000);

        if( matchZonedDateTime.isBefore(currentZonedDateTime) && matchZonedDateTime.plusDays(1).isBefore(currentZonedDateTime) == false ) {
            int currentHour = currentZonedDateTime.getHour();

            allHours = CommonMethods.getHoursUntilDayEnd(currentHour + 1);
        } else if( matchZonedDateTime.isBefore(currentZonedDateTime) ) {
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

    public static void fetchTheUserInFragment(Player player, HashMap<Player, User> users, MatchUpdateHandleable fragment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userRef = db.collection("users").document(player.getUserID());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    User newUser = snapshot.toObject(User.class);
                    users.put(player, newUser);
                    fragment.handleDataUpdate();
                }
            }
        });
    }

    public static void fetchMatchInFragment(String matchId, MatchUpdateHandleable fragment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference matchRef = db.collection("matches").document(matchId);
        matchRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    Match newMatch = snapshot.toObject(Match.class);
                    fragment.setMatch(newMatch);
                    fragment.setPositionMap( generatePositionMap(newMatch) );
                    fragment.fetchUsers();
                    fragment.updateButtonVisibilities();
                    fragment.handleDataUpdate();
                }
            }
        });
    }

    private static HashMap<Integer,Player> generatePositionMap( Match match ) {
        HashMap<Integer,Player> positionMap = new HashMap<Integer, Player>();
        for(Player player : match.getPlayers()) {
            positionMap.put(player.getPosition(), player);
        }
        return positionMap;
    }



}
