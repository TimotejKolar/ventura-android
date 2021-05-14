package com.example.ventura;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class MyApplication extends Application {
    private Sessions sessions;
    private User user;
    public boolean isLoggedIn;
    private static final String TAG_USERID = "userId";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_FIRSTNAME = "first_name";
    private static final String TAG_LASTNAME = "last_name";
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new User();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sp.contains(TAG_USERID)){
            user.setEmail(sp.getString(TAG_EMAIL,"/"));
            user.setFirstName(sp.getString(TAG_FIRSTNAME,"/"));
            user.setLastName(sp.getString(TAG_LASTNAME,"/"));
            user.setId(sp.getString(TAG_USERID,"/"));
            isLoggedIn = true;
            //SharedPreferences.Editor editor = sp.edit();
            //editor.putString(TAG_UUID, APP_UUID);
            //editor.apply();
        }
        else{
            isLoggedIn = false;
        }
        sessions = new Sessions();
    }

    public User getUser(){
        return user;
    }

    public Sessions getSessions(){
        return sessions;
    }

}
