package com.example.plannerappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TeamDetailActivity extends AppCompatActivity implements TeamMemberListAdapter.OnTeamMemberListener, TaskListAdapter.OnTaskListener {

    private RecyclerView membersRecycler;
    private RecyclerView teamTasksRecycler;
    private RecyclerView.Adapter mTasksAdapter;
    private RecyclerView.LayoutManager tasksLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private String accessToken = "";
    private Integer userId;
    private final String TAG = "TeamDetailActivity";
    private Boolean isTeamOwner = false;
    Team team;
    private TaskFragment mTaskFragment;
    private ConstraintLayout teamDetailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        teamDetailLayout = findViewById(R.id.team_layout);
        teamDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Clicked background.");
                if (mTaskFragment != null && mTaskFragment.isVisible()){
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.anim.fade_out)
                            .hide(mTaskFragment)
                            .commit();
                }
            }
        });

        Intent intent = getIntent();
        team = intent.getParcelableExtra("team");
        Toast.makeText(this, "Got Team " + team.m_name, Toast.LENGTH_SHORT).show();

        TextView teamNameText = findViewById(R.id.teamNameText);
        teamNameText.setText(team.m_name);

        TextView teamPointsText = findViewById(R.id.teamPoints);
        teamPointsText.setText("Points: " + String.valueOf(team.m_teamPoints));
        Toast.makeText(this, String.valueOf(team.m_teamPoints), Toast.LENGTH_SHORT).show();
        // Get Token.
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final String email = sharedPrefs.getString("email", "Couldn't find username??");
        accessToken = sharedPrefs.getString("access", "");
        String refreshToken = sharedPrefs.getString("refresh", "");
        // Get user id.
        HashMap[] parsedToken;
        // Format: 0 -> Header, 1 -> Payload, 2 -> Signature.
        assert accessToken != null;
        parsedToken = JWTUtils.decoded(accessToken);
        userId = Integer.parseInt((String) Objects.requireNonNull(parsedToken[1].get("user_id")));

        BottomNavigationView teamControlBar = findViewById(R.id.teamControlBar);
        if (team.m_owner.equals(email)){
            isTeamOwner = true;
            teamControlBar.setVisibility(View.VISIBLE);
            teamControlBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_requests:
                            Toast.makeText(TeamDetailActivity.this, "Checking team requests", Toast.LENGTH_SHORT).show();
                            // Redirect to Dashboard Activity.
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            MemberRequestFragment requestFragment = MemberRequestFragment.newInstance("a", "b");
                            ft.replace(R.id.fragmentPlaceholder, requestFragment);
                            ft.setCustomAnimations(android.R.animator.fade_in, android.R.anim.fade_out);
                            ft.addToBackStack(null);
                            ft.commit();
                            FrameLayout fragmentPlaceholder = findViewById(R.id.fragmentPlaceholder);
                            fragmentPlaceholder.bringToFront();
                            break;
                        case R.id.action_tasks:
                            Toast.makeText(TeamDetailActivity.this, "Tasks", Toast.LENGTH_SHORT).show();
                            Intent newTaskIntent = new Intent(TeamDetailActivity.this, CreateTaskActivity.class);
                            newTaskIntent.putExtra("team", team);
                            newTaskIntent.putExtra("email", email);
                            startActivity(newTaskIntent);
                            break;
                        case R.id.action_calendar:
                            Toast.makeText(context, "Calendar", Toast.LENGTH_SHORT).show();
                            Intent calendarActivity = new Intent(TeamDetailActivity.this, CalendarActivity.class);
                            calendarActivity.putParcelableArrayListExtra("tasks", new ArrayList<>(team.m_tasks));
                            startActivity(calendarActivity);
                    }
                    return true;
                }
            });
        }
        else {
            teamControlBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onStart() {
        // Setup Members Recycle View
        membersRecycler = findViewById(R.id.teamMemberRecycler);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        membersRecycler.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        membersRecycler.setLayoutManager(layoutManager);
        // specify an adapter
        for (String m : team.m_members){
            Log.w(TAG, m);
        }
        mAdapter = new TeamMemberListAdapter(this, team.m_members, this);
        membersRecycler.setAdapter(mAdapter);
        // Idk why we need this? Does not seem to work....
        membersRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);

        // Setup Team Tasks Recycle view
        teamTasksRecycler = findViewById(R.id.teamTasksRecycler);
        teamTasksRecycler.setHasFixedSize(true);
        tasksLayoutManager = new LinearLayoutManager(this);
        teamTasksRecycler.setLayoutManager(tasksLayoutManager);
        for (Task t: team.m_tasks){
            Log.w(TAG, t.m_title + ": in team tasks list");
        }
        mTasksAdapter = new TaskListAdapter(this, team.m_tasks, this);
        teamTasksRecycler.setAdapter(mTasksAdapter);
        teamTasksRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);

        // If team owner, query for team member requests
        if (isTeamOwner) {

        }

        super.onStart();
    }

    @Override
    public void onTeamMemberClick(int position) {
        String member = team.m_members.get(position);
        Toast.makeText(this, member, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskClick(int position) {
        Task task = team.m_tasks.get(position);
        Toast.makeText(this, task.m_title, Toast.LENGTH_SHORT).show();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = team.m_tasks.indexOf(task); i < team.m_tasks.size(); i++){
            tasks.add(team.m_tasks.get(i));
        }
        for (int i = 0; i <team.m_tasks.indexOf(task); i++){
            tasks.add(team.m_tasks.get(i));
        }
        mTaskFragment = TaskFragment.newInstance(tasks, 0);
        ft.replace(R.id.fragmentPlaceholder, mTaskFragment);
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.anim.fade_out);
        ft.addToBackStack(null);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        FrameLayout fragmentPlaceholder = findViewById(R.id.fragmentPlaceholder);
        fragmentPlaceholder.bringToFront();
    }

    private HashMap<String, String> getTeamMemberRequests(){
        String teamRequestUrl = apiBaseUrl + "teams/" + team.m_id + "/team_request/";
        HashMap<String, String> requestIdToName = new HashMap<String, String>();
        JsonArrayRequest teamMemberRequest = new JsonArrayRequest(Request.Method.GET, teamRequestUrl, null, new  Response.Listener<JSONArray>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                // Parse responses into tasks.
                try {
                    Log.w(TAG, response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        String username = getUserName(response.getJSONObject(i).get("from_user").toString());
                        requestIdToName.put(response.getJSONObject(i).get("id").toString(), username);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                if (error instanceof TimeoutError){
                    Toast.makeText(TeamDetailActivity.this, "Server down Server down!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof NoConnectionError){
                    Toast.makeText(TeamDetailActivity.this, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError){
                    Toast.makeText(TeamDetailActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError){
                    Toast.makeText(TeamDetailActivity.this, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(TeamDetailActivity.this, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
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
        };
        return requestIdToName;
    }

    //TODO: Implement this
    String getUserName(String id) {
        return "Username for " + id;
    }
}
