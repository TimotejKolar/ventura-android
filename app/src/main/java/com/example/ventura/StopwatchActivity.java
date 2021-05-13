package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {
    private static final int PERMISSION_ALL = 123;
    final int activityID = 1;
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;

    public MyApplication app;
    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CAMERA
    };
    LocationManager locationManager;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        app = (MyApplication)getApplication();
        if (savedInstanceState != null) {
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");
        }
        Session sesh = new Session();
        app.getSessions().addActivity(sesh);
        session = app.getSessions().getSessions().get(0);

        runTimer();
    }

    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = running;
        running = false;
    }
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    public void onClickStart(View view) {
        running = true;
    }
    public void onClickStop(View view)
    {
        running = false;
        seconds = 0;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length >= PERMISSIONS.length) {
                    for (int i=0; i<PERMISSIONS.length; i++) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this,"Nimaš vseh dovolenj!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    initMapStartGPS();
                }
                else
                    finish();
            }

        }
    }
    @SuppressLint("MissingPermission")
    public void initMapStartGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,5,mLocationListener);
    }
    public void onClickPause(View view)
    {
        running = false;
    }
    public void onClickShowLocation(View view){
        //Intent i = new Intent(this,LocationActivity.class);
        //i.putExtra("time",seconds);
        //startActivityForResult(i,activityID);
    }
    private void runTimer()
    {

        // Get the text view.
        final TextView timeView
                = (TextView)findViewById(
                R.id.textViewTimeValue);

        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

        }
    };
}