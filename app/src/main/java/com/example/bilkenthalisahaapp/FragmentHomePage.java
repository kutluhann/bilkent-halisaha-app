package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentAddMatchBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentHomescreenBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentHomePage extends Fragment {
    FragmentHomescreenBinding binding;
    User user;

    ArrayList<Match> upcomingMatches = new ArrayList<Match>();
    UpcomingMatchesAdapter upcomingMatchesAdapter = new UpcomingMatchesAdapter(upcomingMatches);
    RecyclerView upcomingMatchesRecyclerView;

    ArrayList<Match> lastMatches = new ArrayList<Match>();
    LastMatchAdapter lastMatchAdapter = new LastMatchAdapter(lastMatches);
    RecyclerView lastMatchesRecyclerView;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getCurrentUser() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            //Log.d(TAG, "Current data: " + snapshot.getData());
                            user = snapshot.toObject(User.class);
                            initialization();
                        } else {
                            //Log.d(TAG, "Current data: null");
                            user = Firestore.createUserAndSave();
                            initialization();
                        }
                    }
                });
    }


    //upcoming matches adapter
    private void setupUpcomingMatchesAdapter(View view) {
        upcomingMatchesRecyclerView = view.findViewById(R.id.upcomingMatchesRecylerView);
        upcomingMatchesRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        upcomingMatchesRecyclerView.setLayoutManager(linearLayoutManager);
        upcomingMatchesRecyclerView.setAdapter(upcomingMatchesAdapter);
    }

    private void getUpcomingUserMatches() {

            final int MAX_FETCH_NUMBER = 5;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query query;
            query = db.collection("matches")
                    .whereArrayContains("userIds", user.getUserID() )
                    .orderBy("time", Query.Direction.ASCENDING)
                    .startAt( Timestamp.now() )
                    .limit(MAX_FETCH_NUMBER);


            ListenerRegistration listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Match newMatch = dc.getDocument().toObject(Match.class);

                            int indexOf = Collections.binarySearch(upcomingMatches, newMatch);
                            switch (dc.getType()) {
                                case ADDED:
                                    if(indexOf < 0) {
                                        int addTo = Math.abs(indexOf) - 1;
                                        upcomingMatches.add(addTo, newMatch);
                                        upcomingMatchesAdapter.notifyItemInserted(addTo);
                                    }
                                    break;
                                case MODIFIED:
                                    if(indexOf > - 1) {
                                        upcomingMatches.set(indexOf, newMatch);
                                        upcomingMatchesAdapter.notifyItemChanged(indexOf);
                                    }
                                    break;
                                case REMOVED:
                                    if(indexOf > - 1) {
                                        upcomingMatches.remove(indexOf);
                                        upcomingMatchesAdapter.notifyItemRemoved(indexOf);
                                    }
                                    break;
                            }


                    }

                }
            });
    }

    //lastMatchesAdapter
    private void setupLastMatchesAdapter(View view) {
        lastMatchesRecyclerView = view.findViewById(R.id.lastMatchRecylerView);
        lastMatchesRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lastMatchesRecyclerView.setLayoutManager(linearLayoutManager);
        lastMatchesRecyclerView.setAdapter(lastMatchAdapter);
    }

    private void getLastMatches() {

        final int LAST_DAYS_FETCH_NUMBER = 3;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        Instant instant = Instant.ofEpochSecond( Timestamp.now().getSeconds() );
        Instant limitInstant = instant.minusSeconds( CommonMethods.ONE_DAY_AS_SECONDS * LAST_DAYS_FETCH_NUMBER );
        Timestamp limit = new Timestamp(limitInstant.getEpochSecond(), 0);

        query = db.collection("matches")
                .whereArrayContains("userIds", user.getUserID() )
                .orderBy("time", Query.Direction.DESCENDING)
                .startAt( Timestamp.now() )
                .endBefore( limit );

        ListenerRegistration listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    Match newMatch = dc.getDocument().toObject(Match.class);

                        int indexOf = Collections.binarySearch(lastMatches, newMatch, Collections.reverseOrder() );
                        switch (dc.getType()) {
                            case ADDED:
                                if (indexOf < 0) {
                                    int addTo = Math.abs(indexOf) - 1;
                                    lastMatches.add(addTo, newMatch);
                                    lastMatchAdapter.notifyItemInserted(addTo);
                                }
                                break;
                            case MODIFIED:
                                if (indexOf > -1) {
                                    lastMatches.set(indexOf, newMatch);
                                    lastMatchAdapter.notifyItemChanged(indexOf);
                                }
                                break;
                            case REMOVED:
                                if (indexOf > -1) {
                                    lastMatches.remove(indexOf);
                                    lastMatchAdapter.notifyItemRemoved(indexOf);
                                }
                                break;
                        }

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomescreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void initialization() {
        try {
            setupUpcomingMatchesAdapter(getView());
            setupLastMatchesAdapter(getView());

            getUpcomingUserMatches();
            getLastMatches();
        } catch (Exception e) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            getCurrentUser();

        } catch(Exception e) {
            Exception exception = e;
        }

    }
}
