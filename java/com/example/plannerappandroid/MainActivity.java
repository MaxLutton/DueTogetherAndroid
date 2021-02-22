package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Email and Password.
        final EditText emailField = findViewById(R.id.emailTextField);
        final EditText passwordField = findViewById(R.id.passwordTextField);

        // Login card.
        final CardView loginCard = findViewById(R.id.loginCard);
        loginCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                Toast.makeText(MainActivity.this, String.format("Logging in with credentials: email: %s password: %s", email, password), Toast.LENGTH_SHORT).show();
                ApiRequestHandler loginRequestHandler = new ApiRequestHandler(getApplicationContext());
                loginRequestHandler.setOnApiEventListener(new OnApiEventListener() {
                    @Override
                    public void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType) {
                        Toast.makeText(MainActivity.this, "Welcome back :)", Toast.LENGTH_LONG).show();
                        // Save tokens to SharedPreferences file.
                        String accessToken;
                        String refreshToken;
                        try {
                            accessToken = resultObject.getString("access");
                            refreshToken = resultObject.getString("refresh");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Unable to parse response :(", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Context context = getApplicationContext();
                        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("access", accessToken);
                        editor.putString("refresh", refreshToken);
                        editor.putString("email", email);
                        editor.apply();

                        //Redirect to Dashboard Activity.
                        Intent dashboardActivity = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(dashboardActivity);
                    }
                });
                loginRequestHandler.loginUser(email, password);
            }
        });
    }
}

