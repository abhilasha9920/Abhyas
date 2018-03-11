package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Teacher_videos extends AppCompatActivity {

    ListView list;
    ArrayList<Video> videos;
    ArrayAdapter<String> adapter;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_videos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        videos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Teacher_videos.this,Teachers_Questions.class);
                String video_id = videos.get(position).getId();
                i.putExtra("id",video_id);
                startActivity(i);
            }
        });
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.teacher__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings : Intent i = new Intent(this,Settings.class);
                startActivity(i);
                return true;
            case R.id.action_refresh :
                adapter.clear();
                videos.clear();
                load();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void load() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("ABHYAS");
        pd.setMessage("LOADING...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/videos/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("videos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String name = obj.getString("name");
                        String id = obj.getString("id");
                        String url = obj.getString("url");
                        String video_id = obj.getString("video_id");
                        Video video = new Video(name,id,url,video_id);
                        videos.add(video);
                        adapter.add(name);
                    }
                    if(pd.isShowing())
                        pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Teacher_videos.this, error.toString(), Toast.LENGTH_LONG).show();
                if(pd.isShowing())
                    pd.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        request.addMarker("videos");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
