package com.example.bilkenthalisahaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.example.bilkenthalisahaapp.appObjects.*;


public class SignUpActivity extends AppCompatActivity {
    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    Button signUpButton;
    TextView goToSingIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        signUpButton = findViewById(R.id.signUpButton);
        goToSingIn = findViewById(R.id.goToSingIn);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(view -> {
            createUser();
        });

        goToSingIn.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void createUser() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String address = "";

        if (!TextUtils.isEmpty(email) && email.contains("@")) {
             address = email.split("@")[1];
        }

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("First name cannot be empty!");
            editTextFirstName.requestFocus();
        } else if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last name cannot be empty!");
            editTextLastName.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email cannot be empty!");
            editTextEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty!");
            editTextPassword.requestFocus();
        } else if (!address.contains("bilkent.edu.tr")) {
            editTextEmail.setError("Enter a Bilkent mail adress!");
            editTextEmail.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String userId = currentUser.getUid();
                        String fullName = firstName + " " + lastName;
                        User user = new User( userId, firstName, lastName );
                        Firestore.createUser(user);

                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //Uri photoUri = Uri.parse("R.drawable.east_campus");
                                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName)
                                            //.setPhotoUri(photoUri)
                                            .build();


                                    mAuth.getCurrentUser().updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this,"Signed up successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }
}