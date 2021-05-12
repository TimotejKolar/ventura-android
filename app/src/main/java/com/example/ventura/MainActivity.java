package com.example.ventura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    //private FragmentLogin fragmentLogin;
    //private FragmentRegister fragmentRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            loadFragment();
        }
    }

    private void loadFragment() {
        Intent intent = new Intent(getBaseContext(), ActivityLogin.class);
        startActivity(intent);
        /*if(fragmentLogin == null) {
            fragmentLogin = new FragmentLogin();
            fragmentRegister = new FragmentRegister();
        }*/

        //Displays login form on application start
        //getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragmentFrame, FragmentLogin.newInstance(), null).commit();
        //getFragmentManager().beginTransaction().replace(R.id.fragmentFrame, fragmentLogin).commit();
    }
}