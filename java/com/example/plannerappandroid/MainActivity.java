package com.example.plannerappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";

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
                ApiRequestHandler loginRequestHandler = new ApiRequestHandler(getApplicationContext());
                loginRequestHandler.setOnApiEventListener(new OnApiEventListener() {
                    @Override
                    public void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType) {
                        if (requestType == ApiRequestHandler.ApiRequestType.LOGIN){
                            // Save tokens to SharedPreferences file.
                            String accessToken;
                            String refreshToken;
                            try {
                                accessToken = resultObject.getString("access");
                                refreshToken = resultObject.getString("refresh");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.w(TAG, "Unable to parse token response");
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
                    }
                });
                loginRequestHandler.loginUser(email, password);
            }
        });
        TextView createAccountText = findViewById(R.id.newAccountText);
        createAccountText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Clicked create new user text");
                Intent newAccountActivity = new Intent(MainActivity.this, NewAccountActivity.class);
                startActivity(newAccountActivity);
            }
        });
    }
}

