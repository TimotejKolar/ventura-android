package com.example.ventura;

import static com.example.ventura.ActivitySessionDetails.round;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SaveSessionActivity extends AppCompatActivity {
    private EditText etSessionName;
    private Spinner dropdown;
    private Button buttonSaveSession;
    private Button buttonDeleteSession;
    private MyApplication app;
    private String sessionUUID;
    private Session session;

    private TextView distance;
    private TextView pace;
    private TextView type;
    String[] types = new String[]{"Running", "Cycling", "Hiking", "Walking"};


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_session);

        app = (MyApplication)getApplication();
        etSessionName = findViewById(R.id.etSessionName);
        dropdown = findViewById(R.id.spinnerActivityType);
        buttonSaveSession = findViewById(R.id.buttonSaveSession);
        buttonDeleteSession = findViewById(R.id.buttonDeleteSession);

        /*dropdown = findViewById(R.id.spinnerActivityType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        dropdown.setAdapter(adapter);*/

        sessionUUID = getIntent().getStringExtra("SessionUUID");
        for(int i = 0; i < app.getSessions().size(); i++) {
            session = app.getSessions().getSessions().get(i);
            if(session.getUuid().equals(sessionUUID)) {
                break;
            }
        }
        distance = (TextView)findViewById(R.id.textViewSaveDistance);
        pace = (TextView)findViewById(R.id.textViewSavePace);
        type = (TextView)findViewById(R.id.textViewSaveType);
        String distanceText = "";
        double distanceValue = 0;
        if(session.getDistance() > 1000){
            distanceValue = session.getDistance() / 1000;
            distanceValue = round(distanceValue, 2);
            distanceText = distanceValue + "km";
        }
        else {
            distanceValue = session.getDistance();
            distanceValue = round(distanceValue, 2);

            distanceText = distanceValue + "m";
        }
        distance.setText(distanceText);
        double paceValue = (session.getDuration() / 60f) / (session.getDistance() / 1000);
        paceValue = round(paceValue, 2);
        Log.i("asd","duration" + session.getDuration() + " distance:" + session.getDistance());
        pace.setText(paceValue + "min/km");
        type.setText(session.getActivityType());
    }


    public void onSaveSessionClick(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = sp.getString("userId", null);
        String jwt = sp.getString("jwt", null);
        if(TextUtils.isEmpty(etSessionName.getText())) {
            etSessionName.setError("Session name should not be empty!");
        }
        session.setActivityName(etSessionName.getText().toString());

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URLConstants.ip+"/activities",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SaveSessionActivity.this, "Activity succcessfully uploaded!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("asd", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", session.getActivityName());
                params.put("latitude", session.getLatitude().toString());
                params.put("longtitude", session.getLongtitude().toString());
                params.put("speed", session.getSpeeds().toString());
                params.put("elevation", session.getElevation().toString());
                params.put("distance", String.valueOf(session.getDistance()));
                params.put("type", session.getActivityType());
                params.put("start_time", Long.toString(session.getStartTime()));
                params.put("elapsed_time", Long.toString(session.getDuration()));
                params.put("user", userID);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-auth-token", jwt);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void onDeleteSessionClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}