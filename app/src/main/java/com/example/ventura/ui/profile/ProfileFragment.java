package com.example.ventura.ui.profile;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ventura.ActivityLogin;
import com.example.ventura.HeartbeatActivity;
import com.example.ventura.LaunchActivity;
import com.example.ventura.MainActivity;
import com.example.ventura.MyApplication;
import com.example.ventura.R;
import com.example.ventura.URLConstants;
import com.example.ventura.YourHeartbeatsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;
    private TextView textViewName;
    private TextView textViewDistance;
    private TextView textViewNumActivities;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    public View root;
    public MyApplication app;
    private int totalDistance = 0;
    private int numActivities = 0;
    Button btn;
    Button heartbeat;
    Button yourHearbeats;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container,         false);
        heartbeat = (Button) rootView.findViewById(R.id.buttonHeartbeat);
        yourHearbeats = (Button) rootView.findViewById(R.id.buttonYourHeartbeats);
        heartbeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 100);
                }
                Intent i = new Intent(getActivity(), HeartbeatActivity.class);
                startActivity(i);

            }
        });
        yourHearbeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), YourHeartbeatsActivity.class);
                startActivity(i);
            }
        });

        btn = (Button) rootView.findViewById(R.id.buttonLogout);
        btn.setOnClickListener(this);
        return rootView;
    }
    @Override
    public void onClick(View v) {
        app.clearPreferences();
        Intent i = new Intent(this.getActivity(), LaunchActivity.class);
        startActivity(i);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        app = (MyApplication) requireActivity().getApplication();
        textViewName = (TextView)getView().findViewById(R.id.textViewName);
        String text = app.getUser().getFirstName() + " " + app.getUser().getLastName();
        textViewName.setText(text);
        textViewDistance = (TextView)getView().findViewById(R.id.textViewDist);
        textViewNumActivities = (TextView)getView().findViewById(R.id.textViewActivities);
        Log.i("asd","error: " + app.getUser().getJwt());
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLConstants.ip+"/activities/user/"+app.getUser().getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray arr = new JSONArray(response);
                            for (int i = 0; i < arr.length();i++){
                                JSONObject e = arr.getJSONObject(i);
                                totalDistance += e.getInt("distance");
                                numActivities++;
                            }
                            Log.i("asd","success");
                            String dist = "Total distance: " + totalDistance;
                            String act = "Total activities: " + numActivities;
                            textViewDistance.setText(dist);
                            textViewNumActivities.setText(act);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("asd",e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("asd", error.toString());
                Toast.makeText(getActivity(), "Error getting data", Toast.LENGTH_SHORT).show();
            }
        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("x-auth-token", app.getUser().getJwt());
            return params;
        }
        };
        queue.add(stringRequest);

    }

}