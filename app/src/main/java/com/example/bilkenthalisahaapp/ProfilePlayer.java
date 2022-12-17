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

import com.example.bilkenthalisahaapp.appObjects.User;
import com.example.bilkenthalisahaapp.databinding.FragmentProfileBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentProfilePlayerBinding;
import com.example.bilkenthalisahaapp.dialogBoxes.LogOutDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfilePlayer extends Fragment {


    private FragmentProfilePlayerBinding binding;
    private User user;

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
            binding.point.setText(user.getAverageRating() + "");

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


}