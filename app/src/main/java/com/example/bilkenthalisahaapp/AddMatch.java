package com.example.bilkenthalisahaapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.CommonMethods;
import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.databinding.FragmentAddMatchBinding;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class AddMatch extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentAddMatchBinding binding;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private String location, time, playerCount, position;

    final Calendar c = Calendar.getInstance();


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

        initializeSpinnerAdapters();

        // Set the text of datePicket to current date
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        binding.datePicker.setText(getFormattedDate(mDay, mMonth + 1, mYear));

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();

                int indexOfDot = time.indexOf(".");
                int hour = Integer.parseInt(time.substring(0, indexOfDot));

                cal.set(mYear, mMonth, mDay, hour, 0, 0);
                cal.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul" ));
                Timestamp timestamp = new Timestamp( cal.getTime() );

                int totalPlayerCount = Integer.parseInt(playerCount) * 2;

                Match newMatch = new Match(location, timestamp, 0, totalPlayerCount);

                Firestore.updateMatch(newMatch);

                Toast.makeText(getContext(),"Match is created successfully", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(AddMatch.this)
                        .navigate(R.id.matches_navigation);
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

                                String stadiumName = location;
                                Firestore.refreshAvailableHours(mDay, mMonth + 1, mYear, stadiumName, getThis() );
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
                Firestore.refreshAvailableHours(mDay, mMonth + 1, mYear, location, getThis() );
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
