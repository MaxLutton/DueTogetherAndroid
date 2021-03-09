package com.example.plannerappandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "MyFirebaseMessagingService";
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // Save token in database
        Log.w(TAG, "Got a new token! " + s);

        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("firebaseToken", s);
        editor.apply();
        Log.w(TAG, "Saved firebase token.");
    }

}
