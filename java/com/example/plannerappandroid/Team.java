package com.example.plannerappandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Team  implements Parcelable {
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

    protected Team(Parcel in) {
        m_name = in.readString();
        m_owner = in.readString();
        m_members = in.createStringArrayList();
        m_id = in.readString();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(m_name);
        parcel.writeString(m_owner);
        parcel.writeStringList(m_members);
        parcel.writeString(m_id);
    }
}
