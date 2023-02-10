package com.example.runningtracker.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.runningtracker.R;
import com.example.runningtracker.track.TrackData;
import com.example.runningtracker.view.TrackingRecords;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder>{

    TrackingRecords context;

    private final List<TrackData> trackDataList;

    public TrackAdapter(List<TrackData> trackDataList) {
        this.trackDataList = trackDataList;
    }

    //Create a new ViewHolder object to display trackDatalist
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.track_records_list, viewGroup, false);
        return new ViewHolder(view);
    }

    //Display trackDataList in RecyclerView
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TrackData trackData = trackDataList.get(i);
        viewHolder.savedDate.setText("Date: " + trackData.getDate());
        viewHolder.savedUsername.setText("Username: " + trackData.getUsername());
        viewHolder.savedHeight.setText("Height: " + trackData.getHeight());
        viewHolder.savedWeight.setText("Weight: " + trackData.getWeight());
        viewHolder.savedDistance.setText("Total Distance: " + trackData.getDistance());
        viewHolder.savedTime.setText("Time: " + trackData.getTime());
        viewHolder.savedAvgSpeed.setText("Average Speed: " + trackData.getAvgSpeed());
        viewHolder.savedMaxSpeed.setText("Max Speed: " + trackData.getMaxSpeed());
        viewHolder.savedComment.setText("Comment: " + trackData.getComment());
    }

    @Override
    public int getItemCount() {
        return trackDataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView savedDate;
        TextView savedDistance;
        TextView savedTime;
        TextView savedAvgSpeed;
        TextView savedMaxSpeed;
        TextView savedComment;
        TextView savedUsername;
        TextView savedHeight;
        TextView savedWeight;

        //Initialize the views in the layout of the recyclerView item
        public ViewHolder(View itemView) {
            super(itemView);
             savedDate = itemView.findViewById(R.id.trackDate);
             savedDistance = itemView.findViewById(R.id.trackDistance);
             savedTime = itemView.findViewById(R.id.trackTime);
             savedAvgSpeed = itemView.findViewById(R.id.trackAvgSpeed);
             savedMaxSpeed = itemView.findViewById(R.id.trackMaxSpeed);
             savedComment = itemView.findViewById(R.id.trackComment);
             savedUsername = itemView.findViewById(R.id.trackUsername);
             savedHeight = itemView.findViewById(R.id.trackHeight);
             savedWeight = itemView.findViewById(R.id.trackWeight);
        }
    }
}
