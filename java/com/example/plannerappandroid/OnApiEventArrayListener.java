package com.example.plannerappandroid;

import org.json.JSONArray;

public interface OnApiEventArrayListener {
    void onApiEvent(JSONArray resultArray, ApiRequestHandler.ApiRequestType requestType);
}
