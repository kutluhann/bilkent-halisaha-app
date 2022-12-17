package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;



import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentFirstBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentProfileBinding;
import com.example.bilkenthalisahaapp.dialogBoxes.LogOutDialogFragment;

import java.util.ArrayList;

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
        private final View view;


        public ViewHolder(View view) {
            super(view);

            // Define click listener for the ViewHolder's View

            stadiumNameView = (TextView) view.findViewById(R.id.stadiumName);
            timeView = (TextView) view.findViewById(R.id.matchTimeText);
            joinNumberView = (TextView) view.findViewById(R.id.joinNumberText);
            joinButton = (Button) view.findViewById(R.id.button);
            this.view = view;
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

        public View getView() {
            return view;
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

        try {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Match match = localDataSet.get(position);
            viewHolder.getStadiumNameView().setText(match.getLocation());// Problem

            String timeString = CommonMethods.generateTimeString(match.getTime().getSeconds());
            viewHolder.getTimeView().setText(timeString);
            String joinNumberString = match.getPlayers().size() + "/" + match.getMaxTeamSize();
            viewHolder.getJoinNumberView().setText(joinNumberString);

            Bundle matchBundle = new Bundle();
            matchBundle.putString("matchId", match.getMatchId() );

            viewHolder.getJoinButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Navigation.findNavController(viewHolder.itemView).
                            navigate(R.id.action_matches_to_MatchInfo, matchBundle);
                }
            });

            viewHolder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Navigation.findNavController(viewHolder.itemView).
                            navigate(R.id.action_matches_to_MatchInfo, matchBundle);
                }
            });

        }catch (Exception e) {

        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
