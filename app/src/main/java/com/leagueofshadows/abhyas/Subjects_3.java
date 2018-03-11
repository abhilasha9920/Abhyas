package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

public class Subjects_3 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    GridView gridView;
    GridAdapter adap;
    ArrayList<Subject> subjects;
    String id;
    String name;
    String standard;
    String fathername;
    String roll;
    String dob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_3);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Intent i = getIntent();
        id = i.getStringExtra("current_user_id");
        name = i.getStringExtra("current_user_name");
        standard = i.getStringExtra("current_user_standard");
        fathername = i.getStringExtra("current_user_fathername");
        roll = i.getStringExtra("current_user_roll");
        dob = i.getStringExtra("current_user_dob");
        subjects = new ArrayList<>();
        gridView = (GridView)findViewById(R.id.container);
        adap = new GridAdapter(Subjects_3.this);
        gridView.setAdapter(adap);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjects.get(position);
                Intent i = new Intent(Subjects_3.this,Videos.class);
                i.putExtra("subject",subject.getName());
                i.putExtra("id",subject.getId());
                startActivity(i);
            }
        });
        load();
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id1 = item.getItemId();

        if (id1 == R.id.nav_profile)
        {
            Intent i = new Intent(this,StudentProfile.class);
            i.putExtra("name",name);
            i.putExtra("fathername",fathername);
            i.putExtra("standard",standard);
            i.putExtra("roll",roll);
            i.putExtra("dob",dob);
            i.putExtra("id",id);
            startActivity(i);
        }
        else if (id1 == R.id.nav_settings)
        {
            Intent i = new Intent(this,Settings.class);
            startActivity(i);
        }
        else if (id1 == R.id.nav_share)
        {

        }
        else if (id1 == R.id.about_us)
        {
            Intent i = new Intent(this,AboutUs.class);
            startActivity(i);
        }
        else if(id1 == R.id.logout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ABHYAS");
            builder.setMessage(" Do you want to logout? ");
            builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit= sp.edit();
                    edit.putString("current_user_id",null);
                    edit.putString("current_user_standard",null);
                    edit.putString("current_user_name",null);
                    edit.apply();
                    Intent i = new Intent(Subjects_3.this,Parent.class);
                    startActivity(i);
                    finish();
                }
            }).setCancelable(true).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
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
                    adap.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pd.isShowing())
                    pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pd.isShowing())
                    pd.dismiss();
                Toast.makeText(Subjects_3.this,error.getMessage(),Toast.LENGTH_LONG).show();
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



    class GridAdapter extends BaseAdapter {

        Context context;
        GridAdapter(Context context)
        {
            this.context=context;
        }
        @Override
        public int getCount() {
            return subjects.size();
        }

        @Override
        public Object getItem(int position) {

            return subjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                Subject subject = subjects.get(position);
                String name = subject.getName();
                convertView = getLayoutInflater().inflate(R.layout.subject_item_2,parent,false);
                //ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
                TextView textView = (TextView)convertView.findViewById(R.id.name);
               /* if(position%2==0)
                    imageView.setImageResource(R.drawable.subject_1);
                else
                    imageView.setImageResource(R.drawable.subject);*/
                textView.setText(name);
            }
            else
            {
                Subject subject = subjects.get(position);
                String name = subject.getName();
                TextView textView = (TextView)convertView.findViewById(R.id.name);
              /*  if(position%2==0)
                    imageView.setImageResource(R.drawable.subject_1);
                else
                    imageView.setImageResource(R.drawable.subject);*/
                textView.setText(name);
            }
            return convertView;
        }
    }

}
