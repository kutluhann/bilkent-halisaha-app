package com.example.bilkenthalisahaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Day_RecyclerAdapter extends RecyclerView.Adapter<Day_RecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<DayModel> dayModels;

    public Day_RecyclerAdapter(Context context, ArrayList<DayModel> dayModels) {
        this.context = context;
        this.dayModels = dayModels;

    }

    @NonNull
    @Override
    public Day_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.day_item, parent, false);

        return new Day_RecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Day_RecyclerAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(dayModels.get(position).getDate());

        Match_RecyclerAdapter adapter = new Match_RecyclerAdapter(context, dayModels.get(position).getMatchModels());
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return dayModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.date);
            recyclerView = itemView.findViewById(R.id.matchRecyclerView);
        }
    }

}
