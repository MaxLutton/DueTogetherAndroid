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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamRequestActivity extends AppCompatActivity  implements TeamRequestListAdapter.OnRequestListener  {
    private RecyclerView requestsRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TeamRequest> requestList;
    private final String TAG = "TeamRequestActivity";
    private String accessToken;
    ApiRequestHandler teamRequestsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_request);

        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        accessToken = sharedPrefs.getString("access", "");
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();
        requestList = intent.getParcelableArrayListExtra("teamRequests");
        requestsRecycler = findViewById(R.id.team_request_recycler);
        requestsRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        requestsRecycler.setLayoutManager(layoutManager);
        mAdapter = new TeamRequestListAdapter(this, requestList, this);
        requestsRecycler.setAdapter(mAdapter);
        requestsRecycler.getRecycledViewPool().setMaxRecycledViews(0,0);

        teamRequestsHandler = new ApiRequestHandler(accessToken, getApplicationContext());
        teamRequestsHandler.setOnApiEventListener(new OnApiEventListener() {
            @Override
            public void onApiEvent(JSONObject result, ApiRequestHandler.ApiRequestType requestType) {
                Log.w(TAG, "Successfully sent responded to member request.");
                }
            });

        super.onStart();
    }

    @Override
    public void onRequestClick(int position, String accept) {
        Log.w(TAG, "Got the click");
        TeamRequest request = requestList.get(position);
        if (accept.equals("accept")) {
            Log.w(TAG, "Accepting new user: " +request.mFromUserName);
            teamRequestsHandler.respondToTeamRequest(request.mToTeamId, request.mRequestId, true);
            requestList.remove(position);
            requestsRecycler.removeViewAt(position);
        } else if (accept.equals("reject")) {
            Log.w(TAG, "Rejecting user: " + request.mFromUserName);
            teamRequestsHandler.respondToTeamRequest(request.mToTeamId, request.mRequestId, true);
            requestList.remove(position);
            requestsRecycler.removeViewAt(position);
        } else {
            Log.w(TAG, "Clicked on request for: " + request.mFromUserName);
        }
    }
}