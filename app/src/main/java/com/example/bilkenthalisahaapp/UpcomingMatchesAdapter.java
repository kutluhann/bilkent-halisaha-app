package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;

import org.w3c.dom.Text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class UpcomingMatchesAdapter extends RecyclerView.Adapter<UpcomingMatchesAdapter.ViewHolder>{
    private ArrayList<Match> upcomingMatches;
    private Fragment fragment;

    public UpcomingMatchesAdapter(ArrayList<Match> upcomingMatches, Fragment fragment) {
        this.upcomingMatches = upcomingMatches;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_matches_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Match match = upcomingMatches.get(position);
        holder.getPitch().setText(match.getLocation());
        holder.getDate().setText(CommonMethods.generateDateString(match.getTime().getSeconds()));
        holder.getTime().setText(CommonMethods.generateTimeString(match.getTime().getSeconds()));
        holder.getNumberOfPlayers().setText(match.getPlayers().size() + "/" + match.getMaxTeamSize());

        Bundle matchBundle = new Bundle();
        matchBundle.putString("matchId", match.getMatchId() );

        holder.getCancelButton().setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return upcomingMatches.size();
    }
    private String addZerosToLength(String text, int length ) {
        while( text.length() < length ) {
            text = "0" + text;
        }
        return text;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pitch;
        private TextView date;
        private TextView time;
        private TextView numberOfPlayers;
        private Button cancelButton;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pitch = itemView.findViewById(R.id.upcomingMatchStadiumName);
            date = itemView.findViewById(R.id.dateText);
            time = itemView.findViewById(R.id.matchTimeText);
            numberOfPlayers = itemView.findViewById(R.id.joinNumberText);
            cancelButton = itemView.findViewById(R.id.cancelButton);
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

        public TextView getNumberOfPlayers() {
            return numberOfPlayers;
        }

        public Button getCancelButton() {
            return cancelButton;
        }

        public View getView() {
            return view;
        }
    }
}
