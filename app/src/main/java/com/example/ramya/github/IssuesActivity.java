package com.example.ramya.github;

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

/**
 * Activity to display issues in github repository
 */
public class IssuesActivity extends ActionBarActivity {

    private ListView mListView;
    private ArrayAdapter<String> itemsAdapter = null;
    private ArrayList<String> issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_issues);
        mListView = (ListView) findViewById(R.id.issues_list_view);

        Intent intent = getIntent();
        if (null != intent) {
            String reponame = intent.getStringExtra(AppConstants.REPO_NAME);
            String repoowner = intent.getStringExtra(AppConstants.REPO_OWNER);
            String issuesUrl = new StringBuilder(AppConstants.GITHUB_API_URL)
                    .append("repos/").append(repoowner)
                    .append("/").append(reponame).append("/issues").toString();
            makeJsonRequest(issuesUrl);
        }
    }

    /**
     * util method to make api request and bring issues in repository
     * @param url
     */
    public void makeJsonRequest(String url) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a JSON response from the provided URL.
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

    /**
     * Util method to set response data to listview
     * @param response
     */
    public void setData(JSONArray response) {
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parseData(response));
        mListView.setAdapter(itemsAdapter);
    }

    /**
     * simple parser method to extract issue title
     * @param response
     * @return
     */
    public ArrayList<String> parseData(JSONArray response) {
        issues = new ArrayList<String>();
        if (response.length() != 0) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    String issueTitle = response.getJSONObject(i).getString("title");
                    issues.add(issueTitle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return issues;
    }
}
