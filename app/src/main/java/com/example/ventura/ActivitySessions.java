package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ventura.ui.sessions.SessionsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivitySessions extends AppCompatActivity {
    private SessionsViewModel mViewModel;
    private RecyclerView recyclerView;
    private AdapterSession adapter;
    private MyApplication app;
    private Sessions ss = new Sessions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        recyclerView = findViewById(R.id.recyclerViewSessions);
        app = (MyApplication) getApplication();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        getSessions();
        initAdapter();
    }



    private void initDialog() {}

    private void initAdapter() {
        app.getSessions().clear();
        adapter = new AdapterSession(app, new AdapterSession.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
                String jwt = sp.getString("jwt", null);
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.DefaultCompany.RGA");
                intent.putExtra("SessionUUID", app.getSessions().getSessionAtPos(position).getUuid());
                intent.putExtra("jwt", jwt);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View itemView, int position) {
                /*ch.getMovieArrayList().remove(position);
                app.saveData();
                adapter.notifyDataSetChanged();*/
            }
        }, ss);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
    }

    public ArrayList<Double> stringToDoubleArray(String string) {
        String[] stringArray;
        string = string.substring(3, string.length()-3);
        stringArray = string.split(",");
        ArrayList<Double> doubleArrayList = new ArrayList<>();
        for(int i = 0; i < stringArray.length; i++) {
            doubleArrayList.add(Double.parseDouble(stringArray[i]));
        }
        return doubleArrayList;
    }

    /*protected void onResume()
    {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }*/

    public void getSessions() {
        Sessions sessions = new Sessions();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
        String userID = sp.getString("userId", null);
        String jwt = sp.getString("jwt", null);
        RequestQueue queue = Volley.newRequestQueue(app.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLConstants.ip+"/activities/user/"+userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i < jsonArray.length(); i++) {
                                Session session = new Session();
                                JSONObject obj = jsonArray.getJSONObject(i);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                long miliseconds = 0;
                                try {
                                    Date date = format.parse(obj.getString("start_time"));
                                    miliseconds = date.getTime();
                                }
                                catch (ParseException ex) {
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
                                session.setUuid(obj.getString("_id"));
                                app.getSessions().addSession(session);
                                adapter.notifyDataSetChanged();
                                Log.i("asd","gotsessiosn");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("asda",e.getMessage());
                        }

                        //adapter.notifyDataSetChanged();
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