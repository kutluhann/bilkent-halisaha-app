package com.example.bilkenthalisahaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.databinding.FragmentAddMatchBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentMatchDisplayBinding;
import com.example.bilkenthalisahaapp.databinding.FragmentSecondBinding;
import com.google.firebase.Timestamp;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;



public class AddMatchFragment extends Fragment {

    private FragmentAddMatchBinding binding;
    private int mYear, mMonth, mDay, mHour, mMinute;

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

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String locationName = binding.editLocationName.getText().toString();
                int teamSize = Integer.parseInt( binding.editTextTeamSize.getText().toString() );
                int maxTeamSize = Integer.parseInt( binding.editTextMaxTeamSize.getText().toString() );
                Calendar cal = Calendar.getInstance();

                //binding.textViewDate.setText(cal.getTime().toString() + " " + cal.getTimeZone());
                cal.set(mYear, mMonth, mDay, mHour, mMinute);
                cal.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul" ));
                //binding.textViewTime.setText(cal.getTime().toString() + " " + cal.getTimeZone());
                Timestamp timestamp = new Timestamp( cal.getTime() );


                Match newMatch = new Match(locationName, timestamp, teamSize, maxTeamSize);

                Firestore.updateMatch(newMatch);

            }
        });

                binding.selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);


                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        binding.textViewDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });

        binding.selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                binding.textViewTime.setText(hourOfDay + ":" + minute);
                                mHour = hourOfDay;
                                mMinute = minute;
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
