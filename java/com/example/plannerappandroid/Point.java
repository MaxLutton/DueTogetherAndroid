package com.example.plannerappandroid;



import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

enum EntryType {
    USER,
    TEAM
}

public class Point {
    int value;
    DateTime date;
    int teamTotal = 0;
    int userTotal = 0;
    String TAG = "Point";
    public Point(JSONObject object) throws JSONException {
        Log.w(TAG, object.toString());
        value = object.getInt("value");
        teamTotal = object.getInt("current_total_team");
        userTotal = object.getInt("current_total_user");
        date = new DateTime(object.getString("date"));
    }

    // Convert Date field to an float for use in line chart.
    public Entry getEntry(EntryType type){
        float x = this.date.getMillis() / 1000f;
        Log.w(TAG, "Date as float: " + x);
        if (type == EntryType.TEAM){
            return new Entry(x, this.teamTotal);
        }
        else if (type == EntryType.USER){
            return new Entry(x, this.userTotal);
        }
        else {
            return new Entry(x, this.value);
        }

    }


}
