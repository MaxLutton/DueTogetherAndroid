package com.example.plannerappandroid;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.SimpleDateFormat;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class Task implements Parcelable {
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
    DateTime m_createdDate;
    DateTime m_dueDate;
    int m_points;
    DateTime m_completedDate;
    String TAG = "Task";

      @RequiresApi(api = Build.VERSION_CODES.O)
      public Task(JSONObject jsonInput) throws JSONException {
        Log.w(TAG, jsonInput.toString()) ;
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
            if (!jsonInput.getString("createdDate").equals("null")){
                m_createdDate = new DateTime(jsonInput.getString("createdDate"));
            }
            if (!jsonInput.getString("dueDate").equals("null")){
                m_dueDate = new DateTime(jsonInput.getString("dueDate"));
            }
            if (!jsonInput.getString("completedDate").equals("null")){
                m_completedDate = new DateTime(jsonInput.getString("completedDate"));
            }
            Log.w(TAG, "Parsed all dates for " + m_id);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (jsonInput.has("points"))
            m_points = jsonInput.getInt("points");
    }

    protected Task(Parcel in) {
        m_id = in.readInt();
        m_owner = in.readString();
        m_assignee = in.readString();
        m_title = in.readString();
        byte tmpM_completed = in.readByte();
        m_completed = tmpM_completed == 0 ? null : tmpM_completed == 1;
        m_points = in.readInt();
        long completedDate = in.readLong();
        if (completedDate != 0){
            m_completedDate = new DateTime(completedDate);
        }
        long createdDate = in.readLong();
        if (createdDate != 0){
            m_createdDate = new DateTime(createdDate);
        }
        long dueDate = in.readLong();
        if (dueDate != 0){
            m_dueDate = new DateTime(dueDate);
        }

    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(m_id);
        parcel.writeString(m_owner);
        parcel.writeString(m_assignee);
        parcel.writeString(m_title);
        parcel.writeByte((byte) (m_completed == null ? 0 : m_completed ? 1 : 2));
        parcel.writeInt(m_points);
        if (m_completedDate != null) {
            parcel.writeLong(m_completedDate.getMillis());
        }
        else {
            parcel.writeLong(0);
        }
        if (m_createdDate != null){
            parcel.writeLong(m_createdDate.getMillis());
        }
        else{
            parcel.writeLong(0);
        }
        if (m_dueDate != null){
            parcel.writeLong(m_dueDate.getMillis());
        }
        else {
            parcel.writeLong(0);
        }
    }
}
