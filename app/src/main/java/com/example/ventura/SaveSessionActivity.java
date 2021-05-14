package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SaveSessionActivity extends AppCompatActivity {
    private EditText etSessionName;
    private Spinner dropdown;
    private Button buttonSaveSession;
    private Button buttonDeleteSession;
    private MyApplication app;
    private String sessionUUID;
    private Session session;
    String[] types = new String[]{"Running", "Cycling", "Hiking", "Walking"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_session);

        app = (MyApplication)getApplication();
        etSessionName = findViewById(R.id.etSessionName);
        dropdown = findViewById(R.id.spinnerActivityType);
        buttonSaveSession = findViewById(R.id.buttonSaveSession);
        buttonDeleteSession = findViewById(R.id.buttonDeleteSession);

        dropdown = findViewById(R.id.spinnerActivityType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        dropdown.setAdapter(adapter);

        sessionUUID = getIntent().getStringExtra("SessionUUID");
        for(int i = 0; i < app.getSessions().size(); i++) {
            session = app.getSessions().getSessions().get(i);
            if(session.getUuid().equals(sessionUUID)) {
                break;
            }
        }
    }


    public void onSaveSessionClick(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = sp.getString("userId", null);
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
                params.put("lattitude", session.getLatitude().toString());
                params.put("longtitude", session.getLongtitude().toString());
                params.put("speed", session.getSpeeds().toString());
                params.put("elevation", session.getElevation().toString());
                params.put("distance", String.valueOf(session.getDistance()));
                params.put("type", session.getActivityType());
                params.put("start_time", Long.toString(session.getStartTime()));
                params.put("end_time", Long.toString(session.getEndTime()));
                params.put("user", userID);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
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