package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        Intent intent = getIntent();
        team = intent.getParcelableExtra("team");
        Toast.makeText(this, "Got Team " + team.m_name, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        // Get Token.
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "Couldn't find username??");
        accessToken = sharedPrefs.getString("access", "");
        String refreshToken = sharedPrefs.getString("refresh", "");

        // Get user id.
        HashMap[] parsedToken;
        // Format: 0 -> Header, 1 -> Payload, 2 -> Signature.
        assert accessToken != null;
        parsedToken = JWTUtils.decoded(accessToken);
        userId = Integer.parseInt((String) Objects.requireNonNull(parsedToken[1].get("user_id")));

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

        super.onStart();
    }
    void getTeamTasks(){
        //TODO: GET TEAM TASKS AND ASSOCIATE WITH MEMBERS.
    }

    @Override
    public void onTeamMemberClick(int position) {
        String member = team.m_members.get(position);
        Toast.makeText(this, member, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskClick(int position) {
        Task task = team.m_tasks.get(position);
        //TODO: Launch Task Fragment
        Toast.makeText(this, task.m_title, Toast.LENGTH_SHORT).show();
    }
}
