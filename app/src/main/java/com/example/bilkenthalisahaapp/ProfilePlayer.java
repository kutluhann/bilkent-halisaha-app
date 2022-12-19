package com.example.bilkenthalisahaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.appObjects.MatchRating;
import com.example.bilkenthalisahaapp.appObjects.Player;
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
        if (lastMatches.size() > 0) {
            for (int i = 1; i < lastMatches.size() + 1; i++) {
                Player p = lastMatches.get(i - 1).getPlayerByID(user.getUserID());
                if (p != null) {
                    MatchRating matchRating = p.getMatchRating();

                    View view = getView().findViewById(getResources().getIdentifier("match" + i, "id", getActivity().getPackageName()));
                    TextView text = getView().findViewById(getResources().getIdentifier("ratingText" + i, "id", getActivity().getPackageName()));

                    Bundle matchBundle = new Bundle();
                    matchBundle.putString("matchId", lastMatches.get(i - 1).getMatchId());

                    if (matchRating.getAttended() != null) {
                        if (matchRating.getAttended() == true) {
                            double rating = matchRating.getAverageRating();

                            text.setText(rating + "");
                            text.setClickable(true);

                            int height = calculateGraphHeight(rating);
                            view.getLayoutParams().height = height;

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Navigation.findNavController(view).
                                            navigate(R.id.action_global_match_info, matchBundle);
                                }
                            });
                        } else {
                            text.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            text.setTextSize(12);
                            text.setText("Not\nAttended");

                            view.getLayoutParams().height = 1;
                        }
                    } else {
                        text.setTextSize(12);
                        text.setText("No\nRating");

                        view.getLayoutParams().height = 1;
                    }
                    text.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Navigation.findNavController(view).
                                    navigate(R.id.action_global_match_info, matchBundle);
                        }
                    });
                    view.requestLayout();
                }
            }
        }
    }

    private int calculateGraphHeight(double rating) {
        return (int) calculateDpFromPixels(rating * 140 / 10);
    }

    private double calculateDpFromPixels(double pixels) {
        double density = getResources().getDisplayMetrics().density;
        return pixels * density;
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