package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class AdminPortal extends AppCompatActivity implements Communicator{

    RecyclerView rcv;
    ArrayList<Teacher> teachers;
    Recycler3Adapter adap;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        teachers = new ArrayList<>();
        rcv = (RecyclerView)findViewById(R.id.rcv);
        LinearLayoutManager lnm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lnm);
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings : {
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
            }
            case R.id.action_refresh : {
                teachers.clear();
                load();
                return true;
            }
            case R.id.action_add : {
                Intent i = new Intent(AdminPortal.this,AddTeacher.class);
                startActivity(i);
                return true;
            }
            case R.id.action_logout :
            {
                SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("current_user_id",null);
                edit.putString("user_type",null);
                edit.putString("current_user_name",null);
                edit.putString("current_user_standard",null);
                edit.apply();
                Intent i = new Intent(AdminPortal.this,Parent.class);
                startActivity(i);
                finish();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void load() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/teachers/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray json = jsonObject.getJSONArray("teachers");
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject j = json.getJSONObject(i);
                        String name = j.getString("name");
                        String id = j.getString("id");
                        String standard = j.getString("standard");
                        Teacher teacher = new Teacher(id,name,standard);
                        teachers.add(teacher);
                    }
                   // Log.e("length", Integer.toString(teachers.size()));
                    adap = new Recycler3Adapter(teachers,AdminPortal.this);
                    rcv.setAdapter(adap);
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
                Toast.makeText(AdminPortal.this,"please refresh",Toast.LENGTH_LONG).show();
            }
        });
        request.addMarker("subjects");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public void modifyTeacher(final String id, final String name, final String standard, final int pos) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/teachers/modify.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getInt("success")==1)
                    {
                        teachers.remove(pos);
                        Teacher teacher = new Teacher(id, name, standard);
                        teachers.add(pos,teacher);
                        adap.notifyDataSetChanged();
                        Toast.makeText(AdminPortal.this,"Modification Successful",Toast.LENGTH_SHORT);

                    }
                    else
                        Toast.makeText(AdminPortal.this,"please retry",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminPortal.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put("id",id);
                params.put("name",name);
                params.put("standard",standard);
                return params;
            }
        };
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public void delete(final Teacher teacher, final int pos) {
        final String id=teacher.getId();
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/teachers/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getInt("success")==1)
                    {
                        teachers.remove(teacher);
                        adap.notifyDataSetChanged();
                        Toast.makeText(AdminPortal.this,"Deletion Successful",Toast.LENGTH_SHORT);
                    }
                    else
                        Toast.makeText(AdminPortal.this,"please retry",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminPortal.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put("id",id);
                return params;
            }
        };
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
class Recycler3Adapter extends RecyclerView.Adapter<Recycler3Adapter.ViewHolder> {

    private ArrayList<Teacher> teachers;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView id;
        TextView standard;
        LinearLayout container;

        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            id = (TextView) v.findViewById(R.id.id);
            standard = (TextView) v.findViewById(R.id.standard);
            container=(LinearLayout)v.findViewById(R.id.container);
        }
    }

    Recycler3Adapter(ArrayList<Teacher> teachers, Context context) {
        this.teachers = teachers;
        this.context=context;
    }

    @Override
    public Recycler3Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Recycler3Adapter.ViewHolder holder, int position) {
        final Teacher teacher = teachers.get(position);
        holder.name.setText("Name : "+teacher.getName());
        holder.standard.setText("Standard : "+teacher.getStandard());
        holder.id.setText("ID : "+teacher.getId());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("ABHYAS");
                builder.setMessage("Modify");
                final View v = LayoutInflater.from(context).inflate(R.layout.teacher_modify,null);
                builder.setView(v);
                final EditText n1 = (EditText) v.findViewById(R.id.name);
                final EditText s1 = (EditText) v.findViewById(R.id.standard);
                n1.setText(teachers.get(holder.getAdapterPosition()).getName());
                s1.setText(teachers.get(holder.getAdapterPosition()).getStandard());
                builder.setPositiveButton("MODIFY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Communicator com = (Communicator) context;
                        com.modifyTeacher(teachers.get(holder.getAdapterPosition()).getId(),n1.getText().toString(),s1.getText().toString(),holder.getAdapterPosition());
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Teacher teacher = teachers.get(holder.getAdapterPosition());
                        Communicator com = (Communicator) context;
                        com.delete(teacher,holder.getAdapterPosition());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
       // Log.e("size",Integer.toString(teachers.size()));
        return teachers.size();
    }
}
