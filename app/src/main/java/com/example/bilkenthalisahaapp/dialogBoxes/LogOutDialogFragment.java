package com.example.bilkenthalisahaapp.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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

import com.example.bilkenthalisahaapp.Profile;
import com.example.bilkenthalisahaapp.SignInActivity;
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

import com.google.firebase.auth.FirebaseAuth;

public class LogOutDialogFragment extends DialogFragment {

    FirebaseAuth mAuth;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {



        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth = FirebaseAuth.getInstance();
                        String userId = mAuth.getCurrentUser().getUid();

                        Profile p = new Profile();
                        p.getUser(userId);
                        mAuth.signOut();
                        Toast.makeText(getActivity(),"Logged out successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), SignInActivity.class));
                        // log out
                        //
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();


    }





}
