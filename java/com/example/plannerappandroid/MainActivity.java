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
                Toast.makeText(MainActivity.this, String.format("Logging in with credentials: email: %s password: %s",email, password), Toast.LENGTH_SHORT).show();

                //Setup request.
                String tokenUrl = apiBaseUrl + "token/";
                JSONObject body = new JSONObject();
                try {
                    body.put("username", email);
                    body.put("password", password);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, tokenUrl, body, new  Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Welcome back :)", Toast.LENGTH_LONG).show();

                        // Save tokens to SharedPreferences file.
                        String accessToken;
                        String refreshToken;
                        try {
                            accessToken = response.getString("access");
                            refreshToken = response.getString("refresh");
                        } catch(JSONException e){
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
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if (error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "Server down Server down!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NoConnectionError){
                            Toast.makeText(MainActivity.this, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
                        }
                        else if (error instanceof AuthFailureError){
                            Toast.makeText(MainActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                        }
                        else if (error instanceof ServerError){
                            Toast.makeText(MainActivity.this, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                //Add request to queue!
                VolleyController.getInstance(getApplicationContext()).addToRequestQueue(loginRequest);
            }
        });

    }
}
