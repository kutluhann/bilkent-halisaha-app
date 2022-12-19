package com.example.bilkenthalisahaapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.appObjects.Player;
import com.example.bilkenthalisahaapp.appObjects.Team;
import com.example.bilkenthalisahaapp.appObjects.User;
import com.example.bilkenthalisahaapp.databinding.FragmentAddMatchBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class AddMatch extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentAddMatchBinding binding;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private User user;

    private String location, time, playerCount, position;

    final Calendar c = Calendar.getInstance();



    private void getCurrentUser() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

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
                            user = snapshot.toObject(User.class);
                        } else {
                            //Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private int getPositionInfo() {
        return binding.positionSpinner.getSelectedItemPosition() + 1;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddMatchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        c.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        // Set the text of datePicket to current date
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        c.set(mYear, mMonth, mDay, 0, 0);

        initializeSpinnerAdapters();

        getCurrentUser();

        binding.datePicker.setText(getFormattedDate(mDay, mMonth + 1, mYear));

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Object hourAsObject = binding.timeSpinner.getSelectedItem();
                if (hourAsObject != null) {
                    String hourAsString = (String) hourAsObject;
                    hourAsString = hourAsString.substring(0,5);
                    Calendar cal = Calendar.getInstance();

                    int indexOfDot = hourAsString.indexOf(".");
                    int hour = Integer.parseInt(hourAsString.substring(0, indexOfDot));

                    cal.set(mYear, mMonth, mDay, hour, 0, 0);
                    cal.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
                    Timestamp timestamp = new Timestamp( cal.getTime() );

                    int totalPlayerCount = Integer.parseInt(playerCount) * 2;

                    Match newMatch = new Match(location, timestamp, totalPlayerCount);

                    int position = getPositionInfo();
                    Player player = new Player(user.getUserID(), getPositionInfo(), newMatch.getMatchId(), Team.TEAM_A, true);
                    newMatch.addLocalPlayer(player);

                    Firestore.updateMatch(newMatch);

                    Toast.makeText(getContext(), "Match is created successfully", Toast.LENGTH_SHORT).show();

                    Bundle matchBundle = new Bundle();
                    matchBundle.putString("matchId", newMatch.getMatchId() );

                    NavHostFragment.findNavController(AddMatch.this)
                            .navigateUp();

                    NavHostFragment.findNavController(AddMatch.this)
                            .navigate(R.id.action_global_match_info, matchBundle);
                } else {
                    Toast.makeText(getContext(), "Choose an appropriate time", Toast.LENGTH_SHORT).show();
                }
            }
            });

            binding.datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                binding.datePicker.setText(getFormattedDate(dayOfMonth, monthOfYear + 1, year));
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                c.set(mYear, mMonth, mDay);

                                String stadiumName = location;
                                Firestore.refreshAvailableHours(c, stadiumName, getThis() );
                            }
                        }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
    }

    private AddMatch getThis() {
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    public void handleAvailableTimesChange(ArrayList<String> availableTimes) {

        ArrayAdapter<String> timeAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, availableTimes);
        timeAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timeSpinner.setAdapter(timeAA);
        binding.timeSpinner.setOnItemSelectedListener(this);
    }

    private void initializeSpinnerAdapters() {
        // Pitch
        ArrayAdapter<CharSequence> pitchAA = ArrayAdapter.createFromResource(getContext(),
                R.array.locations_array, android.R.layout.simple_spinner_item);
        pitchAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.locationSpinner.setAdapter(pitchAA);
        binding.locationSpinner.setOnItemSelectedListener(this);

        // Time
        //adapter
        /*
        ArrayList<String> availableTimes = CommonMethods.getAllHours();
        ArrayAdapter<String> timeAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, availableTimes);
        timeAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timeSpinner.setAdapter(timeAA);
        */

        binding.timeSpinner.setOnItemSelectedListener(this);

        // Position
        ArrayAdapter<CharSequence> playerCountAA = ArrayAdapter.createFromResource(getContext(),
                R.array.number_of_players, android.R.layout.simple_spinner_item);
        playerCountAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.playerCountSpinner.setAdapter(playerCountAA);
        binding.playerCountSpinner.setOnItemSelectedListener(this);
        // Position
        ArrayAdapter<CharSequence> positionAA = ArrayAdapter.createFromResource(getContext(),
                R.array.positions_array, android.R.layout.simple_spinner_item);
        positionAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.positionSpinner.setAdapter(positionAA);
        binding.playerCountSpinner.setOnItemSelectedListener(this);
    }

    private String getFormattedNumber(int number) {
        String str;
        if (number < 10) {
            str = "0" + number;
        } else {
            str = "" + number;
        }

        return str;
    }

    private String getFormattedDate(int day, int month, int year) {
        return getFormattedNumber(day) + "." + getFormattedNumber(month) + "." + getFormattedNumber(year);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.locationSpinner:
                location = parent.getSelectedItem().toString();
                Firestore.refreshAvailableHours(c, location, getThis() );
                break;
            case R.id.timeSpinner:
                time = parent.getSelectedItem().toString();
                break;
            case R.id.playerCountSpinner:
                playerCount = parent.getSelectedItem().toString();
                break;
            case R.id.positionSpinner:
                position = parent.getSelectedItem().toString();
                break;
        }
        Object item =  binding.timeSpinner.getSelectedItem();
        item = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
