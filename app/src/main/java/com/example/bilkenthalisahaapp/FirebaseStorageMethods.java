package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bilkenthalisahaapp.appObjects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseStorageMethods {

    private static final String PRE_PATH = "profilePictures/";

    public static void uploadPhoto(Uri uri, User user, Context context) {

        if(user.getProfilePictureURL() != null) {
            removePhoto( user.getProfilePictureURL() );
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        //String path = PRE_PATH + userId + uri.getLastPathSegment();
        String path = PRE_PATH + user.getUserID() + (Math.random() * 10000);
        StorageReference photoRef = storageRef.child(path);
        photoRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Firestore.updateProfilePicture( user.getUserID(), path );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "An error occured while uploading photo.", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    public static void removePhoto( String path ) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference photoReference = storageRef.child(path);

        photoReference.delete();
    }

    public static void showImage( Context context, ImageView imageView, String path, int defaultDrawable ) {

        if( path != null ) {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference photoReference = storageRef.child(path);

                GlideApp.with(context)
                        .load(photoReference)
                        .into(imageView);
            } catch (Exception e) {
                imageView.setImageResource( defaultDrawable );
            }
        } else {
            imageView.setImageResource( defaultDrawable );
        }



   }


}
