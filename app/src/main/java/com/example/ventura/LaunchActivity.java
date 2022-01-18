package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_launch);
        MyApplication app = (MyApplication) this.getApplication();
        Intent i;
        if(app.isLoggedIn){
            i = new Intent(this, MainActivity.class);
        }
        else {
            i = new Intent(this, ActivityLogin.class);
        }
        finish();
        startActivity(i);
    }
}