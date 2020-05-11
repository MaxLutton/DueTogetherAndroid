package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
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
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                Toast.makeText(MainActivity.this, String.format("Logging in with credentials: email: %s password: %s",email, password), Toast.LENGTH_SHORT).show();

                //Setup request.
                String tokenUrl = apiBaseUrl + "token/";
                String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", email, password);
                JSONObject jsonBody = null;
                try {
                    jsonBody = new JSONObject(body);
                } catch (JSONException e) {
                    //Toast.makeText(MainActivity.this, "Entered invalid credentials! Require both email and password.", Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, body, Toast.LENGTH_LONG).show();
                    Log.d("Um", body);
                    e.printStackTrace();
                    return;
                }
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, tokenUrl, jsonBody, new  Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        /*
                        1. Check status code.
                        2. If failed, then
                            a) 403: Tell user that user/pwd is wrong
                            b) Timeout? Tell user need internet access...
                        3. If pass, then store both tokens in internal memory. Redirect to home activity.
                         */
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

                //Add request to queue!
                VolleyController.getInstance(getApplicationContext()).addToRequestQueue(loginRequest);
            }
        });

    }
}
