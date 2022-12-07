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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long time = match.getTime().getSeconds();
        db.collection("matches").document( time + "" ).set(match);

    }

    public static void getNewMatches() {
        final int PAGE_LIMIT = 1;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Query query;
            if (matches.isEmpty() ) {
                query = db.collection("matches")
                        .orderBy("time", Query.Direction.ASCENDING)
                        .limit(PAGE_LIMIT);
            } else {
                Match lastMatch = matches.get(matches.size() - 1);
                query = db.collection("matches")
                        .orderBy("time", Query.Direction.ASCENDING)
                        .startAfter(lastMatch.getTime())
                        .limit(PAGE_LIMIT);
            }

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        return;
                    }

                    for (QueryDocumentSnapshot doc : value) {
                        if(doc!=null && doc.exists()) {
                            Match newMatch = doc.toObject(Match.class);

                            int indexOf = indexOfMatch(newMatch);
                            if (indexOf != -1) {
                                matches.set(indexOf, newMatch);
                            } else {
                                matches.add(newMatch);
                            }
                        }


                    }

                }
            });

    }

    public static int indexOfMatch(Match match) {
        for(int i = 0; i < matches.size(); i++) {
            if( matches.get(i).equals(match) ) return i;
        }
        return -1;
    }

    public static ArrayList<Match> getMatches() {
        return matches;
    }
}
