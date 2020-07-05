package com.example.plannerappandroid;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Team {
    String m_name;
    String m_owner;
    List<String> m_members =  new ArrayList<>();
    String m_id;

    public Team(JSONObject jsonInput) throws JSONException {
        Log.w("JsonStuff", jsonInput.toString()) ;
        if (jsonInput.has("name")){
            this.m_name = jsonInput.getString("name");
        }
        if (jsonInput.has("team_owner")){
            this.m_owner = jsonInput.getString("team_owner");
        }
        if (jsonInput.has("team_members")){
            JSONArray memberList = jsonInput.getJSONArray("team_members");
            for (int i = 0; i < memberList.length(); i++){
                String member = memberList.getString(i);
                m_members.add(member);
            }
        }
        if (jsonInput.has("id")){
            this.m_id = jsonInput.getString("id");
        }
    }
}
