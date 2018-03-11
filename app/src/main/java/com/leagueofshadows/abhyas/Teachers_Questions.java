package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class Teachers_Questions extends AppCompatActivity {

    ArrayList<Question> questions;
    ArrayAdapter<String> adapter;
    String id;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers__questions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        list = (ListView)findViewById(R.id.list);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        questions = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Teachers_Questions.this,Add_Question.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });
        load();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Teachers_Questions.this);
                builder.setTitle("ABHYAS");
                builder.setMessage("Do you want to delete the Question?");
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(position);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void delete(final int position) {
        final Question question = questions.get(position);
        final String qid = question.getId();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Abhyas");
        dialog.setMessage("Fetching Questions");
        dialog.show();
        String url = "http://araniisansthan.com/Abhyas/questions/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    if(success==1)
                    {
                        adapter.remove(question.getQuestion());
                        questions.remove(position);
                        Toast.makeText(Teachers_Questions.this,"Delete Successful",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(Teachers_Questions.this,"Please retry",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(Teachers_Questions.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",qid);
                return params;
            }
        };
        request.addMarker("questions");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void load() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Abhyas");
        dialog.setMessage("Fetching Questions");
        dialog.show();
        String url = "http://araniisansthan.com/Abhyas/questions/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("questions");
                    for(int i=0;i<array.length();i++)
                    {
                        JSONObject j = array.getJSONObject(i);
                        String id1 = j.getString("id");
                        String q = j.getString("question");
                        String op1 = j.getString("op1");
                        String op2 = j.getString("op2");
                        String op3 = j.getString("op3");
                        String op4 = j.getString("op4");
                        int ans = j.getInt("ans");
                        Question question = new Question(id1,q,op1,op2,op3,op4,ans);
                        questions.add(question);
                        adapter.add(q);
                    }

                    dialog.dismiss();
                }
                catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(Teachers_Questions.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                return params;
            }
        };
        request.addMarker("questions");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

}
