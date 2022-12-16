package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bilkenthalisahaapp.appObjects.*;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;


public class RatePlayersAdapter extends RecyclerView.Adapter<RatePlayersAdapter.ViewHolder> {
    private Match match;
    private ArrayList<Player> players;
    //private ArrayList<User> users;
    private Context context;

    public RatePlayersAdapter(Match match, Context context) {
        this.context = context;
        this.match = match;
        this.players = match.getPlayers();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView profileName;
        private ShapeableImageView circularProfileImage;
        private Spinner spinner;

        public ViewHolder (View itemView) {
            super(itemView);

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_player_recycler, parent, false);
        return new RatePlayersAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = players.get(position);
        //User user = getUser(player.getUserID);
        //holder.getProfileName().setText(String.format("%s %s", user.getName, user.getSurname));
        ArrayAdapter<CharSequence> ratings = ArrayAdapter.createFromResource(context,
                R.array.ratings_array, android.R.layout.simple_spinner_item);
        ratings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(ratings);

        //FirebaseStorageMethods.showImage( context, holder.circularProfileImage, user.getUserID() );
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    /*public User getUser(String userID) {
        for(int i = 0; i < users.size(); i++) {
            if(userID.equals(users.get(i).getUserID)) {
                return users.get(i)
            }
        }
    }

            */
}
