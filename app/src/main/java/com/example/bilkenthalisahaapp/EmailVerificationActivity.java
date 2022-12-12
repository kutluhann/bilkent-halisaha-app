package com.example.bilkenthalisahaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {
    TextView emailVerificationText;
    Button resendButton;

    Handler handler = new Handler();
    Runnable runnable;
    final int DELAY = 1000;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        emailVerificationText = findViewById(R.id.emailVerificationText);
        resendButton = findViewById(R.id.resendButton);

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
    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            emailVerificationText.setText("An email has sent to\n " + currentUser.getEmail() + ".\n Please verify your email!");
        } else {
            startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    currentUser.reload();
                    if (currentUser.isEmailVerified()) {
                        handler.removeCallbacks(this);
                        Toast.makeText(EmailVerificationActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EmailVerificationActivity.this, MainActivity.class));
                        finish();
                    }
                    handler.postDelayed(this, DELAY);
                }
            }, DELAY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
    }
}