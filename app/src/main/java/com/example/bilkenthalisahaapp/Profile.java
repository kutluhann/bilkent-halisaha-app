package com.example.bilkenthalisahaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.databinding.FragmentFirstBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();

        binding.logoutButton.setOnClickListener(view1 -> {
            mAuth.signOut();

            Toast.makeText(getActivity(),"Logged our successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), SignInActivity.class));
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}