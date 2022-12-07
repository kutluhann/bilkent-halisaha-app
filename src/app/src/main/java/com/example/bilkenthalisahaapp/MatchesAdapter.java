package com.example.bilkenthalisahaapp;

import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

    private ArrayList<Match> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView stadiumNameView;
        private final TextView timeView;
        private final TextView joinNumberView;
        private final Button joinButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            stadiumNameView = (TextView) view.findViewById(R.id.stadiumName);
            timeView = (TextView) view.findViewById(R.id.matchTimeText);
            joinNumberView = (TextView) view.findViewById(R.id.joinNumberText);
            joinButton = (Button) view.findViewById(R.id.button);



        }

        public TextView getStadiumNameView() {
            return stadiumNameView;
        }

        public Button getJoinButton() {
            return joinButton;
        }

        public TextView getJoinNumberView() {
            return joinNumberView;
        }

        public TextView getTimeView() {
            return timeView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MatchesAdapter(ArrayList<Match> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Match match = localDataSet.get(position);
        viewHolder.getStadiumNameView().setText(match.getLocation());

        String timeString = generateTimeString( match.getTime().getSeconds()  );
        viewHolder.getTimeView().setText( timeString );
        String joinNumberString = match.getTeamSize() + "/" + match.getMaxTeamSize();
        viewHolder.getJoinNumberView().setText( joinNumberString );
    }

    private String generateTimeString(long currentTime) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(currentTime * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        return hour + "." + minute;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
