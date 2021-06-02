package com.example.ventura.ui.sessions;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ventura.AdapterSession;
import com.example.ventura.MyApplication;
import com.example.ventura.R;
import com.example.ventura.Session;
import com.example.ventura.Sessions;
import com.example.ventura.URLConstants;
import com.example.ventura.VolleyCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionsFragment extends Fragment {

    private SessionsViewModel mViewModel;
    private RecyclerView recyclerView;
    private AdapterSession adapter;
    private MyApplication app;
    private Sessions ss = new Sessions();

    public static SessionsFragment newInstance() {
        return new SessionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sessions_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SessionsViewModel.class);
        recyclerView = getView().findViewById(R.id.recyclerView);
        app = (MyApplication)getActivity().getApplication();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        getSessions(new VolleyCallback() {
            @Override
            public void onSuccessResponse(Sessions sessions) {
                ss = sessions;
                initDialog();
                initAdapter();
            }
        });
    }


    private void initDialog() {}

    private void initAdapter() {
        adapter = new AdapterSession(app, new AdapterSession.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                /*Intent i = new Intent(getActivity().getBaseContext(), SessionsFragment.class);
                i.putExtra("MovieUUID", ch.getMovieArrayList().get(position).getUuid());
                startActivity(i);*/
            }

            @Override
            public void onItemLongClick(View itemView, int position) {
                /*ch.getMovieArrayList().remove(position);
                app.saveData();
                adapter.notifyDataSetChanged();*/
            }
        }, ss);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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

    public void getSessions(final VolleyCallback callback) {
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
                                sessions.addSession(session);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("asda",e.getMessage());
                        }
                        callback.onSuccessResponse(sessions);
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