package com.example.bilkenthalisahaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.databinding.FragmentMatchDisplayBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;

public class MatchDisplayFragment extends Fragment {

    private static final long DAY_AS_SECONDS = 24*60*60;

    private FragmentMatchDisplayBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Match> matches = new ArrayList<Match>();
    private ParentRecyclerAdapter adapter;
    private ArrayList<ChildRecyclerDataset> childRecyclerDatasets = new ArrayList<ChildRecyclerDataset>();
    private LinkedList<ListenerRegistration> listeners = new LinkedList<ListenerRegistration>();
    private long currentTime;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMatchDisplayBinding.inflate(inflater, container, false);

        Timestamp timestamp = Timestamp.now();
        currentTime = timestamp.getSeconds() - timestamp.getSeconds() % DAY_AS_SECONDS;

        return binding.getRoot();

    }

    private void setupParentAdapter(@NonNull View view) {

        adapter = new ParentRecyclerAdapter(childRecyclerDatasets);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //this should go here
        setupParentAdapter(view);

        binding.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebase refresh
                getNewDay();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        for( ListenerRegistration listener : listeners ) {
            listener.remove();
        }
    }


    public void getNewDay() {

        final long PAGE_LIMIT = DAY_AS_SECONDS; //day
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding.textView.setText(currentTime + "");

        ChildRecyclerDataset childRecyclerDataset = new ChildRecyclerDataset(currentTime);
        currentTime += PAGE_LIMIT;
        childRecyclerDatasets.add(childRecyclerDataset);
        adapter.notifyItemInserted( childRecyclerDatasets.size() - 1 );

    }

    public void getNewMatches() {

        final long PAGE_LIMIT = 3 * DAY_AS_SECONDS; //day
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Timestamp current = new Timestamp(currentTime, 0);;
        Timestamp limit = new Timestamp(currentTime + PAGE_LIMIT, 0);
        currentTime += PAGE_LIMIT;

        Query query;
        if (matches.isEmpty() ) {
            query = db.collection("matches")
                    .orderBy("time", Query.Direction.ASCENDING)
                    .startAt(current)
                    .endBefore(limit);
        } else {
            Match lastMatch = matches.get(matches.size() - 1);
            query = db.collection("matches")
                    .orderBy("time", Query.Direction.ASCENDING)
                    .startAt(current)
                    .endBefore(limit);
        }

        binding.textView.setText(currentTime + "");

        ListenerRegistration listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }
/*
                for (QueryDocumentSnapshot doc : value) {
                    if(doc!=null && doc.exists()) {
                        Match newMatch = doc.toObject(Match.class);

                        int indexOf = indexOfMatch(newMatch);
                        if (indexOf > -1 ) {
                            matches.set(indexOf, newMatch);
                            adapter.notifyItemChanged(indexOf);
                        } else {
                            int addTo = Math.abs(indexOf) - 1;
                            matches.add(addTo, newMatch);
                            adapter.notifyItemInserted(addTo);
                        }
                    }

                }
*/
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Match newMatch = dc.getDocument().toObject(Match.class);
                    int indexOf = indexOfMatch(newMatch);
                    switch (dc.getType()) {
                        case ADDED:
                            if(indexOf < 0) {
                                int addTo = Math.abs(indexOf) - 1;
                                matches.add(addTo, newMatch);
                                adapter.notifyItemInserted(addTo);
                            }
                            break;
                        case MODIFIED:
                            if(indexOf > - 1) {
                                matches.set(indexOf, newMatch);
                                adapter.notifyItemChanged(indexOf);
                            }
                            break;
                        case REMOVED:
                            if(indexOf > - 1) {
                                matches.remove(indexOf);
                                adapter.notifyItemRemoved(indexOf);
                            }
                            break;
                    }
                }

            }
        });
        listeners.add(listener);

    }

    public int indexOfMatch(Match match) {
        return Collections.binarySearch(matches, match);
    }

}