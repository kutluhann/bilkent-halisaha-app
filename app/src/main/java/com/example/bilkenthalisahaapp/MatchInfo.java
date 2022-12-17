package com.example.bilkenthalisahaapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.*;
import com.example.bilkenthalisahaapp.appObjects.weatherObjects.Hour;
import com.example.bilkenthalisahaapp.databinding.FragmentFormationBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.bilkenthalisahaapp.interfaces.MatchUpdateHandleable;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MatchInfo extends Fragment implements MatchUpdateHandleable {
    private FragmentFormationBinding binding;
    private Button cancelButton;

    private User activeUser;

    private Match match;
    private HashMap<Player, User> users = new HashMap<Player, User>();
    //minus positions are teamB and plus positive positions are teamA, like 1,3 -2.
    private HashMap<Integer, Player> positionMap = new HashMap<Integer, Player>();
    private Team displayedTeam = Team.TEAM_A;

    private Drawable selectedBackgroundDrawable;
    private Drawable normalBackgroundDrawable;

    private void getUser( String userId ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
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

    public void updateButtonVisibilities() {
        Player activePlayer = getPlayerOfActiveUser();
        if(activePlayer != null) {
            //if match is passed
            if( !CommonMethods.isMatchPassed(match) ) {
                if(activePlayer.isOwner()) {
                    binding.cancelMatchButton.setVisibility(View.VISIBLE);
                    binding.leaveMatchButton.setVisibility(View.GONE);
                    binding.ratePlayersButton.setVisibility(View.GONE);
                } else {
                    binding.cancelMatchButton.setVisibility(View.GONE);
                    binding.leaveMatchButton.setVisibility(View.VISIBLE);
                    binding.ratePlayersButton.setVisibility(View.GONE);
                }
            } else {
                binding.cancelMatchButton.setVisibility(View.GONE);
                binding.leaveMatchButton.setVisibility(View.GONE);
                binding.ratePlayersButton.setVisibility(View.VISIBLE);
            }
        } else {
            binding.cancelMatchButton.setVisibility(View.GONE);
            binding.leaveMatchButton.setVisibility(View.GONE);
            binding.ratePlayersButton.setVisibility(View.GONE);
        }
    }

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



    public int getDefaultDrawableOfPosition( int position ) {
        //position is in range of 1-6 or -1-(-6)
        position = Math.abs(position);
        if( position == 1 ) {
            return R.drawable.goalkeeper;
        } else if(position == 2) {
            return R.drawable.defender1;
        } else if(position == 3) {
            return R.drawable.defender2;
        } else if(position == 4) {
            return R.drawable.midfielder;
        } else if(position == 5) {
            return R.drawable.forward1;
        } else if(position == 6) {
            return R.drawable.forward2;
        } else {
            return R.drawable.default_profile_photo;
        }
    }

    //position is in range of 1-6
    private Player getPlayerOfPosition(int position) {
        position = Math.abs(position);
        if(displayedTeam == Team.TEAM_B) {
            position = -position;
        }
        Player player = positionMap.get(position);
        return player;
    }

    private void kickPlayerFromMatch(Player player) {
        Firestore.removePlayerFromMatch(player, match);
    }

    @Override
    public void handleDataUpdate() {
        //refreshes the page according to new data or available data

        if(match != null) {
            //handle match data update

            binding.pitchInfo.setText(match.getLocation());

            String matchSummary = String.format("%s / %s", getDateString(), getTimeString());
            binding.timeAndDateInfo.setText(matchSummary);

            ForecastAPI forecastAPI = ForecastAPI.getInstance();
            Hour hour = forecastAPI.getHour(match.getTime().getSeconds());
            if (hour != null) {
                binding.weatherInfo.setText(hour.getCondition().getText() + " (" + hour.getTemp_c() + " °C)");
            } else {
                binding.weatherInfo.setText("No weather data");
            }

            ArrayList<Player> players = match.getPlayers();

            for(int position = 1; position <= match.getMaxTeamSize() / 2; position++) {
                Player player = getPlayerOfPosition(position);
                ShapeableImageView playerBox = getPositionImageView(position);
                if(player != null) {
                    //change with profile navigation
                    User user = users.get(player);

                    Player activeUsersPlayer = getPlayerOfActiveUser();

                    if(user != null) {
                        FirebaseStorageMethods.showImage( getContext(), playerBox, user.getProfilePictureURL(), getDefaultDrawableOfPosition(position) );
                        //TO-DO
                        //Add navigate to profile fragment



                        //TO-DO
                        //Add long click listener to remove player, first open a dialog box with an inner class
                        if( activeUsersPlayer != null && !CommonMethods.isMatchPassed(match)
                                && activeUsersPlayer.isOwner() && !activeUsersPlayer.equals(player) ) {
                            playerBox.setLongClickable(true);
                            playerBox.setOnLongClickListener(new View.OnLongClickListener() {
                                //TO-DO dialog box
                                @Override
                                public boolean onLongClick(View view) {
                                    User kickedUser = user;
                                    Player kickedPlayer = player;
                                    kickPlayerFromMatch(kickedPlayer);
                                    return true;
                                }
                            });

                        } else {
                            playerBox.setLongClickable(false);
                        }
                    } else {
                        FirebaseStorageMethods.showImage( getContext(), playerBox, null, getDefaultDrawableOfPosition(position) );
                    }
                } else {
                    FirebaseStorageMethods.showImage( getContext(), playerBox, null, getDefaultDrawableOfPosition(position) );
                    //if he is already in match first remove and after that add
                    //is it working correct, if it is already in not array the remove function?
                    int finalPosition = position;
                    Player oldPlayer = getPlayerOfActiveUser();
                    playerBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(oldPlayer == null) {
                                Player newPlayer = new Player(activeUser.getUserID(), finalPosition, match.getMatchId(), displayedTeam, false );
                                Firestore.addPlayerToMatch(newPlayer, match);
                            } else {
                                Player newPlayer = new Player(activeUser.getUserID(), finalPosition, match.getMatchId(), displayedTeam, oldPlayer.isOwner() );
                                Firestore.changePositionOfPlayer(oldPlayer, newPlayer, match);
                            }

                        }
                    });
                }
            }
        }

    }

    private Player getPlayerOfActiveUser() {
        try {
            Player tempPlayer = new Player(activeUser.getUserID(), 0, match.getMatchId(), displayedTeam, false);
            int index = match.getPlayers().indexOf(tempPlayer);
            if (index > -1) {
                //already in match
                return match.getPlayers().get(index);

            } else {
                //not in match
                return null;
            }
        } catch (Exception e) {
            return  null;
        }
    }


    @Override
    public void setMatch(Match match) {
        this.match = match;
    }

    public void setPositionMap(HashMap<Integer, Player> positionMap) {
        this.positionMap = positionMap;
    }

    private void cancelMatch() {
        NavHostFragment.findNavController(MatchInfo.this).navigateUp();
        Firestore.removeMatch(match);
    }

    private void leaveMatch() {
        Firestore.removePlayerFromMatch( getPlayerOfActiveUser(), match );
    }

    private void handleRatePlayersButtonClicked() {
        //TO-DO
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

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        getUser(userId);

        Bundle matchBundle = getArguments();
        String matchId = matchBundle.getString("matchId");
        fetchMatch(matchId);
        //can change the color
        //it can work more beautiful with color filter or animation or both.
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


        binding.leaveMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               leaveMatch();
            }
        });

        binding.ratePlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRatePlayersButtonClicked();
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
                cancelMatch();
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

    private ShapeableImageView getPositionImageView(int position) {

        position = Math.abs(position);
            if(position == 1) {
                return binding.player1;
            }else if (position == 2) {
                return binding.player2;
            }else if (position == 3) {
                return binding.player3;
            }else if (position == 4) {
                return binding.player4;
            }else if (position == 5) {
                return binding.player5;
            }else if(position== 6) {
                return binding.player6;
            } else {
                return null;
            }

    }


}
