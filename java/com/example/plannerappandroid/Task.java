package com.example.plannerappandroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZonedDateTime;
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

    protected Task(Parcel in) {
        m_id = in.readInt();
        m_owner = in.readString();
        m_assignee = in.readString();
        m_title = in.readString();
        byte tmpM_completed = in.readByte();
        m_completed = tmpM_completed == 0 ? null : tmpM_completed == 1;
        m_points = in.readInt();
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
    }
}
