package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Match_RecyclerAdapter extends RecyclerView.Adapter<Match_RecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<MatchModel> matchModels;

    public Match_RecyclerAdapter(Context context, ArrayList<MatchModel> matchModels) {
        this.context = context;
        this.matchModels = matchModels;
    }

    @NonNull
    @Override
    public Match_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.match_item, parent, false);

        return new Match_RecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Match_RecyclerAdapter.MyViewHolder holder, int position) {
        holder.locationView.setText(matchModels.get(position).getLocation());
        holder.timeView.setText(matchModels.get(position).getTime());
        holder.capacityView.setText(matchModels.get(position).getCapacity());
    }

    @Override
    public int getItemCount() {
        return matchModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView locationView;
        TextView timeView;
        TextView capacityView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            locationView = itemView.findViewById(R.id.location);
            timeView = itemView.findViewById(R.id.time);
            capacityView = itemView.findViewById(R.id.capacity);
        }
    }
}
