package com.example.runningtracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runningtracker.service.LocationService;
import com.example.runningtracker.R;
import com.example.runningtracker.track.TrackData;

import java.util.ArrayList;
import java.util.List;

public class RecordsInfo extends AppCompatActivity {

    private LocationService mService;
    private TextView dateSaved, totalDistance, averageSpeed, maximumSpeed, timeElapsed;
    private BroadcastReceiver mReceiver;
    Button saveButton;
    ImageView startButton, stopButt;
    TextView addComments;

    //Array list for TrackData
    List<TrackData> trackDataList = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_info);

        loadText();

        totalDistance = findViewById(R.id.totalDistanceSaved);
        timeElapsed = findViewById(R.id.timeElapsed);
        dateSaved = findViewById(R.id.dateSaved);
        averageSpeed = findViewById(R.id.averageSpeedSaved);
        maximumSpeed = findViewById(R.id.maxSpeedSaved);

        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.broad.LOAD_URL");
        registerReceiver(mReceiver, filter);

        //Start foreground service after pressing this button
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver(mReceiver, filter);
                startButton.setEnabled(false);
                stopButt.setEnabled(true);
                saveButton.setEnabled(false);
                totalDistance.setVisibility(View.VISIBLE);
                timeElapsed.setVisibility(View.VISIBLE);
                averageSpeed.setVisibility(View.VISIBLE);
                maximumSpeed.setVisibility(View.VISIBLE);
                Intent start = new Intent(RecordsInfo.this, LocationService.class);
                ContextCompat.startForegroundService(RecordsInfo.this, start);
                Toast.makeText(RecordsInfo.this, "Service is started", Toast.LENGTH_SHORT).show();
            }
        });

        //Stop foreground service after pressing this button
        stopButt = findViewById(R.id.stopButton);
        stopButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(mReceiver);
                stopButt.setEnabled(false);
                startButton.setEnabled(true);
                saveButton.setEnabled(true);
                Intent stop = new Intent(RecordsInfo.this, LocationService.class);
                LocationService.mTimerTask.cancel();
                stopService(stop);
                Toast.makeText(RecordsInfo.this, "Service is stopped", Toast.LENGTH_SHORT).show();
            }
        });

        //Open TrackingRecords page after clicking this button
        saveButton = findViewById(R.id.trackRecords);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordsInfo.this, TrackingRecords.class);
                startActivity(intent);
                openRecords();
            }
        });

        //Allow user to enter their goals or comment in it
        addComments = findViewById(R.id.addComment);
        if(addComments != null) {
            addComments.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Text will be saved after the user enter it
                        saveText();
                        Toast.makeText(RecordsInfo.this, "Text shared to SharedPreferences", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    //Receive data from broadcast service in service class
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {

            String date = intent.getStringExtra("date");
            String dist = intent.getStringExtra("distance");
            String time = intent.getStringExtra("time");
            String avgSpeed = intent.getStringExtra("avgspeed");
            String maxSpeed = intent.getStringExtra("maxspeed");
            dateSaved.setText(date);
            totalDistance.setText(dist + "KM");
            timeElapsed.setText(time);
            averageSpeed.setText(avgSpeed + "KM/H");
            maximumSpeed.setText(maxSpeed + "KM/H");
        }
    }

    //Save text after user entering it
    private void saveText() {
        String comment = addComments.getText().toString();
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_text", comment);
        editor.apply();
        Toast.makeText(this, "Comment / Goal is saved!", Toast.LENGTH_SHORT).show();
    }

    //Load text to display it
    private void loadText(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String savedText = sharedPreferences.getString("saved_text","");
        if(addComments != null) {
            addComments.setText(savedText);
        }
    }

    //Allows user to resume activity after clicking the notification
    @Override
    protected void onResume(){
        super.onResume();
        if(getIntent().hasExtra(LocationService.NOTIFICATION_TIME)) {
            String time = getIntent().getStringExtra(LocationService.NOTIFICATION_TIME);
            timeElapsed.setText(time);
            timeElapsed.setVisibility(View.VISIBLE);
        }
        if(getIntent().hasExtra(LocationService.NOTIFICATION_DISTANCE)) {
            String distanceNotification = getIntent().getStringExtra(LocationService.NOTIFICATION_DISTANCE);
            totalDistance.setText(distanceNotification);
            totalDistance.setVisibility(View.VISIBLE);
        }
        if(getIntent().hasExtra(LocationService.NOTIFICATION_AVGSPEED)) {
            String avgSpeed = getIntent().getStringExtra(LocationService.NOTIFICATION_AVGSPEED);
            averageSpeed.setText(avgSpeed);
            averageSpeed.setVisibility(View.VISIBLE);
        }
        if(getIntent().hasExtra(LocationService.NOTIFICATION_MAXSPEED)) {
            String maxSpeed = getIntent().getStringExtra(LocationService.NOTIFICATION_MAXSPEED);
            maximumSpeed.setText(maxSpeed);
            maximumSpeed.setVisibility(View.VISIBLE);
        }
    }

    //Pass the data to TrackingRecords in String format
    public void openRecords(){
        Intent userInfo = getIntent();
        String username = userInfo.getStringExtra("passUsername");
        String height = userInfo.getStringExtra("passHeight");
        String weight = userInfo.getStringExtra("passWeight");

        String passDate = dateSaved.getText().toString();
        String passTime = timeElapsed.getText().toString();
        String passDist = totalDistance.getText().toString();
        String passAvgSpeed = averageSpeed.getText().toString();
        String passMaxSpeed = maximumSpeed.getText().toString();
        String passComment = addComments.getText().toString();

        Intent records = new Intent(RecordsInfo.this, TrackingRecords.class);
        records.putExtra("date", passDate);
        records.putExtra("time",passTime);
        records.putExtra("distance", passDist);
        records.putExtra("avgspeed", passAvgSpeed);
        records.putExtra("maxspeed",passMaxSpeed);
        records.putExtra("comment", passComment);
        records.putExtra("username", username);
        records.putExtra("height", height);
        records.putExtra("weight", weight);
        startActivity(records);
    }


    //Destroy the activity and stop the service
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}