
package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class Subjects extends AppCompatActivity {
    CustomAdapter adap;
    ArrayList<Subject> subjects;
    ListView list;
    String standard;
    String name;
    String id;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        id = i.getStringExtra("current_user_id");
        name = i.getStringExtra("current_user_name");
        standard = i.getStringExtra("current_user_standard");
        subjects = new ArrayList<>();
        adap = new CustomAdapter(this,R.layout.subject_item,subjects);
        list = (ListView) findViewById(R.id.list);
        pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        load();
    }

    @Override
    protected void onStop() {
        VolleyHelper.getInstance(this).cancel("subjects");
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
                subjects.clear();
                load();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    void load()
    {
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/subjects/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray json = jsonObject.getJSONArray("subjects");
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject j = json.getJSONObject(i);
                        String name = j.getString("name");
                        String id = j.getString("id");
                        Subject subject = new Subject(name,id);
                        subjects.add(subject);
                    }
                    list.setAdapter(adap);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pd!=null&&pd.isShowing())
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pd!=null&&pd.isShowing())
                pd.dismiss();
                Toast.makeText(Subjects.this,"please refresh",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("standard",standard);
                return params;
            }
        };
        request.addMarker("subjects");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    class CustomAdapter extends ArrayAdapter<Subject>
    {
        ArrayList<Subject> subjects;
        Context context;
        CustomAdapter(Context context, int resource, ArrayList<Subject> subjects) {
            super(context, resource, subjects);
            this.subjects=subjects;
            this.context=context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView==null)
            {
                convertView = getLayoutInflater().inflate(R.layout.subject_item,parent,false);
                TextView txt = (TextView) convertView.findViewById(R.id.button);
                final Subject subject = subjects.get(position);
                txt.setText(subject.getName());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,Videos.class);
                        i.putExtra("subject",subject.getName());
                        i.putExtra("id",subject.getId());
                        startActivity(i);
                    }
                });
            }
            else
            {
                TextView txt = (TextView) convertView.findViewById(R.id.button);
                final Subject subject = subjects.get(position);
                txt.setText(subject.getName());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,Videos.class);
                        i.putExtra("subject",subject.getName());
                        i.putExtra("id",subject.getId());
                        startActivity(i);
                    }
                });
            }
            return convertView;
        }
    }
}
