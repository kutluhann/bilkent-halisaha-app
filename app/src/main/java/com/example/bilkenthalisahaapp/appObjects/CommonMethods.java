package com.example.bilkenthalisahaapp.appObjects;

import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;

public class CommonMethods {

    public static final ZoneId ISTANBUL_ZONE_ID = ZoneId.of( "Europe/Istanbul" ) ;
    public static final long ONE_DAY_AS_SECONDS = 60*60*24;

    public static String addZerosToLength(String text, int length ) {
        while( text.length() < length ) {
            text = "0" + text;
        }
        return text;
    }

    public static String generateTimeString(long currentTime) {
        long matchEpochTime = currentTime;
        //LocalDateTime matchDateTime = LocalDateTime.ofEpochSecond( matchEpochTime, 0, ZoneOffset.UTC);
        Instant matchInstant = Instant.ofEpochSecond( matchEpochTime );
        int matchHour = matchInstant.atZone( ISTANBUL_ZONE_ID ).get( ChronoField.HOUR_OF_DAY );
        String hourText = CommonMethods.addZerosToLength(matchHour + "", 2);
        int matchMinute = matchInstant.atZone( ISTANBUL_ZONE_ID ).get( ChronoField.MINUTE_OF_HOUR );;
        String minuteText = CommonMethods.addZerosToLength(matchMinute + "", 2);
        String formattedCurrentTime = hourText + "." + minuteText;
        return formattedCurrentTime;
    }

    public static String generateOClockHour( int hour ) {
        String formattedText;
        formattedText = addZerosToLength( hour + "", 2 ) + ".00";
        return formattedText;
    }

    public static ArrayList<String> getAllHours() {

        ArrayList<String> times = new ArrayList<String>();
        String time;
        final int START_HOUR = 7;
        final int END_HOUR = 3; //not inclusive

        for (int i = START_HOUR; i % 24 != END_HOUR; i++) {
            int hour = i % 24;
            if (hour < 10) {
                time = "0" + hour + ".00";
            } else {
                time = hour + ".00";
            }
            times.add(time);
        }

        Collections.sort(times);

        return times;
    }

    public static ArrayList<String> getHoursUntilDayEnd(int start) {

        ArrayList<String> times = new ArrayList<String>();
        String time;

        final int END_HOUR = 0;

        for (int i = start; i % 24 != END_HOUR; i++) {
            int hour = i % 24;
            if (hour < 10) {
                time = "0" + hour + ".00";
            } else {
                time = hour + ".00";
            }
            times.add(time);
        }

        Collections.sort(times);

        ArrayList<String> allHours = getAllHours();
        for( int i = 0; i < times.size(); i++) {
            String hour = times.get(i);
            if( allHours.contains(hour) == false ) {
                times.remove(hour);
                i--;
            }
        }

        return times;
    }



}
