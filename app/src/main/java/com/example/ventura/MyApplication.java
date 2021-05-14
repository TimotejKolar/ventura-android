package com.example.ventura;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;


public class MyApplication extends Application {
    private Sessions sessions;
    private User user;
    public boolean isLoggedIn;
    private static final String TAG_USERID = "userId";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_FIRSTNAME = "first_name";
    private static final String TAG_LASTNAME = "last_name";
    private static final String MY_FILE_NAME = "data.json";
    private Gson gson;
    private File file;
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new User();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.contains(TAG_USERID)) {
            user.setEmail(sp.getString(TAG_EMAIL, "/"));
            user.setFirstName(sp.getString(TAG_FIRSTNAME, "/"));
            user.setLastName(sp.getString(TAG_LASTNAME, "/"));
            user.setId(sp.getString(TAG_USERID, "/"));
            isLoggedIn = true;
        } else {
            isLoggedIn = false;
        }
        if (!readFromFile()) {
            sessions = new Sessions();
        }
    }

    public User getUser() {
        return user;
    }

    public Sessions getSessions() {
        return sessions;
    }

    public void saveData() {
        saveToFile();
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }

    private File getFile() {
        if (file == null) {
            File filesDir = getFilesDir();
            file = new File(filesDir, MY_FILE_NAME);
        }
        return file;
    }

    private void saveToFile() {
        try {
            FileUtils.writeStringToFile(getFile(), getGson().toJson(sessions));
        } catch (IOException e) {
            Timber.d("Can't save file %s", file.getPath());
        }
    }

    private boolean readFromFile() {
        if (!getFile().exists()) return false;
        try {
            sessions = getGson().fromJson(FileUtils.readFileToString(getFile()), Sessions.class);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
