package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NavigableMap;

public class LastMatchAdapter extends RecyclerView.Adapter<LastMatchAdapter.ViewHolder> {
    private ArrayList<Match> lastMatches;
    private Fragment fragment;

    public LastMatchAdapter(ArrayList<Match> lastMatches, Fragment fragment) {
        this.lastMatches = lastMatches;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_match_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Match lastMatch = lastMatches.get(position);
        holder.getPitch().setText(lastMatch.getLocation());
        holder.getJoinNumberText().setText(lastMatch.getPlayers().size() + "/" + lastMatch.getMaxTeamSize());
        holder.getTime().setText(CommonMethods.generateTimeString(lastMatch.getTime().getSeconds()));
        holder.getDate().setText(CommonMethods.generateDateString(lastMatch.getTime().getSeconds()));

        Bundle matchBundle = new Bundle();
        matchBundle.putString("matchId", lastMatch.getMatchId() );

        holder.getRatePlayersButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_HomeScreen_to_MatchInfo, matchBundle);
            }
        });

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_HomeScreen_to_MatchInfo, matchBundle);
            }
        });
    }
    private String addZerosToLength(String text, int length ) {
        while( text.length() < length ) {
            text = "0" + text;
        }
        return text;
    }


    @Override
    public int getItemCount() {
        return lastMatches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pitch;
        private TextView date;
        private TextView time;
        private TextView joinNumberText;
        private LinearLayout ratePlayersButton;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pitch = itemView.findViewById(R.id.stadiumName);
            date = itemView.findViewById(R.id.dateText);
            time = itemView.findViewById(R.id.matchTimeText);
            joinNumberText = itemView.findViewById(R.id.joinNumberText);
            ratePlayersButton = itemView.findViewById(R.id.ratePlayersButton);
            this.view = itemView;
        }

        public TextView getPitch() {
            return pitch;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getTime() {
            return time;
        }

        public TextView getJoinNumberText() {
            return joinNumberText;
        }

        public LinearLayout getRatePlayersButton() {
            return ratePlayersButton;
        }

        public View getView() {
            return view;
        }
    }
}
