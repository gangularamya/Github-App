package com.example.ramya.github;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
 * Activity to display user repositories
 */
public class MainActivity extends ActionBarActivity {

    private Button mSearchButton;
    private EditText mSearchContent;
    private ListView mListView;
    private ArrayAdapter<String> itemsAdapter = null;
    private Context context;
    private ArrayList<String> repositories;
    private String repositoryOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        mSearchButton = (Button)findViewById(R.id.search_button);
        mSearchContent = (EditText)findViewById(R.id.inputSearch);
        mListView = (ListView)findViewById(R.id.list_view);
        // search button click listener
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonRequest(mSearchContent.getText().toString());
            }
        });
        // list item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, IssuesActivity.class);
                intent.putExtra(AppConstants.REPO_NAME, repositories.get(position));
                intent.putExtra(AppConstants.REPO_OWNER, repositoryOwner);
                context.startActivity(intent);
            }
        });
    }

    /**
     * util method to make api request and bring user repositories
     * @param user
     */
    public void makeJsonRequest(String user){
        repositoryOwner =user;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = new StringBuilder(AppConstants.GITHUB_API_URL)
                .append("users/").append(repositoryOwner).append("/repos").toString();
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
    public void setData(JSONArray response){
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parseData(response));
        mListView.setAdapter(itemsAdapter);
    }

    /**
     * simple parser method to extract repositories
     * @param response
     * @return
     */
    public ArrayList<String> parseData(JSONArray response){
        repositories = new ArrayList<String>();
        if(response.length() != 0){
            for(int i=0; i<response.length(); i++){
                try {
                    String name = response.getJSONObject(i).getString("name");
                    repositories.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return repositories;
    }


}
