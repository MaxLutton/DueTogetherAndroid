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
    private String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private String accessToken = "";
    private Integer userId;
    final List<Team> teamList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_teams);

        final ConstraintLayout createTeamCard = findViewById(R.id.newTeamCard);
        createTeamCard.setVisibility(View.INVISIBLE);
        final TextInputEditText newTeamName = findViewById(R.id.newTeamInput);
        Button submitTeamNameBtn = findViewById(R.id.sendNewTeamBtn);
        submitTeamNameBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(UserTeamsActivity.this, "Creating new Team with name: " + newTeamName.getText(), Toast.LENGTH_SHORT).show();
                // Send POST request. Then redirect to new Team Page if successful.
                String createTeamUrl = apiBaseUrl + "teams/";
                JSONObject body = new JSONObject();
                try {
                    body.put("name", newTeamName.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserTeamsActivity.this, "Invalid team name!", Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest createTeamRequest = new JsonObjectRequest(Request.Method.POST, createTeamUrl, body, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("JsonStuff", response.toString());
                        try {
                            Team createdTeam = new Team(response);
                            launchTeamActivity(createdTeam);
                        } catch (JSONException | IllegalAccessException ex) {
                            ex.printStackTrace();
                            Toast.makeText(UserTeamsActivity.this, "Couldn't do it!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if (error instanceof TimeoutError) {
                            Toast.makeText(UserTeamsActivity.this, "Server down Server down!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(UserTeamsActivity.this, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(UserTeamsActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(UserTeamsActivity.this, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserTeamsActivity.this, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String auth = "Bearer "
                                + accessToken;
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
                VolleyController.getInstance(getApplicationContext()).addToRequestQueue(createTeamRequest);
            }});
        final Button createTeamBtn = findViewById(R.id.create_team_btn);
        createTeamBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createTeamCard.setVisibility(View.VISIBLE);
            }
        });

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
        getTeamList();

        super.onStart();
    }

    // TODO: STORE/RETRIEVE CACHE
    protected void getTeamList(){
        String teamListUrl = apiBaseUrl + "users/" + userId;
        JsonObjectRequest teamListRequest = new JsonObjectRequest(Request.Method.GET, teamListUrl, null, new  Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.w("JsonStuff", response.toString()) ;
                // Parse responses into teams.
                try {
                    /*
                    ...
                        "teams": [
                            {
                                "id": 2,
                                "team_owner": "maxymax",
                                "team_members": [
                                    "maxymax",
                                    "notmax"
                                ],
                                "name": "TEAM DOS"
                            },
                        ]
                        ...
                     */
                    JSONArray teamArray = response.getJSONArray("teams");
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
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                if (error instanceof TimeoutError){
                    Toast.makeText(UserTeamsActivity.this, "Server down Server down!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof NoConnectionError){
                    Toast.makeText(UserTeamsActivity.this, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError){
                    Toast.makeText(UserTeamsActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError){
                    Toast.makeText(UserTeamsActivity.this, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UserTeamsActivity.this, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
                }

            }
        })
        {@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String auth = "Bearer "
                    + accessToken;
            headers.put("Authorization", auth);
            return headers;
        }};

        //Add request to queue!
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(teamListRequest);
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
