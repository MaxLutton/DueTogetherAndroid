package com.example.plannerappandroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class Task {
    /*
    {
    "id": 23,
    "owner": "maxymax",
    "assignee": "notmax",
    "title": "New Title1",
    "completed": false,
    "createdDate": "2020-05-13T10:05:55.891306Z",
    "dueDate": "2020-12-12T11:11:00Z",
    "points": 2,
    "completedDate": null,
    "foo": null
     */
    int m_id;
    String m_owner;
    String m_assignee;
    String m_title;
    Boolean m_completed;
    ZonedDateTime m_createdDate;
    ZonedDateTime m_dueDate;
    int m_points;
    ZonedDateTime m_completedDate;

      @RequiresApi(api = Build.VERSION_CODES.O)
      public Task(JSONObject jsonInput) throws JSONException {
        Log.w("JsonStuff", jsonInput.toString()) ;
        if (jsonInput.has("id"))
            m_id = jsonInput.getInt("id");
        if (jsonInput.has("owner"))
            m_owner = jsonInput.getString("owner");
        if (jsonInput.has("assignee"))
            m_assignee = jsonInput.getString("assignee");
        if (jsonInput.has("title"))
            m_title = jsonInput.getString("title");
        if (jsonInput.has("completed"))
            m_completed = jsonInput.getBoolean("completed");
        try {
            if (jsonInput.has("createdDate") && !jsonInput.isNull("createdDate"))
                //m_createdDate = new SimpleDateFormat("YYYY-MM-ddThh:mm:ss.SSSZ").parse(jsonInput.getString("createdDate"));
                m_createdDate = ZonedDateTime.parse(jsonInput.getString("createdDate"));
            if (jsonInput.has("dueDate") && !jsonInput.isNull("dueDate"))
                m_dueDate = ZonedDateTime.parse(jsonInput.getString("dueDate"));
            if (jsonInput.has("completedDate") &&  !jsonInput.isNull("completedDate"))
                m_completedDate = ZonedDateTime.parse(jsonInput.getString("completedDate"));
        } catch (Exception e){
            e.printStackTrace();
        }
        if (jsonInput.has("points"))
            m_points = jsonInput.getInt("points");
    }
}
