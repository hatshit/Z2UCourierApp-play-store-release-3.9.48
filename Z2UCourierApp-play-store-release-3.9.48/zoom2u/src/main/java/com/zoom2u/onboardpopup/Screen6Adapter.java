package com.zoom2u.onboardpopup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;

import java.util.ArrayList;

public class Screen6Adapter extends RecyclerView.Adapter<Screen6Adapter.RecyclerViewHolder> {

    private ArrayList<String> courseDataArrayList;
    private Context mcontext;

    public Screen6Adapter(ArrayList<String> recyclerDataArrayList, Context mcontext) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screen_6_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        String recyclerData = courseDataArrayList.get(position);
        holder.courseTV.setText(recyclerData);
    }

    @Override
    public int getItemCount() {
        return courseDataArrayList.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView courseTV;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTV = itemView.findViewById(R.id.text);

        }
    }
}