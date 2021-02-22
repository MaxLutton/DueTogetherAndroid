package com.example.plannerappandroid;

import org.json.JSONObject;

public interface OnApiEventListener {
    void onApiEvent(JSONObject resultObject, ApiRequestHandler.ApiRequestType requestType);
}

