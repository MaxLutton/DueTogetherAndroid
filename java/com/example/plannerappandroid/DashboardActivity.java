package com.example.plannerappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.TimeUnit;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.OnTaskListener {
    private RecyclerView upcomingTasksRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private int userId;
    private String accessToken;
    private String TAG = "DashboardActivity";
    final List<Task> upcomingTaskList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set value of Date Text
        final TextView dashboardDate = findViewById(R.id.dashboardDate);
        String date = new SimpleDateFormat("EE dd MMMM yyyy", Locale.getDefault()).format(new Date());
        dashboardDate.setText(date);

        // Set value of Welcome Text
        final TextView welcomeText = findViewById(R.id.dashboardWelcome);
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String email = sharedPrefs.getString("email", "Couldn't find username??");
        accessToken = sharedPrefs.getString("access", "");
        String refreshToken = sharedPrefs.getString("refresh", "");
        welcomeText.setText(String.format("Welcome, %s", email));

        // Get user id.
        HashMap[] parsedToken;
        // Format: 0 -> Header, 1 -> Payload, 2 -> Signature.
        parsedToken = JWTUtils.decoded(accessToken);
        userId = Integer.parseInt((String)parsedToken[1].get("user_id"));

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
                        Intent createTaskActivity = new Intent(DashboardActivity.this, CreateTaskActivity.class);
                        createTaskActivity.putExtra("email", email);
                        startActivity(createTaskActivity);
                        break;
                }
                return true;
            }
        });
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


    // TODO: STORE/RETRIEVE CACHE
    private void getTaskList(){
        //String taskListUrl = apiBaseUrl + "tasks?assignee=" + userId;
        String taskListUrl = apiBaseUrl + "users/" + userId + "/";
        JsonObjectRequest taskListRequest = new JsonObjectRequest(Request.Method.GET, taskListUrl, null, new  Response.Listener<JSONObject>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                // Parse responses into tasks.
                try {
                    Log.w(TAG, response.toString());

                    JSONObject userProfile = response.getJSONObject("profile");
                    Integer userPointsVal = userProfile.getInt("user_total_points");
                    TextView userPoints = findViewById(R.id.totalPoints);
                    userPoints.setText(userPointsVal.toString());
                    Log.w(TAG, "Set value to: " + userPointsVal.toString());
                    List<Point> pointsList = new ArrayList<>();
                    JSONArray userPointsArray = userProfile.getJSONArray("user_points");
                    for (int i = 0; i < userPointsArray.length(); i++){
                        pointsList.add(new Point(userPointsArray.getJSONObject(i)));
                    }
                    setupChart(pointsList);

                    JSONArray assignedTasks = response.getJSONArray("assigned_tasks");
                    for (int i = 0; i <assignedTasks.length(); i++) {
                        JSONObject task = assignedTasks.getJSONObject(i);
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
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String auth = "Bearer "
                        + accessToken;
                headers.put("Authorization", auth);
                return headers;
            }
        };;

        //Add request to queue!
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(taskListRequest);
    }


    @Override
    public void onTaskClick(int position) {
        Task task = upcomingTaskList.get(position);
        Toast.makeText(this, task.m_title, Toast.LENGTH_SHORT).show();
        //TODO: Launch Task Info Fragment
    }

    public void setupChart(List<Point> points){
        // Setup line chart
        LineChart chart = findViewById(R.id.chart);
        List<Entry> chartData = new ArrayList<Entry>();
        for (Point p : points){
            if (Days.daysBetween(p.date.toLocalDate(), DateTime.now().toLocalDate()).getDays() < 7) {
                chartData.add(p.getEntry(EntryType.USER));
            }
        }
        LineDataSet dataSet = new LineDataSet(chartData, "Points");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setHighLightColor(Color.rgb(244,117,117));
        dataSet.setDrawCircleHole(false);
        dataSet.setLineWidth(5f);


        //dataSet.setColor();
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueTextSize(9f);
        chart.setData(lineData);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.invalidate(); // Refreshses the chart
    }
}
