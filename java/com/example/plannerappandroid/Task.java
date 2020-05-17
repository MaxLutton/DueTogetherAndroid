package com.example.plannerappandroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.SimpleDateFormat;

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
    Date m_createdDate;
    Date m_dueDate;
    int m_points;
    Date m_completedDate;

    public Task(JSONObject jsonInput) throws JSONException {
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
            if (jsonInput.has("createdDate"))
                m_createdDate = new SimpleDateFormat("YYYY-MM-ddThh:mm:ss.SSSZ").parse(jsonInput.getString("createdDate"));
            if (jsonInput.has("dueDate"))
                m_dueDate = new SimpleDateFormat("YYYY-MM-ddThh:mm:ss.SSSZ").parse(jsonInput.getString("dueDate"));
            if (jsonInput.has("completedDate"))
                m_completedDate =new SimpleDateFormat("YYYY-MM-ddThh:mm:ss.SSSZ").parse(jsonInput.getString("completedDate"));
        } catch (Exception e){
            e.printStackTrace();
        }
        if (jsonInput.has("points"))
            m_points = jsonInput.getInt("points");
    }
}
