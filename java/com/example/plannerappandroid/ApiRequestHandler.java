package com.example.plannerappandroid;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class ApiRequestHandler {
    private final String TAG = "ApiRequestHandler";
    private final String apiBaseUrl = "http://10.0.2.2:8000/api/";
    //private final String apiBaseUrl = "http://desktop-div0tj6:8000/api/";
    private String mToken = "";
    private Context mAppContext;
    private OnApiEventListener mOnApiEventListener;
    private OnApiEventArrayListener mOnApiEventArrayListener;

    public enum ApiRequestType {
        GET_USER, LOGIN, COMPLETE_TASK, CREATE_TEAM, CREATE_TASK, DELETE_TASK, UPDATE_TASK, GET_TEAM_REQUESTS,
        RESPOND_TO_REQUEST, CREATE_TEAM_REQUEST, GET_TEAMS, CREATE_USER, UPDATE_FCM_TOKEN
    }

    public ApiRequestHandler(String token, Context appContext) {
        mToken = token;
        mAppContext = appContext;
    }

    public ApiRequestHandler(Context appContext) {
        mAppContext = appContext;
    }

    public void setOnApiEventListener(OnApiEventListener listener) {
        mOnApiEventListener = listener;
    }

    public void setOnApiEventArrayListener(OnApiEventArrayListener listener) {
        mOnApiEventArrayListener = listener;
    }

    private void defaultOnErrorResponse( VolleyError error){
        // TODO: Handle error
        if (error instanceof TimeoutError){
            Toast.makeText(mAppContext, "Server down Server down!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof NoConnectionError){
            Toast.makeText(mAppContext, "Please ensure wifi or data is enabled.", Toast.LENGTH_SHORT).show();
        }
        else if (error instanceof AuthFailureError){
            Toast.makeText(mAppContext, "Invalid credentials.", Toast.LENGTH_SHORT).show();
        }
        else if (error instanceof ServerError){
            Toast.makeText(mAppContext, "Server error! No bueno...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mAppContext, "Other error... Bad stuff man.", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<String, String> createAuthHeaders(){
        Map<String, String> headers = new HashMap<>();
        String auth = "Bearer "
                + mToken;
        headers.put("Authorization", auth);
        return headers;
    }

    public void getUser(int userId) {
        String userUrl = apiBaseUrl + "users/" + userId + "/";
        JsonObjectRequest getUserRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null, new  Response.Listener<JSONObject>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                // Parse responses into tasks.
                Log.w(TAG + "Get User Response: ", response.toString());
                mOnApiEventListener.onApiEvent(response, ApiRequestType.GET_USER);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() {
                return createAuthHeaders();
            }
        };

        VolleyController.getInstance(mAppContext).addToRequestQueue(getUserRequest);
    }

    public void loginUser(String username, String password) {
        //Setup request.
        String tokenUrl = apiBaseUrl + "token/";
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, tokenUrl, body, new  Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG, response.toString());
                mOnApiEventListener.onApiEvent(response, ApiRequestType.LOGIN);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        });

        VolleyController.getInstance(mAppContext).addToRequestQueue(loginRequest);
    }

    public void markTaskCompleted(int taskId) {
        String completeTaskUrl = apiBaseUrl + "tasks/" + taskId + "/";
        JSONObject body = new JSONObject();
        try {
            body.put("completed", true);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in creating Request Body.");
            return;
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PATCH, completeTaskUrl, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mOnApiEventListener.onApiEvent(response, ApiRequestType.COMPLETE_TASK);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthHeaders();
            }
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(updateRequest);
    }

    public void createTeam(String teamName) {
        String createTeamUrl = apiBaseUrl + "teams/";
        JSONObject body = new JSONObject();
        try {
            body.put("name", teamName);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mAppContext, "Invalid team name!", Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest createTeamRequest = new JsonObjectRequest(Request.Method.POST, createTeamUrl, body, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.w("Team Created: ", response.toString());
                mOnApiEventListener.onApiEvent(response, ApiRequestType.CREATE_TEAM);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createAuthHeaders();
            }
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(createTeamRequest);
    }

    public void performTaskRequest(JSONObject body, ApiRequestType requestType, int taskId) {
        String taskUrl = apiBaseUrl + "tasks/";
        int methodType;
        if (requestType == ApiRequestType.CREATE_TASK) {
            methodType = Request.Method.POST;
        } else if (requestType == ApiRequestType.UPDATE_TASK) {
            methodType = Request.Method.PATCH;
            taskUrl += taskId + "/";
        } else if (requestType == ApiRequestType.DELETE_TASK) {
            methodType = Request.Method.DELETE;
            taskUrl += taskId + "/";
        } else {
            Log.e(TAG, "Invalid Request Type.");
            return;
        }
        JsonObjectRequest taskRequest = new JsonObjectRequest(methodType, taskUrl, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG + "Task Response: ", response.toString());
                mOnApiEventListener.onApiEvent(response, requestType);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthHeaders();
            }
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(taskRequest);
    }

    public void getTeamRequests(String teamId) {
        String requestsUrl = apiBaseUrl + "teams/" + teamId + "/team_request/";
        JsonArrayRequest teamRequests = new JsonArrayRequest(Request.Method.GET, requestsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.w(TAG + "Get Team Requests Response: ", response.toString());
                mOnApiEventArrayListener.onApiEvent(response, ApiRequestType.GET_TEAM_REQUESTS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return createAuthHeaders();
            }
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(teamRequests);
    }

    public void respondToTeamRequest(int teamId, int requestId, boolean accept) {
        String requestsUrl = apiBaseUrl + "teams/" + teamId + "/team_request/" + requestId;
        if (accept) {
            requestsUrl += "/accept/";
        } else {
            requestsUrl += "/reject/";
        }
        JsonObjectRequest teamRequest = new JsonObjectRequest(Request.Method.POST, requestsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG, "Responded to the member request.");
                mOnApiEventListener.onApiEvent(response, ApiRequestType.RESPOND_TO_REQUEST);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() { return createAuthHeaders();}
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(teamRequest);
    }

    public void createTeamRequest(int teamId) {
        String requestUrl = apiBaseUrl + "teams/" + teamId + "/team_request/";
        JsonObjectRequest teamRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG, "Created member request.");
                mOnApiEventListener.onApiEvent(response, ApiRequestType.CREATE_TEAM_REQUEST);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() { return createAuthHeaders();}
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(teamRequest);
    }

    public void getTeamId(String teamName) throws JSONException {
        String teamsUrl = apiBaseUrl + "teams/";
        JsonArrayRequest teamsRequest = new JsonArrayRequest(Request.Method.GET, teamsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.w(TAG, "Got all teams");
                mOnApiEventArrayListener.onApiEvent(response, ApiRequestType.GET_TEAMS);
            }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        })  {
            @Override
            public Map<String, String> getHeaders() { return createAuthHeaders();}
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(teamsRequest);
    }

    public void createNewUser(String email, String password) throws JSONException {
        String userUrl = apiBaseUrl + "users/";
        JSONObject body = new JSONObject();
        body.put("username", email);
        body.put("password", password);
        JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.POST, userUrl, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG, "Created user.");
                mOnApiEventListener.onApiEvent(response, ApiRequestType.CREATE_USER);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        });
        VolleyController.getInstance(mAppContext).addToRequestQueue(userRequest);
    }

    public void updateFCMToken(String token, int userId) throws JSONException {
        String fcmUrl = apiBaseUrl + "devices/";
        JSONObject body = new JSONObject();
        body.put("registration_id", token);
        body.put("user", userId);
        body.put("type", "android");
        JsonObjectRequest fcmRequest = new JsonObjectRequest(Request.Method.POST, fcmUrl, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w(TAG, "Updated Firebase Token.");
                mOnApiEventListener.onApiEvent(response, ApiRequestType.UPDATE_FCM_TOKEN);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultOnErrorResponse(error);
            }
        } ) {
            @Override
            public Map<String, String> getHeaders() { return createAuthHeaders();}
        };
        VolleyController.getInstance(mAppContext).addToRequestQueue(fcmRequest);
    }

}
