package com.example.plannerappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserTeamsActivity extends AppCompatActivity implements TeamListAdapter.OnTeamListener {

    private RecyclerView teamsRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String accessToken = "";
    private Integer userId;
    final List<Team> teamList = new ArrayList<>();
    private ApiRequestHandler apiRequestHandler;
    private String TAG = "UserTeamsActivity";
    private String createTeamMode = null;
    String joinTeamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_teams);

        final ConstraintLayout createTeamCard = findViewById(R.id.newTeamCard);
        createTeamCard.setVisibility(View.INVISIBLE);
        final TextInputEditText newTeamName = findViewById(R.id.newTeamInput);
        Button submitTeamNameBtn = findViewById(R.id.sendNewTeamBtn);

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

        // Send POST request and redirect to new Team Page on Success
        apiRequestHandler = new ApiRequestHandler(accessToken, getApplicationContext());
        apiRequestHandler.setOnApiEventListener(new OnApiEventListener() {
            @Override
            public void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType) {
                if (requestType == ApiRequestHandler.ApiRequestType.CREATE_TEAM) {
                    try {
                        Team createdTeam = new Team(resultObject);
                        launchTeamActivity(createdTeam);
                    } catch (JSONException | IllegalAccessException ex) {
                        ex.printStackTrace();
                        Toast.makeText(UserTeamsActivity.this, "Couldn't do it!", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestType == ApiRequestHandler.ApiRequestType.GET_USER) {
                    try {
                        JSONArray teamArray = resultObject.getJSONArray("teams");
                        teamList.clear();
                        teamsRecycler.removeAllViews();
                        for (int i = 0; i < teamArray.length(); i++) {
                            JSONObject team = teamArray.getJSONObject(i);
                            teamList.add(new Team(team));
                            Log.w("Adding to team list", team.toString());
                        }
                        mAdapter = new TeamListAdapter(getApplicationContext(), teamList, UserTeamsActivity.this);
                        teamsRecycler.setAdapter(mAdapter);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (requestType == ApiRequestHandler.ApiRequestType.CREATE_TEAM_REQUEST) {
                    Log.w(TAG, "Succesfully sent request to join the team");
                }
            }
        });
        apiRequestHandler.setOnApiEventArrayListener(new OnApiEventArrayListener() {
            @Override
            public void onApiEvent(JSONArray resultArray, ApiRequestHandler.ApiRequestType requestType) {
                if (requestType == ApiRequestHandler.ApiRequestType.GET_TEAMS) {
                    int targetTeamId = -1;
                    for (int i = 0; i < resultArray.length(); i++) {
                        try {
                            JSONObject tempObject = resultArray.getJSONObject(i);
                            if (tempObject.getString("name").equals(joinTeamName)){
                                targetTeamId = tempObject.getInt("id");
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (targetTeamId != -1) {
                        apiRequestHandler.createTeamRequest(targetTeamId);
                    } else {
                        Toast.makeText(context, "Could not find a team with that name.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        EditText cardEditText = findViewById(R.id.newTeamInput);
        final Button createTeamBtn = findViewById(R.id.create_team_btn);
        createTeamBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                cardEditText.setText("");
                createTeamCard.setVisibility(View.VISIBLE);
                cardEditText.setHint("New Team Name:");
                createTeamMode = "create";
            }
        });
        final Button joinTeamButton = findViewById(R.id.join_team_btn);
        joinTeamButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                cardEditText.setText("");
                createTeamCard.setVisibility(View.VISIBLE);
                cardEditText.setHint("Team Name:");
                createTeamMode = "join";
            }
        });

        submitTeamNameBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (!newTeamName.getText().toString().isEmpty()) {
                    if (createTeamMode.equals("create")) {
                        Log.w(TAG, "Creating new team with name: " + newTeamName.getText());
                        apiRequestHandler.createTeam(newTeamName.getText().toString());
                    } else {
                        Log.w(TAG, "Creating request to join team with name: " + newTeamName.getText());
                        joinTeamName = newTeamName.getText().toString();
                        try {
                            apiRequestHandler.getTeamId(joinTeamName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                createTeamCard.setVisibility(View.INVISIBLE);
            }});

        ConstraintLayout activityLayout = findViewById(R.id.user_teams_layout);
        activityLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (createTeamCard.getVisibility() != View.INVISIBLE) {
                    createTeamCard.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        // Setup Upcoming Tasks Recycle View
        teamsRecycler = findViewById(R.id.team_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        teamsRecycler.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        teamsRecycler.setLayoutManager(layoutManager);
        // specify an adapter
        mAdapter = new TeamListAdapter(this, teamList, this);
        teamsRecycler.setAdapter(mAdapter);
        // Idk why we need this? Does not seem to work....
        teamsRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);
        teamList.clear();
        // Get user details to fill Team list
        apiRequestHandler.getUser(userId);

        super.onStart();
    }

    private void launchTeamActivity(Team team) throws IllegalAccessException {
        Intent teamDetailActivity = new Intent(UserTeamsActivity.this, TeamDetailActivity.class);
        teamDetailActivity.putExtra("team", team);
        startActivity(teamDetailActivity);
    }

    @Override
    public void onTeamClick(int position) {
        Team team = teamList.get(position);
        try {
            launchTeamActivity(team);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
