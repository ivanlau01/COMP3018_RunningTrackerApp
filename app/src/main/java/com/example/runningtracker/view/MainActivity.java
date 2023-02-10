package com.example.runningtracker.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.runningtracker.service.LocationService;
import com.example.runningtracker.R;

public class MainActivity extends AppCompatActivity {

    private LocationService mService;
    public Button startActivity, userRecord;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestForLocationPermission();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE) ;

        //Create an intent to RecordsInfo activity after pressing the button
        startActivity = findViewById(R.id.startActivityButton);
        startActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    RequestForLocationPermission();
                    Intent intent = new Intent(MainActivity.this, RecordsInfo.class);
                    startActivity(intent);
                } else{
                    showAlertMessageLocationDisabled();
                }

            }
        });

        //Create an intent to InfoRecord activity after pressing the button
        userRecord = findViewById(R.id.userInfoButton);
        userRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoRecord = new Intent(MainActivity.this, UserInfo.class);
                startActivity(infoRecord);
            }
        });
        //Initialise Launcher for activity

    }

    private void RequestForLocationPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission is granted
            }
            else{
                //When permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},32);
            }
        }

    //Create an alert message if the user off their location
    private void showAlertMessageLocationDisabled(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device Location is turned off, do you want to turn on location?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    }




