package com.leagueofshadows.abhyas;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Videos extends AppCompatActivity {
    RecyclerView rcv;
    ArrayList<Video> videos;
    RecyclerAdapter adap;
    String id;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        Intent i = getIntent();
        id = i.getStringExtra("id");
        rcv = (RecyclerView) findViewById(R.id.rcv);
        videos = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        adap = new RecyclerAdapter(videos, getApplicationContext());
        rcv.setAdapter(adap);
        Load();
    }

    @Override
    protected void onStop() {
        VolleyHelper.getInstance(this).cancel("videos");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject_menu, menu);
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
                videos.clear();
                Load();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    void Load() {
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
                        Video video = new Video(name, id, url,video_id);
                        videos.add(video);
                    }
                    adap.notifyDataSetChanged();
                    if(pd!=null&&pd.isShowing())
                    pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Videos.this, error.toString(), Toast.LENGTH_LONG).show();
                if(pd!=null&&pd.isShowing())
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
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private ArrayList<Video> videos;
        private ImageLoader imageLoader;
        private Context context;
        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ImageView;
            TextView textView;
            LinearLayout linearLayout;
            ViewHolder(View v) {
                super(v);
                ImageView = (ImageView) v.findViewById(R.id.thumb);
                textView = (TextView) v.findViewById(R.id.name);
                linearLayout = (LinearLayout) v.findViewById(R.id.main);
            }
        }

        RecyclerAdapter(ArrayList<Video> videos, Context context) {
            this.videos = videos;
            imageLoader = VolleyHelper.getInstance(context).getImageLoader();
            this.context=context;
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
            final Video video = videos.get(position);
            holder.textView.setText(video.getName());
            String url = video.getUrl();
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,Videoview.class);
                    i.putExtra("name",video.getName());
                    i.putExtra("id",video.getId());
                    i.putExtra("video_id",video.getVideo_id());
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.ImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.ImageView.setImageResource(R.mipmap.ic_launcher);
                }
            });
        }
        @Override
        public int getItemCount() {
            return videos.size();
        }
    }
