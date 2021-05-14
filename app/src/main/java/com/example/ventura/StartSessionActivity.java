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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StartSessionActivity extends AppCompatActivity {
    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private MyApplication app;
    protected LocationManager locationManager;
    public static final int PERMISSION_ALL = 123;
    TextView asd;
    TextView dist;
    String provider;
    GeoPoint startPoint;
    IMapController mapController;
    Marker marker;
    Session s = new Session();
    boolean running = true;
    int seconds;
    private Polyline polyline;
    private ArrayList<GeoPoint> pathPoints;
    double distance;
    private Location lok;
    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CAMERA
    };

    private Spinner dropdown;
    String[] types = new String[]{"Running", "Cycling", "Hiking", "Walking"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_start_session);
        app = (MyApplication)getApplication();

        dropdown = findViewById(R.id.spinnerActivityType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        dropdown.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
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

    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this,PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            initMapStartGPS();
        }
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
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause(); //needed for compass, my location overlays, v6.0.0 and up
    }


    @SuppressLint("MissingPermission")
    public void onResume(){
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            DecimalFormat df2 = new DecimalFormat("#.##");
            IMapController mapController = map.getController();
            mapController.setZoom(18.5);
            GeoPoint startPoint = new GeoPoint(location.getLatitude(),location.getLongitude()); //Postavi GPS lokacijo
            mapController.setCenter(startPoint);
            //getPositionMarker();
            map.invalidate();
        }
    };
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

    @Override
    public void onBackPressed() {
        finishActivity(1);
        super.onBackPressed();
    }

    public void onStartActivityButtonClick(View view) {
        Intent intent = new Intent(this, StopwatchActivity.class);
        intent.putExtra("activityType", dropdown.getSelectedItem().toString());
        startActivity(intent);
    }
}