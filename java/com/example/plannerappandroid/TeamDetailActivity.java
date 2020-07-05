package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TeamDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        Intent intent = getIntent();
        Team team = intent.getParcelableExtra("team");
        Toast.makeText(this, "Got Team " + team.m_name, Toast.LENGTH_SHORT).show();

    }
}
