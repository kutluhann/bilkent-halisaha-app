package com.example.bilkenthalisahaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class LastMatchAdapter extends RecyclerView.Adapter<LastMatchAdapter.ViewHolder> {
    private Match lastMatch;

    public LastMatchAdapter(Match lastMatch) {
        this.lastMatch = lastMatch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_match_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getPitch().setText(lastMatch.getLocation().getPitchName());
        holder.getJoinNumberText().setText(lastMatch.getTeamSize() + "/" + lastMatch.getMaxTeamSize());
        holder.getTime().setText(generateTimeString(lastMatch.getTime().getSeconds()));
        holder.getDate().setText(generateDateString(lastMatch.getTime().getSeconds()));
        //TODO implement ratePlayers button
    }
    private String addZerosToLength(String text, int length ) {
        while( text.length() < length ) {
            text = "0" + text;
        }
        return text;
    }

    private String generateTimeString(long currentTime) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(currentTime * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        return addZerosToLength( hour + "", 2) + "." + addZerosToLength( minute + "", 2);
    }
    private String generateDateString(long currentTime) {
        LocalDate localDate = Instant.ofEpochMilli(currentTime * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
        int day = localDate.getDayOfMonth();
        String month = localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = localDate.getYear();
        String dayName = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return String.format("%d %s %d - %s", day, month, year, dayName);
    }

    @Override
    public int getItemCount() {
        if(lastMatch == null){
            return 0;
        }
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pitch;
        private TextView date;
        private TextView time;
        private TextView joinNumberText;
        private Button ratePlayersButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pitch = itemView.findViewById(R.id.stadiumName);
            date = itemView.findViewById(R.id.dateText);
            time = itemView.findViewById(R.id.matchTimeText);
            joinNumberText = itemView.findViewById(R.id.joinNumberText);
            ratePlayersButton = itemView.findViewById(R.id.ratePlayersButton);
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

        public Button getRatePlayersButton() {
            return ratePlayersButton;
        }
    }
}
