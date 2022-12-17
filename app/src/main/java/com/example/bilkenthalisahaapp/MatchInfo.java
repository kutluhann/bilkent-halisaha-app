package com.example.bilkenthalisahaapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentFormationBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.bilkenthalisahaapp.interfaces.MatchUpdateHandleable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

    private Drawable selectedBackgroundDrawable;
    private Drawable normalBackgroundDrawable;

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

        selectedBackgroundDrawable = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.team_selected_background, null );
        normalBackgroundDrawable = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.team_button, null );

        Bundle matchBundle = getArguments();
        String matchId = matchBundle.getString("matchId");
        fetchMatch(matchId);

        //can change the color
        //it can work more beautiul with color filter or animation or both.
        binding.buttonTeamA.setBackground( selectedBackgroundDrawable );
        binding.buttonTeamB.setBackground( normalBackgroundDrawable );

        binding.buttonTeamA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayedTeam = Team.TEAM_A;
                binding.buttonTeamA.setBackground( selectedBackgroundDrawable );
                binding.buttonTeamB.setBackground( normalBackgroundDrawable );
                handleDataUpdate();
            }
        });

        binding.buttonTeamB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayedTeam = Team.TEAM_B;
                binding.buttonTeamB.setBackground( selectedBackgroundDrawable );
                binding.buttonTeamA.setBackground( normalBackgroundDrawable );
                handleDataUpdate();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            //im not sure of that because it can create problem about navigation and navigating up(return back)
            //also it removes match and it is very dangerous
            //also we don't know its old page whether it is home page or match display page
            //changed it to navigateUp
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(MatchInfo.this).navigate(R.id.home_navigation);
                NavHostFragment.findNavController(MatchInfo.this).navigateUp();
            }
        });
    }


    private String getDateString() {
        long epochSeconds = match.getTime().getSeconds();
        ZonedDateTime zonedDateTime = CommonMethods.getZonedDateTime(epochSeconds);
        String monthName = zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String dayOfMonth = zonedDateTime.getDayOfMonth() + "";
        dayOfMonth = CommonMethods.addZerosToLength(dayOfMonth, 2);
        return dayOfMonth + " " + monthName;
    }

    private String getTimeString() {
        long epochSeconds = match.getTime().getSeconds();
        ZonedDateTime zonedDateTime = CommonMethods.getZonedDateTime(epochSeconds);
        String hour = zonedDateTime.getHour() + "";
        hour = CommonMethods.addZerosToLength(hour, 2);
        String minute = zonedDateTime.getMinute() + "";
        minute = CommonMethods.addZerosToLength(minute, 2);
        return hour + "." + minute;
    }

    private ShapeableImageView getPlayerImageView(Player p) {

        if( p.getTeam() == displayedTeam ) {
            if(p.getPosition() == 1) {
                return binding.player1;
            }else if (p.getPosition() == 2) {
                return binding.player2;
            }else if (p.getPosition() == 3) {
                return binding.player3;
            }else if (p.getPosition() == 4) {
                return binding.player4;
            }else if (p.getPosition() == 5) {
                return binding.player5;
            }else if(p.getPosition() == 6) {
                return binding.player6;
            } else {
                return null;
            }
        } else {
            return  null;
        }

    }


}
