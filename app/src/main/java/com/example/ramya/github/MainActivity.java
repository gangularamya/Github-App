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

public class MainActivity extends ActionBarActivity {

    public static final String REPO_NAME = "repoName";
    public static final String REPO_OWNER = "repoOwner";

    private Button mSearchButton;
    private EditText mSearchContent;
    private ListView mListView;
    ArrayAdapter<String> itemsAdapter = null;
    Context context;
    ArrayList<String> repositories;
    String repositoryOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        mSearchButton = (Button)findViewById(R.id.search_button);
        mSearchContent = (EditText)findViewById(R.id.inputSearch);
        mListView = (ListView)findViewById(R.id.list_view);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonRequest(mSearchContent.getText().toString());
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, IssuesActivity.class);
                intent.putExtra(REPO_NAME, repositories.get(position));
                intent.putExtra(REPO_OWNER, repositoryOwner);
                context.startActivity(intent);
            }
        });

    }

    public void makeJsonRequest(String user){
        repositoryOwner =user;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.github.com/users/"+repositoryOwner+"/repos";

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
        repositories = new ArrayList<String>();
        if(response.length() != 0){
            for(int i=0; i<response.length(); i++){
                try {
                    String name = response.getJSONObject(i).getString("name");
//                    Log.d("Github" ,"" +name);
                    repositories.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return repositories;
    }


}
