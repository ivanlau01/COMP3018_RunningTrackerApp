package com.example.runningtracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.runningtracker.R;
import com.example.runningtracker.track.TrackData;
import com.example.runningtracker.adapter.TrackAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TrackingRecords extends AppCompatActivity {

    List<TrackData> trackDataList = new ArrayList<>();
    private TrackAdapter trackAdapter;

    Button shareButton;
    ImageView backButton, deleteButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_records);

        loadData();
        buildRecyclerView();
        insertData();
        saveData();

        //Allows user to return to RecordsInfo page
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingRecords.this, RecordsInfo.class);
                startActivity(intent);
            }
        });

        //Allows user to delete the data saved
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                finish();
                startActivity(getIntent());
                Toast.makeText(TrackingRecords.this, "Delete is pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        //Allows user to share their data to other apps
        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData();
            }
        });
    }
        //Allows user to save the data in json and SharedPreferences
        private void saveData() {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(trackDataList);
            editor.putString("track_data_list", json);
            editor.apply();
            Toast.makeText(this, "Track Data List is saved to SharedPreferences", Toast.LENGTH_SHORT).show();
        }

        //Allows user to load data when returning to TrackingRecords page
        private void loadData(){
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("track_data_list", null);
            Type type = new TypeToken<List<TrackData>>(){}.getType();
            trackDataList = gson.fromJson(json, type);

            if(trackDataList == null){
                trackDataList = new ArrayList<>();
            }
        }

        //Create a recycler view to store large amount of similar data items
        private void buildRecyclerView(){
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //Create an instance of your custom adapter
            trackAdapter = new TrackAdapter(trackDataList);

            //Set the adapter on the Recycler view
            recyclerView.setLayoutManager(recyclerView.getLayoutManager());
            recyclerView.setAdapter(trackAdapter);
        }

        //Retrieve data from RecordsInfo and arrange it in trackDataList
        private void insertData() {
            Intent records = getIntent();
            String date = records.getStringExtra("date");
            String username = records.getStringExtra("username");
            String height = records.getStringExtra("height");
            String weight = records.getStringExtra("weight");
            String distance = records.getStringExtra("distance");
            String time = records.getStringExtra("time");
            String avgSpeed = records.getStringExtra("avgspeed");
            String maxSpeed = records.getStringExtra("maxspeed");
            String comment = records.getStringExtra("comment");

            trackDataList.add(new TrackData(date, username, height, weight, distance, time, avgSpeed, maxSpeed, comment));
            trackAdapter.notifyItemInserted(trackDataList.size());
        }

        //Allows user to share their data out to other apps
        //In the other app, user must retrieve these data by adding String string1 = getIntent().getStringExtra("string1_key"); in the other app.
        private void shareData(){
            Intent records = getIntent();
            String date = records.getStringExtra("date");
            String username = records.getStringExtra("username");
            String height = records.getStringExtra("height");
            String weight = records.getStringExtra("weight");
            String distance = records.getStringExtra("distance");
            String time = records.getStringExtra("time");
            String avgSpeed = records.getStringExtra("avgspeed");
            String maxSpeed = records.getStringExtra("maxspeed");
            String comment = records.getStringExtra("comment");

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Date: " + date);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Username: " + username);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Height: " + height);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Weight: " + weight);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Distance: " + distance);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Time: " + time);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Average Speed: " + avgSpeed);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Max Speed: " + maxSpeed);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Comment: " + comment);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        //Delete the data from SharedPreferences
        private void deleteData(){
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();
        }

}