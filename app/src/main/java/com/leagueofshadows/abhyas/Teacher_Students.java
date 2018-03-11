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

public class Teacher_Students extends AppCompatActivity implements Communicator1 {

    RecyclerView rcv;
    ArrayList<Student> students;
    Recycler4Adapter adap;
    String id;
    String standard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__students);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        standard = i.getStringExtra("standard");
        rcv = (RecyclerView)findViewById(R.id.rcv);
        students = new ArrayList<>();
        adap = new Recycler4Adapter(students,this);
        LinearLayoutManager lnm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lnm);
        rcv.setAdapter(adap);
        load();
    }

    private void load() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/students/get.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray json = jsonObject.getJSONArray("students");
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject j = json.getJSONObject(i);
                        String name = j.getString("name");
                        String id = j.getString("id");
                        String standard = j.getString("standard");
                        String roll = j.getString("roll");
                        String fathername = j.getString("fathername");
                        String dob = j.getString("dob");
                        Student student = new Student(id,name,fathername,standard,roll,dob);
                        students.add(student);
                    }
                    // Log.e("length", Integer.toString(teachers.size()));
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
                Toast.makeText(Teacher_Students.this,"please refresh",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put("standard",standard);
                return params;
            }
        };
        request.addMarker("subjects");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
            }
            case R.id.action_refresh: {
                students.clear();
                load();
                return true;
            }
            case R.id.action_add: {
                Intent i = new Intent(Teacher_Students.this, AddStudent.class);
                i.putExtra("standard",standard);
                startActivityForResult(i,1);
                return true;
            }
            case  R.id.logout:
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
                        Intent i = new Intent(Teacher_Students.this,Parent.class);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void modifyStudent(Student student, int pos) {

    }

    @Override
    public void delete(final Student student, int pos) {
        final String id=student.getId();
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Fetching...");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/students/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getInt("success")==1)
                    {
                        students.remove(student);
                        adap.notifyDataSetChanged();
                        Toast.makeText(Teacher_Students.this,"Deletion Successful",Toast.LENGTH_SHORT);
                    }
                    else
                        Toast.makeText(Teacher_Students.this,"please retry",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Teacher_Students.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            students.clear();
            load();
        }
        else
        super.onActivityResult(requestCode, resultCode, data);
    }
}

class Recycler4Adapter extends RecyclerView.Adapter<Recycler4Adapter.ViewHolder> {

    private ArrayList<Student> students;
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
            container = (LinearLayout) v.findViewById(R.id.container);
        }
    }

    Recycler4Adapter(ArrayList<Student> students, Context context) {
        this.students = students;
        this.context = context;
    }

    @Override
    public Recycler4Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Recycler4Adapter.ViewHolder holder, final int position) {
        final Student student = students.get(position);
        holder.name.setText("Name : " + student.getName());
        holder.standard.setText("Standard : " + student.getStandard());
        holder.id.setText("ID : " + student.getId());
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("ABHYAS");
                builder.setMessage("DELETE?");
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Communicator1 com = (Communicator1) context;
                        com.delete(student,holder.getAdapterPosition());
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,StudentProfile.class);
                i.putExtra("name",student.getName());
                i.putExtra("fathername",student.getFathername());
                i.putExtra("standard",student.getStandard());
                i.putExtra("roll",student.getRoll());
                i.putExtra("dob",student.getDob());
                i.putExtra("id",student.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Log.e("size",Integer.toString(teachers.size()));
        return students.size();
    }

}
