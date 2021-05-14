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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {
    private static final int PERMISSION_ALL = 123;
    final int activityID = 1;
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;
    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private MyApplication app;
    protected LocationManager locationManager;
    TextView asd;
    TextView dist;
    String provider;
    GeoPoint startPoint;
    IMapController mapController;
    Marker marker;
    Session s = new Session();
    private Polyline polyline;
    private ArrayList<GeoPoint> pathPoints;
    double distance;
    private Location lok;
    private Session session;


    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CAMERA
    };

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
        app.getSessions().addSession(sesh);
        session = app.getSessions().getSessions().get(0);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        map = (MapView) findViewById(R.id.mapStopwatch);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        polyline = new Polyline();
        pathPoints = new ArrayList<GeoPoint>();
        map.getOverlays().add(polyline);

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

    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this,PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            initMapStartGPS();
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
    /*@SuppressLint("MissingPermission")
    public void initMapStartGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,5,mLocationListener);
    }*/

    @SuppressLint("UseCompatLoadingForDrawables")
    private Marker getPositionMarker() { //Singelton
        if (marker==null) {
            marker = new Marker(map);
            marker.setTitle("Here I am");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(getResources().getDrawable(R.drawable.location_icon));
            map.getOverlays().add(marker);
        }
        return marker;
    }

    @SuppressLint("MissingPermission")
    public void initMapStartGPS() {
        mapController = map.getController();
        mapController.setZoom(18.5);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            startPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                            mapController.setCenter(startPoint);
                        }
                    }
                });
        map.invalidate();
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
            if(running) {
                DecimalFormat df2 = new DecimalFormat("#.##");
                IMapController mapController = map.getController();
                mapController.setZoom(18.5);
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude()); //Postavi GPS lokacijo
                pathPoints.add(startPoint);
                polyline.setPoints(pathPoints);
                mapController.setCenter(startPoint);
                distance = polyline.getDistance();
                String stringedDistance = df2.format(distance).toString();
                s.setDistance(distance);
                s.setEndTime(location.getTime());
                s.addLatitude(location.getLatitude());
                s.addLongtitude(location.getLongitude());
                s.addElevation(location.getAltitude());
                s.addSpeed(location.getSpeed());
                stringedDistance += "m";
                //dist.setText(stringedDistance);

                map.invalidate();
            }
        }
    };
}