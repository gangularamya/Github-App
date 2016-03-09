package com.example.ramya.github;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class IssuesActivity extends ActionBarActivity {

    private ListView mListView;
    ArrayAdapter<String> itemsAdapter = null;
    Context context;
    ArrayList<String> issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_issues);
        mListView = (ListView)findViewById(R.id.issues_list_view);

        Intent intent = getIntent();
        if (null != intent) {
            String reponame = intent.getStringExtra(MainActivity.REPO_NAME);
            String repoowner = intent.getStringExtra(MainActivity.REPO_OWNER);
            String url ="https://api.github.com/repos/"+repoowner+"/"+reponame+"/issues";
            makeJsonRequest(url);
        }


    }

    public void makeJsonRequest(String url){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a JSON response from the provided URL.
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Github", "success");
                        setData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Github", "error" + error.getMessage());

            }
        });

        // Add the request to the RequestQueue.
        queue.add(req);
    }

    public void setData(JSONArray response){

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parseData(response));
        mListView.setAdapter(itemsAdapter);

    }


    public ArrayList<String> parseData(JSONArray response){
        issues = new ArrayList<String>();
        if(response.length() != 0){
            for(int i=0; i<response.length(); i++){
                try {
                    String issueTitle = response.getJSONObject(i).getString("title");
//                    Log.d("Github" ,"" +name);
                    issues.add(issueTitle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return issues;
    }




}
