package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.databinding.FragmentMatchDisplayBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.LinkedList;

public class MatchDisplay extends Fragment {

    private static final long DAY_AS_SECONDS = 24*60*60;

    private FragmentMatchDisplayBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Match> matches = new ArrayList<Match>();
    private ArrayList<ChildRecyclerDataset> childRecyclerDatasets = new ArrayList<ChildRecyclerDataset>();
    private ParentRecyclerAdapter adapter = new ParentRecyclerAdapter(childRecyclerDatasets);
    private LinkedList<ListenerRegistration> listeners = new LinkedList<ListenerRegistration>();
    private long currentTime;
    public final Fragment matchDisplayFragment = MatchDisplay.this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initial methods
        Timestamp timestamp = Timestamp.now();
        currentTime = timestamp.getSeconds() - timestamp.getSeconds() % DAY_AS_SECONDS;

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMatchDisplayBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    private void setupParentAdapter(@NonNull View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupParentAdapter(view);

        //initial
        if(childRecyclerDatasets.size() == 0) {
            final int INITIAL_FETCH_COUNT = 2;
            getNewDays(INITIAL_FETCH_COUNT);
        }

            binding.refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //firebase refresh
                    final int FETCH_DAYS_AMOUNT = 3;
                    getNewDays(FETCH_DAYS_AMOUNT);
                }
            });

            binding.createMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(MatchDisplay.this)
                            .navigate(R.id.action_matchDisplay_to_addMatch);
                }
            });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //stop listeners
        /*
        for( ListenerRegistration listener : listeners ) {
            listener.remove();
        }
        */

    }

    //when totally closed
    @Override
    public void onDestroy() {
        super.onDestroy();
/*
        //stop listeners
        //causing cache problems
        for( ChildRecyclerDataset childRecyclerDataset : childRecyclerDatasets ) {
            ListenerRegistration listenerRegistration = childRecyclerDataset.getListenerRegistration();
            listenerRegistration.remove();
        }
        */

    }

    private void getNewDays(int count) {
        for(int i = 0; i < count; i++) {
            getNewDay();
        }
    }

    private void getNewDay() {

        final long PAGE_LIMIT = DAY_AS_SECONDS; //day
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //binding.textView.setText(currentTime + "");

        ChildRecyclerDataset childRecyclerDataset = new ChildRecyclerDataset(currentTime);
        currentTime += PAGE_LIMIT;
        childRecyclerDatasets.add(childRecyclerDataset);
        adapter.notifyItemInserted( childRecyclerDatasets.size() - 1 );

    }

}