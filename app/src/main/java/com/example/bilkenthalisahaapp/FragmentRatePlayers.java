package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.databinding.FragmentHomescreenBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentRatePlayersBinding;

public class FragmentRatePlayers extends Fragment {
    FragmentRatePlayersBinding binding;
    Match match;
    RatePlayersAdapter ratePlayersAdapter = new RatePlayersAdapter(match, getContext());
    RecyclerView playersRecycler;

    public void setupRatePlayersAdapter(View view) {
        playersRecycler = view.findViewById(R.id.upcomingMatchRecyler);
        playersRecycler.setNestedScrollingEnabled(false);
        playersRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        playersRecycler.setLayoutManager(linearLayoutManager);
        playersRecycler.setAdapter(ratePlayersAdapter);
    }

    public void handleTeamAClickListener() {
        binding.teamAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void handleTeamBClickListener() {
        binding.teamBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void handleDoneButton() {
        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRatePlayersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleTeamAClickListener();
        handleTeamBClickListener();
        handleDoneButton();
    }
}