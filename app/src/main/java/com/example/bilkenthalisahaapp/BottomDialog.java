package com.example.bilkenthalisahaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bilkenthalisahaapp.appObjects.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BottomDialog extends BottomSheetDialogFragment {
    Button changePhoto, deletePhoto;
    Profile profile;

    public BottomDialog(Profile profile) {
        this.profile = profile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_dialog_layout, container, false);

        Button changePhoto = v.findViewById(R.id.changePhoto);
        Button deletePhoto = v.findViewById(R.id.deletePhoto);

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                profile.handleNewPhoto();
                dismiss();
            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                profile.handleDeletePhoto();
                dismiss();
            }
        });

        return v;
    }
}
