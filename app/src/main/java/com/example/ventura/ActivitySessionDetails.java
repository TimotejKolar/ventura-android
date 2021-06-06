package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySessionDetails extends AppCompatActivity {
    private String sessionUUID;
    private MyApplication app;
    private MapView map;
    private TextView textViewTitle;
    private TextView textViewDistance;
    private TextView textViewDuration;
    private TextView textViewElevation;
    private TextView textViewPace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        map = findViewById(R.id.mapD);
        textViewTitle = findViewById(R.id.textViewTitleD);
        textViewDistance = findViewById(R.id.textViewDistanceD);
        textViewDuration = findViewById(R.id.textViewDurationD);
        textViewElevation = findViewById(R.id.textViewElevationD);
        textViewPace = findViewById(R.id.textViewPaceD);

        app = (MyApplication) getApplication();
        sessionUUID = getIntent().getStringExtra("SessionUUID");
        getSession(new VolleyCallbackSession() {
            @Override
            public void onSuccessResponse(Session session) {
                setData(session);
                drawPath(map, session.getLatitude(), session.getLongtitude());
            }
        });
    }


    public void setData(Session session) {
        double distance = 0, durationSeconds = 0, durationMinutes = 0, durationHours = 0;
        distance = session.getDistance() / 1000;
        distance = round(distance, 2);
        textViewTitle.setText(session.getActivityName());
        textViewDistance.setText(Double.toString(distance) + " km");
        durationMinutes = session.getDuration() / 60;
        if (durationMinutes >= 60) {
            durationHours++;
            durationMinutes -= 60;
        }
        durationSeconds = session.getDuration() % 60;
        if (durationHours != 0) {
            textViewDuration.setText(Integer.toString((int) durationHours) + "h " + Integer.toString((int) durationMinutes) + "m " + Integer.toString((int) durationSeconds) + "s");
        } else if (durationMinutes != 0) {
            textViewDuration.setText(Integer.toString((int) durationMinutes) + "m " + Integer.toString((int) durationSeconds) + "s");
        } else {
            textViewDuration.setText(Integer.toString((int) durationSeconds) + "s");
        }

        int elevationGain = 0;
        List<Double> elevation = session.getElevation();
        for (int i = 0; i < elevation.size() - 1; i++) {
            if (elevation.get(i) < elevation.get(i + 1)) {
                double diff = elevation.get(i + 1) - elevation.get(i);
                elevationGain += diff;
            }
        }
        textViewElevation.setText(Integer.toString(elevationGain) + " m");

        double pace = (session.getDuration() / 60) / (session.getDistance() / 1000);
        pace = round(pace, 2);
        textViewPace.setText(Double.toString(pace) + " min/km");
        //drawPath(map, session.getLatitude(), session.getLongtitude());
    }

    public void drawPath(MapView map, ArrayList<Double> latitude, ArrayList<Double> longitude) {
        Polyline polyline = new Polyline(map);
        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(latitude.get(0), longitude.get(0));
        mapController.setCenter(startPoint);
        mapController.setZoom(16.0);
        for (int i = 0; i < latitude.size(); i++) {
            GeoPoint geoPoint = new GeoPoint(latitude.get(i), longitude.get(i));
            polyline.addPoint(geoPoint);
        }
        map.getOverlays().add(polyline);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public ArrayList<Double> stringToDoubleArray(String string) {
        String[] stringArray;
        string = string.substring(3, string.length() - 3);
        stringArray = string.split(",");
        ArrayList<Double> doubleArrayList = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            doubleArrayList.add(Double.parseDouble(stringArray[i]));
        }
        return doubleArrayList;
    }

    public void getSession(final VolleyCallbackSession callback) {
        Session session = new Session();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
        String jwt = sp.getString("jwt", null);
        RequestQueue queue = Volley.newRequestQueue(app.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLConstants.ip + "/activities/" + sessionUUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONArray jsonArray = new JSONArray(response);
                            JSONObject obj = new JSONObject(response);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            long miliseconds = 0;
                            try {
                                Date date = format.parse(obj.getString("start_time"));
                                miliseconds = date.getTime();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            session.setActivityName(obj.getString("title"));
                            session.setActivityType(obj.getString("type"));
                            session.setLatitude(stringToDoubleArray(obj.getString("latitude")));
                            session.setLongtitude(stringToDoubleArray(obj.getString("longtitude")));
                            session.setElevation(stringToDoubleArray(obj.getString("elevation")));
                            session.setSpeeds(stringToDoubleArray(obj.getString("speed")));
                            session.setDistance(Double.parseDouble(obj.getString("distance")));
                            session.setStartTime(miliseconds);
                            session.setDuration(Long.parseLong(obj.getString("elapsed_time")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("asda", e.getMessage());
                        }
                        callback.onSuccessResponse(session);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("asd", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-auth-token", jwt);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}