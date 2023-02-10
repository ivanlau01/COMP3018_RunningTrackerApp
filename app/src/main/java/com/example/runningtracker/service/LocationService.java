package com.example.runningtracker.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.runningtracker.R;
import com.example.runningtracker.view.RecordsInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    //Name all the variables
    private static final String CHANNEL_ID = "GPS Tracking Service";
    public static final String NOTIFICATION_TIME = "time";
    public static final String NOTIFICATION_DISTANCE = "distance";
    public static final String NOTIFICATION_AVGSPEED = "avgspeed";
    public static final String NOTIFICATION_MAXSPEED = "maxspeed";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationService mService;
    private LocationRequest locationRequest;
    private float total;
    private static float total_dist_KM;
    private Location prev_loc;
    public static String elapsed_formatted;
    private Calendar calendar;
    private SimpleDateFormat formatDate;
    public int rounded;
    String mDate;
    String mTimerText;
    double mTime = -1.0;
    Timer mTimer;
    public static TimerTask mTimerTask;
    String format_distance, format_speed, format_maxSpeed;
    double average_speed;
    float maxSpeed = 0;
    int totalSpeed = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Create a notification channel for android system which are greater than or equal to Android 8.0
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "GPS Tracking Service", NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Links from : https://www.youtube.com/watch?v=7QVr5SgpVog
    private void timer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                mTime++;
                mTimerText = getTimer();
            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 0 ,1000);
    }

    //Create a method that takes total time elapsed in seconds and convert it to hours, minutes and seconds.
    @SuppressLint("DefaultLocale")
    String getTimer()
    {
        rounded = (int) Math.round(mTime);
        int elapsed_secs = ((rounded % 86400) % 3600) % 60;
        int elapsed_mins = ((rounded % 86400) % 3600) / 60;
        int elapsed_hrs = ((rounded % 86400) / 3600);
        Log.d("comp3018", "Time Elapsed: " + elapsed_formatted);
        return elapsed_formatted = String.format("%02d:%02d:%02d", elapsed_hrs, elapsed_mins, elapsed_secs);
    }

    //Create a date variable with the current date and time
    //Referenced from: https://www.javatpoint.com/java-string-to-date
    @SuppressLint("SimpleDateFormat")
    private void setDate() {
        calendar = Calendar.getInstance();
        formatDate = new SimpleDateFormat("dd MMM yyyy  h:mm a");
        mDate = formatDate.format(calendar.getTime());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer();
        setDate();
        createNotificationChannel();

        //Referenced from: https://stackoverflow.com/questions/30460932/send-putextra-via-notification-intent
        //Create an intent to display after clicking the notification
        Intent notificationIntent = new Intent(this, RecordsInfo.class);
        notificationIntent.putExtra(NOTIFICATION_TIME, elapsed_formatted);
        notificationIntent.putExtra(NOTIFICATION_DISTANCE,format_distance);
        notificationIntent.putExtra(NOTIFICATION_AVGSPEED, format_speed);
        notificationIntent.putExtra(NOTIFICATION_MAXSPEED, format_maxSpeed);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.running)
                .setContentTitle("GPS Tracking Service")
                .setContentText("Distance Travelled: " + format_distance + "KM")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        //Start Foreground service
        startForeground(1, builder.build());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();
        locationCallback = new LocationCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("comp3018", "location " + location.toString());

                    builder.setContentText("Distance Travelled: " + format_distance + "KM");
                    notificationManager.notify(1, builder.build());

                    if(prev_loc != null)
                    {
                        total += prev_loc.distanceTo(location);
                    }
                    prev_loc = location;
                    total_dist_KM = total/1000;
                    //Formula for Average speed
                    average_speed = total_dist_KM/((rounded)/3600.0);
                    Log.d("comp3018","distance "+total_dist_KM);
                    //Convert distance to string format
                    format_distance = String.format("%.2f",total_dist_KM);
                    //Convert speed to string format
                    format_speed = String.format("%.2f", average_speed);
                    //Formula for Max speed
                    totalSpeed += average_speed;
                    if(average_speed > maxSpeed){
                        maxSpeed = (float) average_speed;
                    }
                    //Convert max speed to string format
                    format_maxSpeed = String.format("%.2f",maxSpeed);

                    //Referenced from: https://www.techotopia.com/index.php/Android_Broadcast_Intents_and_Broadcast_Receivers
                    //Create a broadcast intent and send the data to multiple components
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.broad.LOAD_URL");
                    broadcastIntent.putExtra("date", mDate);
                    broadcastIntent.putExtra("time",elapsed_formatted);
                    broadcastIntent.putExtra("distance", format_distance);
                    broadcastIntent.putExtra("avgspeed", format_speed);
                    broadcastIntent.putExtra("maxspeed",format_maxSpeed);
                    sendBroadcast(broadcastIntent);

                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        catch(SecurityException e){
            // lacking permission to access location
        }
        return START_STICKY;
    }


    //Stop foreground service and cancel location update after it is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d("comp3018", "Location Service is stopped!");
    }

}
