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

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentFormationBinding;
import com.example.bilkenthalisahaapp.interfaces.MatchUpdateHandleable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MatchInfo extends Fragment implements MatchUpdateHandleable {
    private FragmentFormationBinding binding;
    private Button cancelButton;

    private Match match;
    private HashMap<Player, User> users = new HashMap<Player, User>();
    private Team displayedTeam = Team.TEAM_A;

    private void fetchMatch(String matchId) {
        Firestore.fetchMatchInFragment(matchId, this);
    }

    @Override
    public void fetchUsers() {
        ArrayList<Player> players = this.match.getPlayers();
        for( Player player : players ) {
            fetchTheUser(player);
        }
    }

    private void fetchTheUser( Player player ) {
        Firestore.fetchTheUserInFragment(player, users, this );
    }


    @Override
    public void handleDataUpdate() {
        //refreshes the page according to new data or available data
        if(match != null) {
            //handle match data update
            String matchSummary = String.format("%s %s - %s", getDateString(), getTimeString(), match.getLocation());
            binding.matchSumTextView.setText(matchSummary);

            ArrayList<Player> players = match.getPlayers();
            for( Player player : players ) {
                User user = users.get(player);
                if(user != null) {
                    //handle player-user iteration


                }
            }
        }

    }

    @Override
    public void setMatch(Match match) {
        this.match = match;
    }



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
        fetchMatch(matchId);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            //im not sure of that because it can create problem about navigation and navigating up(return back)
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MatchInfo.this)
                        .navigate(R.id.home_navigation);
            }
        });
    }

    private LocalDateTime convertTimestampToDateTime(Timestamp timestamp) {
        //is returning correct time?
        long epochSeconds = timestamp.getSeconds();
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC);
        return localDateTime;

    }

    private String getDateString() {
        LocalDateTime localDateTime = convertTimestampToDateTime( match.getTime() );
        String monthName = localDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String dayOfMonth = localDateTime.getDayOfMonth() + "";
        dayOfMonth = CommonMethods.addZerosToLength(dayOfMonth, 2);
        return dayOfMonth + " " + monthName;
    }

    private String getTimeString() {
        LocalDateTime localDateTime = convertTimestampToDateTime( match.getTime() );
        String hour = localDateTime.getHour() + "";
        hour = CommonMethods.addZerosToLength(hour, 2);
        String minute = localDateTime.getMinute() + "";
        minute = CommonMethods.addZerosToLength(minute, 2);
        return hour + "." + minute;
    }

}
