package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView upcomingTasksRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
        welcomeText.setText(String.format("Welcome, %s", email));

        // Get data for upcoming tasks.
        List<String> upcomingTaskList = new ArrayList<String>();
        upcomingTaskList.add("Task 1");
        upcomingTaskList.add("Task 2");
        upcomingTaskList.add("Task 3");
        upcomingTaskList.add("Task 4");
        upcomingTaskList.add("Task 5");
        upcomingTaskList.add("Task 6");
        upcomingTaskList.add("Task 7");
        upcomingTaskList.add("Task 8");
        upcomingTaskList.add("Task 9");
        upcomingTaskList.add("Task 10");

        // Setup Upcoming Tasks Recycle View
        upcomingTasksRecycler = findViewById(R.id.upcomingTaskRecycler);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        upcomingTasksRecycler.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        upcomingTasksRecycler.setLayoutManager(layoutManager);
        // specify an adapter
        mAdapter = new TaskListAdapter(this, upcomingTaskList);
        upcomingTasksRecycler.setAdapter(mAdapter);
        // Idk why we need this? Does not seem to work....
        upcomingTasksRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);




    }

    private int getUserPoints(){
        int points = 0;
        return points;
    }


}
