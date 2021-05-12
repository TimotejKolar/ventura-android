package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ActivityRegister extends AppCompatActivity {
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmailRegister;
    private EditText etPasswordRegister;
    private EditText etRepeatPassword;
    private TextView textViewLogin;
    private Button buttonRegister;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        textViewLogin = findViewById(R.id.textViewLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        compositeSubscription = new CompositeSubscription();

        textViewLogin.setOnClickListener(view->showLoginForm());
        buttonRegister.setOnClickListener(view->register());
    }

    private void showLoginForm() {
        Intent intent = new Intent(getBaseContext(), ActivityLogin.class);
        startActivity(intent);
    }

    private void register() {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmailRegister.getText().toString();
        String password = etPasswordRegister.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();
        int nErrors = 0;

        if(TextUtils.isEmpty(firstName)) {
            nErrors++;
            etFirstName.setError("First name should not be empty");
        }

        if(TextUtils.isEmpty(lastName)) {
            nErrors++;
            etLastName.setError("Last name should not be empty");
        }

        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            nErrors++;
            etEmailRegister.setError("Email is not valid!");
        }

        if(TextUtils.isEmpty(password)) {
            nErrors++;
            etPasswordRegister.setError("Password should not be empty!");
        }

        if(TextUtils.isEmpty(repeatPassword)) {
            nErrors++;
            etRepeatPassword.setError("Repeat password should not be empty!");
        }

        /*if(repeatPassword.equals(password)) {
            nErrors++;
            etRepeatPassword.setError("Password does not match!");
        }*/

        if(nErrors == 0) {
            User user = new User(firstName, lastName, email, password);
            registerProcess(user);
        }
        else {
            showSnackBarMessage("Enter valid details!");
        }
    }

    private void registerProcess(User user) {
        compositeSubscription.add(NetworkUtil.getRetroFit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        //Toast.makeText(this, response., Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable error) {
        if(error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();
            try {
                String errorBody = ((HttpException)error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showSnackBarMessage(response.message());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else showSnackBarMessage("Network error!");
    }

    private void showSnackBarMessage(String message) {
        if(getCurrentFocus() != null) {
            Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}