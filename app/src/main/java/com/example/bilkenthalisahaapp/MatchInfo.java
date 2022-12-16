package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.databinding.FragmentFormationBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentHomescreenBinding;

public class MatchInfo extends Fragment {
    FragmentFormationBinding binding;
    Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelButton = view.findViewById(R.id.cancelMatchButton);

        Bundle matchBundle = getArguments();
        String matchId = matchBundle.getString("matchId");

        cancelButton.setOnClickListener(new View.OnClickListener() {


            //im not sure of that because it can create problem about navigation and navigating up(return back)
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MatchInfo.this)
                        .navigate(R.id.home_navigation);
            }
        });
    }

}
