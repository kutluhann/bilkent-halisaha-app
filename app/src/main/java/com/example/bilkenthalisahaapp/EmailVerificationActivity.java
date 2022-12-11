package com.example.bilkenthalisahaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {
    TextView emailVerificationText;
    Button resendButton, refreshButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        emailVerificationText = findViewById(R.id.emailVerificationText);
        resendButton = findViewById(R.id.resendButton);
        refreshButton = findViewById(R.id.refreshButton);

        mAuth = FirebaseAuth.getInstance();

        resendButton.setOnClickListener(view -> {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmailVerificationActivity.this,"Verification email is sent again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EmailVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        refreshButton.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                currentUser.reload();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (currentUser.isEmailVerified()) {
                    Toast.makeText(EmailVerificationActivity.this,"Email verified successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(EmailVerificationActivity.this,"Please verify your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.reload();
            emailVerificationText.setText("An email has sent to " + currentUser.getEmail() + ". Please verify your email!");
        } else {
            startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
        }
    }
}