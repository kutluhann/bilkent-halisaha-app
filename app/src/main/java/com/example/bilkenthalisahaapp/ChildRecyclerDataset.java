package com.example.bilkenthalisahaapp;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Calendar;

public class ChildRecyclerDataset {

    private long currentTime;
    private ListenerRegistration listenerRegistration;
    private ArrayList<Match> matchList = new ArrayList<Match>();
    private MatchesAdapter matchesAdapter;

     public ChildRecyclerDataset(long currentTime) {
         this.currentTime = currentTime;
     }

    public long getCurrentTime() {
        return currentTime;
    }

    public ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }

    public ArrayList<Match> getMatchList() {
        return matchList;
    }

    public MatchesAdapter getMatchesAdapter() {
        return matchesAdapter;
    }

    public void setMatchesAdapter(MatchesAdapter matchesAdapter) {
        this.matchesAdapter = matchesAdapter;
    }

    public void setListenerRegistration(ListenerRegistration listenerRegistration) {
        this.listenerRegistration = listenerRegistration;
    }
}
