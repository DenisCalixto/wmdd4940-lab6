package edu.wmdd.lab6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String API_URL = "https://www.reddit.com/.json";
    private ListView redditListView;
    ArrayList<RedditItem> itemsArrayList;
    private RedditListAdapter redditListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redditListView = findViewById(R.id.reddit_list);

        getData();

        redditListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                RedditItem item = (RedditItem) adapter.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item.getPermalink(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, RedditActivity.class);
                intent.putExtra("permalink", item.getPermalink());
                startActivity(intent);
            }
        });

    }

    private void getData() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        
                        try {

                            JSONObject obj = new JSONObject(response);
//                            Log.d("Volley", obj.toString());
//                            Log.d("Volley", obj.getJSONObject("data").toString());
//                            Log.d("Volley", obj.getJSONObject("data").getJSONArray("children").toString());

                            itemsArrayList = new ArrayList<>();
                            JSONArray dataArray  = obj.getJSONObject("data").getJSONArray("children");

                            for (int i = 0; i < dataArray.length(); i++) {

                                RedditItem redditItem = new RedditItem();
                                JSONObject dataobj = dataArray.getJSONObject(i).getJSONObject("data");

                                redditItem.setTitle(dataobj.getString("title"));
                                redditItem.setPermalink(dataobj.getString("permalink"));

                                itemsArrayList.add(redditItem);

                            }

                            setupListview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupListview(){
        redditListAdapter = new RedditListAdapter(this, itemsArrayList);
        redditListView.setAdapter(redditListAdapter);
    }
}
