package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class NewAccountActivity extends AppCompatActivity {

    private String newEmail;
    private String newPassword;
    private String TAG = "NewAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        ApiRequestHandler createUserHandler = new ApiRequestHandler(getApplicationContext());
        createUserHandler.setOnApiEventListener(new OnApiEventListener() {
            @Override
            public void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType) {
                if (requestType == ApiRequestHandler.ApiRequestType.CREATE_USER) {
                    Log.w(TAG, "Create User request successful.");
                    createUserHandler.loginUser(newEmail, newPassword);
                } else if (requestType == ApiRequestHandler.ApiRequestType.LOGIN) {
                    Log.w(TAG, "Log in with new user successful.");
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
                    editor.putString("email", newEmail);
                    editor.apply();

                    //Redirect to Dashboard Activity.
                    Intent dashboardActivity = new Intent(NewAccountActivity.this, DashboardActivity.class);
                    startActivity(dashboardActivity);
                }
            }
        });
        EditText email = findViewById(R.id.newUserEmail);
        EditText password = findViewById(R.id.newUserPassword);
        Button newUserSubmit = findViewById(R.id.newUserSubmit);
        newUserSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                newEmail = email.getText().toString();
                newPassword = password.getText().toString();
                if (!newEmail.isEmpty() && !newPassword.isEmpty()){
                    try {
                        createUserHandler.createNewUser(newEmail, newPassword);
                    } catch (JSONException e) {
                        Toast.makeText(NewAccountActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(NewAccountActivity.this, "Must provide an email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}