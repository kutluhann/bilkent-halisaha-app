package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class ParentRecyclerAdapter extends RecyclerView.Adapter<ParentRecyclerAdapter.ViewHolder> {

    private ArrayList<ChildRecyclerDataset> localDataSet;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView childRecycler;
        private final TextView dayView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            childRecycler = (RecyclerView) view.findViewById(R.id.childRecycler);
            dayView = (TextView) view.findViewById(R.id.dayTextView);



        }

        public RecyclerView getChildRecycler() {
            return childRecycler;
        }

        public TextView getDayView() {
            return dayView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ParentRecyclerAdapter(ArrayList<ChildRecyclerDataset> dataSet) {

        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.parent_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        ChildRecyclerDataset currentDataset = localDataSet.get(position);
        viewHolder.getDayView().setText( currentDataset.getCurrentTime()+ "" + "position: "  + position );

        setupChildAdapter(viewHolder, position);
        getDateMatches(position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    private void setupChildAdapter(ViewHolder viewHolder, int index) {

        ChildRecyclerDataset currentDataset = localDataSet.get(index);
        RecyclerView currentRecyclerView = viewHolder.getChildRecycler();
        MatchesAdapter adapter = new MatchesAdapter(currentDataset.getMatchList());

        currentRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( context );
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        currentRecyclerView.setLayoutManager(linearLayoutManager);
        currentRecyclerView.setAdapter(adapter);

        currentDataset.setMatchesAdapter(adapter);
    }

    private void getDateMatches(int index) {
        ChildRecyclerDataset currentDataset = localDataSet.get(index);
        ArrayList<Match> matches = currentDataset.getMatchList();
        RecyclerView.Adapter adapter = currentDataset.getMatchesAdapter();

        final long DAY_AS_SECONDS = 24*60*60;
        long indexTime = currentDataset.getCurrentTime();
        Timestamp indexTimestamp = new Timestamp(indexTime, 0);
        long limit = indexTime + DAY_AS_SECONDS;
        Timestamp limitTimestamp = new Timestamp(limit, 0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query;
            query = db.collection("matches")
                    .orderBy("time", Query.Direction.ASCENDING)
                    .startAt(indexTimestamp)
                    .endBefore(limitTimestamp);


        ListenerRegistration listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    Match newMatch = dc.getDocument().toObject(Match.class);
                    int indexOf = Collections.binarySearch(matches, newMatch);
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
        currentDataset.setListenerRegistration(listener);

    }

}
