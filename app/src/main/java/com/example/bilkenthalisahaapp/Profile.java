package com.example.bilkenthalisahaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentFirstBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentProfileBinding;
import com.example.bilkenthalisahaapp.dialogBoxes.LogOutDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

public class Profile extends Fragment {

    private FragmentProfileBinding binding;
    private User user;
    private ListenerRegistration listenerRegistration;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
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

            if( user.getProfilePictureURL() != null ) {
                FirebaseStorageMethods.showImage(getContext(), binding.profilePicture, user.getProfilePictureURL() );
            } else {
                binding.profilePicture.setImageResource(R.drawable.default_profile_photo);
            }


            //change last 5 ratings below
        } catch (Exception e) {

        }

    }

    private void getUser( String userId ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection("users")
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
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        getUser(userId);

        binding.logoutButton.setOnClickListener(view1 -> {


            DialogFragment logOutDialog = new LogOutDialogFragment();
            logOutDialog.show(getActivity().getSupportFragmentManager(),"logout");



        });

        binding.profilePicture.setClickable(true);
        binding.profilePicture.setOnClickListener(
                view1 -> {
                    openModal();
                }
        );

        super.onViewCreated(view, savedInstanceState);
    }

    private void openModal() {
        BottomDialog bottomDialog = new BottomDialog(this);
        bottomDialog.show(getParentFragmentManager(), "TAG");
    }

    // Request code for selecting a PDF document.
    private static final int PICK_PHOTO_FILE = 2;

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_PHOTO_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == PICK_PHOTO_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                FirebaseStorageMethods.uploadPhoto(uri, user, getContext());
                Toast.makeText(getActivity(), "Photo is added successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //listenerRegistration.remove();
    }


    public void handleNewPhoto() {
        openFile();
    }

    public void handleDeletePhoto() {
        String photoUrl = user.getProfilePictureURL();
        if (photoUrl != null) {
            FirebaseStorageMethods.removePhoto(photoUrl);
            Firestore.updateProfilePicture(user.getUserID(), null);
            Toast.makeText(getActivity(), "Photo is deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Add a photo first", Toast.LENGTH_SHORT).show();
        }
    }

}