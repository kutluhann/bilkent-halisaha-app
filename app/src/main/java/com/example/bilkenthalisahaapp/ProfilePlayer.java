package com.example.bilkenthalisahaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.appObjects.User;
import com.example.bilkenthalisahaapp.databinding.FragmentProfileBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentProfilePlayerBinding;
import com.example.bilkenthalisahaapp.dialogBoxes.LogOutDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

public class ProfilePlayer extends Fragment {


    private FragmentProfilePlayerBinding binding;
    private User user;
    private ArrayList<Match> lastMatches = new ArrayList<Match>();

    private void handleGraphRender() {
        //TO-DO
        //Render graphs here!


    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentProfilePlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private String generateFullName(String name, String surname) {
        return name + " " + surname;
    }

    //When user info change and need refresh, update and refresh all changeable things
    private void handleUserInfoChange() {

        try {
            binding.username.setText(generateFullName(user.getName(), user.getSurname()));
            binding.matchesAttendedNumber.setText(user.getNumberOfAttendedMatches() + "");
            binding.mvpNumber.setText(user.getNumberOfMVPRewards() + "");
            binding.missedMatchesNumber.setText(user.getNumberOfMissedMatches() + "");
            binding.point.setText(String.format("%.1f" ,user.getAverageRating()));

            FirebaseStorageMethods.showImage(getContext(), binding.profilePicture, user.getProfilePictureURL(), R.drawable.default_profile_photo );

            //TO-DO
            //change last 5 ratings below
        } catch (Exception e) {

        }

    }
    private void getUser( String userId ) {
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
                            handleUserInfoChange();
                            getLastMatches();
                        } else {
                            //Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        Bundle userBundle = getArguments();
        String userId = userBundle.getString("userId");
        getUser(userId);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //listenerRegistration.remove();
    }

    private void getLastMatches() {

        final int LAST_MATCH_FETCH_NUMBER = 5;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        query = db.collection("matches")
                .whereArrayContains("userIds", user.getUserID() )
                .orderBy("time", Query.Direction.DESCENDING)
                .startAt( Timestamp.now() )
                .limit(LAST_MATCH_FETCH_NUMBER);

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
                                handleGraphRender();
                            }
                            break;
                        case MODIFIED:
                            if (indexOf > -1) {
                                lastMatches.set(indexOf, newMatch);
                                handleGraphRender();
                            }
                            break;
                        case REMOVED:
                            if (indexOf > -1) {
                                lastMatches.remove(indexOf);
                                handleGraphRender();
                            }
                            break;
                    }

                }
            }
        });
    }


}