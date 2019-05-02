package com.example.currencyconverterlocationbased;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class NetworkRequest {

    private RequestQueue requestQueue;
    private NetworkRequest.NetworkResponse responseObject;
    public String URL;

    public NetworkRequest(Context context, String URL, NetworkRequest.NetworkResponse completionHandler) {
        requestQueue = Volley.newRequestQueue(context);
        responseObject = completionHandler;
        this.URL = URL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                successListener(),
                errorListener()
        );

        requestQueue.add(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> successListener() {
        Response.Listener<JSONObject> successObject = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseObject.networkResponse(response, null);
            }
        };

        return successObject;
    }

    private Response.ErrorListener errorListener() {
        Response.ErrorListener errorObject = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseObject.networkResponse(null, error);
            }
        };

        return  errorObject;
    }

    public interface NetworkResponse {
        public void networkResponse(JSONObject response, VolleyError error);
    }
}
