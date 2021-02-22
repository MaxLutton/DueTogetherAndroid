package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CreateTaskActivity extends AppCompatActivity {
    Calendar dueDateCalendar = Calendar.getInstance();
    Spinner assigneeSpinner;
    Spinner pointsSpinner;
    EditText dueDateText;
    Team team;
    Task existingTask;
    String email;
    String assignee;
    String points;
    String TAG = "Create Task Activity";
    String pattern = "yyyy-MM-dd";
    EditText title;
    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Get Token.
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "Couldn't find username??");
        accessToken = sharedPrefs.getString("access", "");
        String refreshToken = sharedPrefs.getString("refresh", "");

        Intent intent = getIntent();
        TextView activityHeader = findViewById(R.id.newTaskText);

        team = intent.getParcelableExtra("team");
        if (team != null){
            activityHeader.setText("Create New Task for " + team.m_name);
        }
        else {
            activityHeader.setText("Create New Task");
        }

        email = intent.getStringExtra("email");

        title = findViewById(R.id.newTaskTitle);
        dueDateText = findViewById(R.id.dueDateText);

        // In case we are editing an existing task, we need to set default values
        existingTask = intent.getParcelableExtra("task");
        if (existingTask != null){
            activityHeader.setText("Update Task: " + existingTask.m_title);
            title.setText(existingTask.m_title);
            dueDateText.setText(existingTask.m_dueDate.toString(pattern));
            dueDateCalendar = existingTask.m_dueDate.toCalendar(Locale.getDefault());
        }

        final DatePickerDialog.OnDateSetListener dueDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dueDateCalendar.set(Calendar.YEAR, i);
                dueDateCalendar.set(Calendar.MONTH, i1);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }
        };

        dueDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateTaskActivity.this, dueDate, dueDateCalendar
                        .get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH),
                        dueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        assigneeSpinner = findViewById(R.id.taskAssigneeSpinner);
        ArrayAdapter<String> assigneeAdapter;
        if (team != null){
            // Re-order members to put current assignee at the top.
            if (existingTask != null){
                int memberIndex = team.m_members.indexOf(existingTask.m_assignee);
                if (memberIndex != -1) {
                    team.m_members.remove(memberIndex);
                    team.m_members.add(0, existingTask.m_assignee);
                }
            }
            assigneeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, team.m_members);
        }
        else {
            ArrayList<String> listOfMe = new ArrayList<>();
            if (existingTask != null){
                listOfMe.add(existingTask.m_assignee);
            } else{
                listOfMe.add(email);
            }
            assigneeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listOfMe);
        }
        assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assigneeSpinner.setAdapter(assigneeAdapter);
        assigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assignee = (String)adapterView.getItemAtPosition(i);
                Log.w(TAG, "Setting assignee to: " + assignee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pointsSpinner = findViewById(R.id.taskPointsSpinner);
        ArrayAdapter<String> pointsAdapter;
        ArrayList<String> pointsList = new ArrayList<>();
        // Set top value in list to current point value.
        if (existingTask != null){
            pointsList.add(String.valueOf(existingTask.m_points));
            if (existingTask.m_points != 1){
                pointsList.add("1");
            }
            if (existingTask.m_points != 2){
                pointsList.add("2");
            }
            if (existingTask.m_points != 3){
                pointsList.add("3");
            }
        }
        else {
            pointsList.add("1");
            pointsList.add("2");
            pointsList.add("3");
        }
        pointsAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, pointsList);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointsSpinner.setAdapter(pointsAdapter);
        pointsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                points = (String) adapterView.getItemAtPosition(i);
                Log.w(TAG, "Setting points to: " + points);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ApiRequestHandler apiRequestHandler = new ApiRequestHandler(accessToken, getApplicationContext());
        apiRequestHandler.setOnApiEventListener(new OnApiEventListener() {
            @Override
            public void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType) {
                if (requestType == ApiRequestHandler.ApiRequestType.CREATE_TASK) {
                    Toast.makeText(CreateTaskActivity.this, "Created new Task!", Toast.LENGTH_SHORT).show();
                    finish();
                    //TODO: Decide how to get new task in Team Task List. Maybe just pass the returned JSONObject in the intent to avoid extra requests.
                } else if (requestType == ApiRequestHandler.ApiRequestType.DELETE_TASK) {
                    // response
                    Toast.makeText(CreateTaskActivity.this, "Deleted task!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, resultObject.toString());
                } else if (requestType == ApiRequestHandler.ApiRequestType.UPDATE_TASK) {
                    Toast.makeText(CreateTaskActivity.this, "Updated Task!", Toast.LENGTH_SHORT).show();
                    finish();
                    //TODO: Decide how to get new task in Team Task List. Maybe just pass the returned JSONObject in the intent to avoid extra requests.
                }
            }
        });
        Button submitBtn = findViewById(R.id.submitNewTaskBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject body = new JSONObject();
                if (team != null){
                    try {
                        body.put("team", team.m_name);
                    } catch (JSONException e) {
                        Log.e(TAG, "Couldn't add team!");
                    }
                }
                if (points == null){
                    if (existingTask != null){
                        points = String.valueOf(existingTask.m_points);
                    } else{
                        points = "1";
                    }
                }
                if (title.getText().toString().equals("")){
                    if (existingTask != null){
                        title.setText(existingTask.m_title);
                    } else{
                        Toast.makeText(CreateTaskActivity.this, "Task Name is Required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (assignee != null){
                    try {
                        body.put("assignee", assignee);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                if (dueDateCalendar != null) {
                    try {
                        String dueDate = simpleDateFormat.format(dueDateCalendar.getTime()) + "T23:59";
                        body.put("dueDate", dueDate);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to add date!");
                        e.printStackTrace();
                    }
                } else{
                    if (existingTask != null){
                        // String dueDate = simpleDateFormat.format(existingTask.m_dueDate)
                        try {
                            body.put("dueDate", existingTask.m_dueDate.toString());
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to add date!");
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(CreateTaskActivity.this, "Due Date Required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    body.put("title", title.getText().toString());
                    body.put("points", points);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.w(TAG, body.toString());

                if (existingTask != null){
                    apiRequestHandler.performTaskRequest(body, ApiRequestHandler.ApiRequestType.UPDATE_TASK, existingTask.m_id);
                } else {
                    apiRequestHandler.performTaskRequest(body, ApiRequestHandler.ApiRequestType.CREATE_TASK, -1);
                }
            }
        });

        Button deleteTaskButton = findViewById(R.id.deleteTaskButton);
        if (existingTask != null){
            deleteTaskButton.setVisibility(View.VISIBLE);
        }
        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Deleting task.");
                if (existingTask != null)
                {
                    apiRequestHandler.performTaskRequest(null, ApiRequestHandler.ApiRequestType.DELETE_TASK, existingTask.m_id);
                }
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dueDateText.setText(sdf.format(dueDateCalendar.getTime()));
    }

}

