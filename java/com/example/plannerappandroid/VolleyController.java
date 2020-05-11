package com.example.plannerappandroid;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// Singleton class for Volley RequestView.
public class VolleyController    {
    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    // Constructor. Sets requestQueue object.
    private VolleyController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyController getInstance(Context context) {
        // If instance is not available, create it. If available, reuse and return the object.
        if (mInstance == null) {
            mInstance = new VolleyController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key. It should not be activity context,
            // or else RequestQueue won’t last for the lifetime of your app
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public  void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

}