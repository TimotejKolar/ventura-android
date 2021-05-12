package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

public class ActivityLogin extends AppCompatActivity {
    private EditText etEmailLogin;
    private EditText etPasswordLogin;
    private Button buttonLogin;
    private ImageView image;
    private TextView textViewRegister;
    private TextView textViewForgotPassword;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        textViewRegister.setOnClickListener(view -> showRegisterForm());
        buttonLogin.setOnClickListener(view -> login());
    }

    private void showRegisterForm() {
        Intent intent = new Intent(getBaseContext(), ActivityRegister.class);
        startActivity(intent);
    }


    private void login() {
        String email = etEmailLogin.getText().toString();
        String password = etPasswordLogin.getText().toString();
        int nErrors = 0;
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            nErrors++;
            etEmailLogin.setError("Email is not valid!");
        }

        if (TextUtils.isEmpty(password)) {
            nErrors++;
            etPasswordLogin.setError("Password should not be empty!");
        }

        if (nErrors == 0) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.178.68:3001/users/login",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(ActivityLogin.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
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
                    params.put("email", email);
                    params.put("password", password);

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
        } else {
            showSnackBarMessage("Enter valid details!");
        }
    }

    private void showSnackBarMessage(String message) {
        if(getCurrentFocus() != null) {
            Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}