package com.example.plannerappandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TeamRequest implements Parcelable {
    String TAG = "TeamRequest";
    int mRequestId;
    int mToTeamId;
    int mFromUserId;
    String mFromUserName;

    public TeamRequest(JSONObject jsonInput) throws JSONException {
        Log.w(TAG, jsonInput.toString());
        this.mRequestId = jsonInput.getInt("id");
        this.mToTeamId = jsonInput.getInt("to_team");
        this.mFromUserId = jsonInput.getInt("from_user");
        this.mFromUserName = jsonInput.getString("from_user_name");
    }

    protected TeamRequest(Parcel in) {
        mRequestId = in.readInt();
        mToTeamId = in.readInt();
        mFromUserId = in.readInt();
        mFromUserName = in.readString();
    }

    public static final Creator<TeamRequest> CREATOR = new Creator<TeamRequest>() {
        @Override
        public TeamRequest createFromParcel(Parcel in) {
            return new TeamRequest(in);
        }

        @Override
        public TeamRequest[] newArray(int size) {
            return new TeamRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mRequestId);
        parcel.writeInt(mToTeamId);
        parcel.writeInt(mFromUserId);
        parcel.writeString(mFromUserName);
    }
}
