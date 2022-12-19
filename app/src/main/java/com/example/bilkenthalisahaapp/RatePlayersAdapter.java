package com.example.bilkenthalisahaapp;

import static java.lang.Double.NaN;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bilkenthalisahaapp.appObjects.*;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.HashMap;


public class RatePlayersAdapter extends RecyclerView.Adapter<RatePlayersAdapter.ViewHolder> {
    private ArrayList<Player> players;
    private HashMap<Player, User> users;
    private Context context;
    private FragmentRatePlayers fragment;
    private HashMap<Player,Integer> selectionMap;

    public RatePlayersAdapter(ArrayList<Player> players, HashMap<Player,
            User> users, Context context,
                              HashMap<Player,Integer> selectionMap,FragmentRatePlayers fragment) {
        this.context = context;
        this.players = players;
        this.users = users;
        this.fragment = fragment;
        this.selectionMap = selectionMap;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView profileName;
        private ShapeableImageView circularProfileImage;
        private Spinner spinner;
        private CardView playerCardView;

        public ViewHolder (View itemView) {
            super(itemView);

            playerCardView = itemView.findViewById(R.id.player_cardview);
            profileName = itemView.findViewById(R.id.profile_name);
            circularProfileImage = itemView.findViewById(R.id.profile_picture_rating);
            spinner = itemView.findViewById(R.id.spinner_rate);
        }

        public TextView getProfileName () {
            return profileName;
        }

        public ShapeableImageView getCircularProfileImage() {
            return circularProfileImage;
        }

        public Spinner getSpinner() {
            return spinner;
        }
    }
    public RatePlayersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.rate_player_recycler, parent, false);
        return new RatePlayersAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        Player player = players.get(position);
        User user = users.get(player);
        ArrayAdapter<CharSequence> ratings = ArrayAdapter.createFromResource(context,
                R.array.ratings_array, android.R.layout.simple_spinner_item);
        ratings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(ratings);

        int oldSelection = selectionMap.getOrDefault(player, 0);
        holder.spinner.setSelection(oldSelection);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionMap.put(player, holder.spinner.getSelectedItemPosition() );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

        User activeUser = fragment.getActiveUser();
        if(activeUser != null) {

            if(player.getMatchRating().getGivenRatingsByPlayer().get( activeUser.getUserID()) != null) {
                int oldRatingByPlayer = player.getMatchRating().getGivenRatingsByPlayer().get(activeUser.getUserID());
                holder.spinner.setEnabled(false);
                holder.spinner.setSelection(oldRatingByPlayer + 1);
            } else if(activeUser.getUserID().equals(player.getUserID())) {
                holder.spinner.setEnabled(false);
            } else {
                holder.spinner.setEnabled(true);
            }

        }

        if(user != null){
            double matchRating = player.getMatchRating().getAverageRating();
            String profileNameText;
            if(matchRating > 0 && matchRating <= 10) {
                profileNameText = String.format("%s (%.1f)", user.getFullName(), matchRating);
            } else {
                profileNameText = String.format("%s", user.getFullName());
            }

            holder.getProfileName().setText(profileNameText);
            FirebaseStorageMethods.showImage( context, holder.circularProfileImage,
                    user.getProfilePictureURL(), R.drawable.default_profile_photo );
        }


    }

    @Override
    public int getItemCount() {
        return players.size();
    }


}
