package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;

import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;
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
                String url = apiBaseUrl + "api-auth/login/";
                Map<String, String> payloadDict = new HashMap<String, String>();
                payloadDict.put("")
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, )
            }
        });

    }
}
