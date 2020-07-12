package com.example.plannerappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.OnTaskListener {
    private RecyclerView upcomingTasksRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private int userId;
    final List<Task> upcomingTaskList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Setup Nav Bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_teams:
                        Toast.makeText(DashboardActivity.this, "Teams", Toast.LENGTH_SHORT).show();
                        // Redirect to Teams Activity.
                        Intent teamsActivity = new Intent(DashboardActivity.this, UserTeamsActivity.class);
                        startActivity(teamsActivity);
                        break;
                    case R.id.action_settings:
                        Toast.makeText(DashboardActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        // Redirect to Dashboard Activity.
                        Intent settingsActivity = new Intent(DashboardActivity.this, SettingsActivity.class);
                        startActivity(settingsActivity);
                        break;
                    case R.id.action_tasks:
                        Toast.makeText(DashboardActivity.this, "Tasks", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        // Set value of User Points Card
        final TextView userPoints = findViewById(R.id.totalPoints);
        userPoints.setText(String.valueOf(getUserPoints()));

        // Set value of Date Text
        final TextView dashboardDate = findViewById(R.id.dashboardDate);
        String date = new SimpleDateFormat("EE dd MMMM yyyy", Locale.getDefault()).format(new Date());
        dashboardDate.setText(date);

        // Set value of Welcome Text
        final TextView welcomeText = findViewById(R.id.dashboardWelcome);
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "Couldn't find username??");
        String accessToken = sharedPrefs.getString("access", "");
        String refreshToken = sharedPrefs.getString("refresh", "");
        welcomeText.setText(String.format("Welcome, %s", email));

        // Get user id.
        HashMap[] parsedToken;
        // Format: 0 -> Header, 1 -> Payload, 2 -> Signature.
        parsedToken = JWTUtils.decoded(accessToken);
        userId = Integer.parseInt((String)parsedToken[1].get("user_id"));


    }

    @Override
    protected void onStart() {
        // Setup Upcoming Tasks Recycle View
        upcomingTasksRecycler = findViewById(R.id.upcomingTaskRecycler);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        upcomingTasksRecycler.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        upcomingTasksRecycler.setLayoutManager(layoutManager);
        // specify an adapter
        mAdapter = new TaskListAdapter(this, upcomingTaskList, this);
        upcomingTasksRecycler.setAdapter(mAdapter);
        // Idk why we need this? Does not seem to work....
        upcomingTasksRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);
        upcomingTaskList.clear();
        getTaskList();

        super.onStart();
    }



    //TODO: Actually get user score!
    private int getUserPoints(){
        int points = 0;
        return points;
    }

    // TODO: STORE/RETRIEVE CACHE
    private void getTaskList(){
        String taskListUrl = apiBaseUrl + "tasks?assignee=" + userId;
        JsonArrayRequest taskListRequest = new JsonArrayRequest(Request.Method.GET, taskListUrl, null, new  Response.Listener<JSONArray>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                // Parse responses into tasks.
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject task = response.getJSONObject(i);
                        upcomingTaskList.add(new Task(task));
                    }
                    mAdapter = new TaskListAdapter(getApplicationContext(), upcomingTaskList, DashboardActivity.this);
                    upcomingTasksRecycler.setAdapter(mAdapter);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                if (error instanceof TimeoutError){
                    Toast.makeText(DashboardActivity.this, "Server down Server down!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof NoConnectionError){
                    Toast.makeText(DashboardActivity.this, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError){
                    Toast.makeText(DashboardActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError){
                    Toast.makeText(DashboardActivity.this, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DashboardActivity.this, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
                }

            }
        }
        );

        //Add request to queue!
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(taskListRequest);
    }


    @Override
    public void onTaskClick(int position) {
        Task task = upcomingTaskList.get(position);
        Toast.makeText(this, task.m_title, Toast.LENGTH_SHORT).show();
        //TODO: Launch Task Info Fragment
    }
}
