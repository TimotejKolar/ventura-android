package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

        textViewRegister.setOnClickListener(view->showRegisterForm());
        buttonLogin.setOnClickListener(view->checkInput());
    }

    private void showRegisterForm() {
        Intent intent = new Intent(getBaseContext(), ActivityRegister.class);
        startActivity(intent);
    }


    private void checkInput() {
        String email = etEmailLogin.getText().toString();
        String password = etPasswordLogin.getText().toString();
        int nErrors = 0;
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            nErrors++;
            etEmailLogin.setError("Email is not valid!");
        }

        if(TextUtils.isEmpty(password)) {
            nErrors++;
            etPasswordLogin.setError("Password should not be empty!");
        }

        if(nErrors == 0) {
            login(email, password);
        }
        else {

        }
    }

    private void login(String email, String password) {
        //compositeSubscription.add();
    }
}