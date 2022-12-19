package com.example.bilkenthalisahaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class SignInActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button signInButton;
    TextView goToSingUp, forgotPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        signInButton = findViewById(R.id.signInButton);
        goToSingUp = findViewById(R.id.goToSingUp);
        forgotPassword = findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(view -> {
            signInUser();
        });

        forgotPassword.setOnClickListener(view -> {
            sendForgotPasswordLink();
        });

        goToSingUp.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email cannot be empty!");
            editTextEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password cannot be empty!");
            editTextPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this,"Signed in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendForgotPasswordLink() {
        String email = editTextEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("First, enter your email!");
            editTextEmail.requestFocus();
        } else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "An email reset link is sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}