package com.example.bilkenthalisahaapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.databinding.FragmentRatePlayersBinding;
import com.example.bilkenthalisahaapp.interfaces.MatchUpdateHandleable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentRatePlayers extends Fragment implements MatchUpdateHandleable {
    FragmentRatePlayersBinding binding;
    Match match;
    RatePlayersAdapter ratePlayersAdapter;
    RecyclerView playersRecycler;

    HashMap<Player, User> users = new HashMap<Player, User>();
    HashMap<Player, Integer> playerRatingSelections = new HashMap<Player, Integer>();

    User activeUser;
    Team displayedTeam = Team.TEAM_A;

    Button doneButton;
    Button teamAButton;
    Button teamBButton;

    private Drawable selectedBackgroundDrawable;
    private Drawable normalBackgroundDrawable;

    ListenerRegistration userListener;
    ListenerRegistration matchListener;
    ArrayList<ListenerRegistration> playerListeners = new ArrayList<ListenerRegistration>();

    @Override
    public void onStop() {
        super.onStop();
        if(userListener != null) {
            userListener.remove();
        }
        if(matchListener != null) {
            matchListener.remove();
        }
        for(ListenerRegistration listenerRegistration : playerListeners) {
            try {
                listenerRegistration.remove();
            } catch(Exception e) {

            }
        }
    }

    private void getUser(String userId ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userListener = db.collection("users")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            //Log.d(TAG, "Current data: " + snapshot.getData());
                            activeUser = snapshot.toObject(User.class);
                            handleDataUpdate();
                        } else {
                            //Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void fetchMatch(String matchId) {
        matchListener = Firestore.fetchMatchInFragment(matchId, this);
    }

    public User getActiveUser() {
        return activeUser;
    }

    private ArrayList<Player> getTeamPlayers() {
        ArrayList<Player> teamPlayers = new ArrayList<Player>();
        for(Player player : match.getPlayers()) {

            if(player.getTeam().equals(displayedTeam)) {
                teamPlayers.add(player);
            }
        }
        return teamPlayers;
    }

    @Override
    public void handleDataUpdate() {
        if(match != null) {
            if(ratePlayersAdapter == null) {
                setupRatePlayersAdapter();
            } else {
                ratePlayersAdapter.setPlayers( getTeamPlayers() );
                ratePlayersAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public void fetchUsers() {
        ArrayList<Player> players = this.match.getPlayers();
        for( Player player : players ) {
            ListenerRegistration listenerRegistration = fetchTheUser(player);
            playerListeners.add(listenerRegistration);
        }
    }

    @Override
    public void setPositionMap(HashMap<Integer, Player> positionMap) {
        //Nothing
    }

    @Override
    public void updateButtonVisibilities() {
        //Nothing (just for interface)
    }

    @Override
    public void handleMatchRemove() {
        try {
            NavHostFragment.findNavController(FragmentRatePlayers.this).navigateUp();
        } catch (Exception e) {

        }
    }

    private ListenerRegistration fetchTheUser( Player player ) {
        return Firestore.fetchTheUserInFragment(player, users, this );
    }


    public void setupRatePlayersAdapter() {
        ratePlayersAdapter = new RatePlayersAdapter(getTeamPlayers(), users, getContext(), playerRatingSelections, this);
        playersRecycler.setNestedScrollingEnabled(false);
        playersRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        playersRecycler.setLayoutManager(linearLayoutManager);
        playersRecycler.setAdapter(ratePlayersAdapter);
    }

    public void handleTeamAClickListener() {
        binding.teamAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayedTeam = Team.TEAM_A;
                teamAButton.setBackground( selectedBackgroundDrawable );
                teamBButton.setBackground( normalBackgroundDrawable );
                handleDataUpdate();
            }
        });
    }

    public void handleTeamBClickListener() {
        binding.teamBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayedTeam = Team.TEAM_B;
                teamBButton.setBackground( selectedBackgroundDrawable );
                teamAButton.setBackground( normalBackgroundDrawable );
                handleDataUpdate();
            }
        });
    }

    public void handleDoneButton() {
        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVotes();
                NavHostFragment.findNavController(FragmentRatePlayers.this).navigateUp();
            }
        });
    }

    private void saveVotes() {
        for(Player player : match.getPlayers()) {
            int selection = playerRatingSelections.getOrDefault(player, 0);
            if(selection == 0) {
                //no rating, so do nothing.
            } else {
                //it works for not attended because it's index 1
                int rating = selection - 1;
                Firestore.votePlayer(activeUser.getUserID(), player, rating);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRatePlayersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void updateButtonClicked() {
        if(displayedTeam == Team.TEAM_A) {
            teamAButton.setBackground( selectedBackgroundDrawable );
            teamBButton.setBackground( normalBackgroundDrawable );
            handleDataUpdate();
        } else {
            teamBButton.setBackground( selectedBackgroundDrawable );
            teamAButton.setBackground( normalBackgroundDrawable );
            handleDataUpdate();
        }

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doneButton = binding.doneButton;
        teamAButton = binding.teamAButton;
        teamBButton = binding.teamBButton;
        playersRecycler = binding.playersRecycler;

        selectedBackgroundDrawable = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.team_selected_background, null );
        normalBackgroundDrawable = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.team_button, null );

        updateButtonClicked();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        getUser(userId);

        Bundle matchBundle = getArguments();
        String matchId = matchBundle.getString("matchId");
        fetchMatch(matchId);

        handleTeamAClickListener();
        handleTeamBClickListener();
        handleDoneButton();
    }
}