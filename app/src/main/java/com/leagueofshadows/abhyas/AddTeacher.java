package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class AddTeacher extends AppCompatActivity {

    ArrayList<String> classes= new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<Subject> subjects;
    CustomAdapter subjectsName;
    boolean[] selected;
    ListView list;
    Button button;
    EditText name;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        classes.add("3");
        classes.add("4");
        classes.add("5");
        list = (ListView)findViewById(R.id.list);
        name = (EditText)findViewById(R.id.name);
        button = (Button)findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        subjects = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,classes);
        subjectsName = new CustomAdapter(subjects);
        list.setAdapter(subjectsName);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjects.clear();
                load(classes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void add() {
        final String n = name.getText().toString();
        final String s = spinner.getSelectedItem().toString();
        if(!n.equals(""))
        {
            int count =0;
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<subjects.size();i++)
                if(selected[i])
                {
                    count++;
                    if(i==subjects.size()-1)
                        builder.append(subjects.get(i).getId());
                    else
                    builder.append(subjects.get(i).getId()).append("+");
                }
            if(count != 0)
            {
                final String sub = builder.toString();
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("Abhyas");
                pd.setMessage("Loading...");
                pd.show();
                String url = "http://araniisansthan.com/Abhyas/teachers/add.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            if(success==1) {
                                Toast.makeText(AddTeacher.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AddTeacher.this,AdminPortal.class);
                                startActivity(i);
                                finish();
                            }
                            else if(success == -1)
                                Toast.makeText(AddTeacher.this,"Teacher already exists",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(AddTeacher.this,"Please try again",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddTeacher.this,"please refresh",Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("standard",s);
                        params.put("name",n);
                        params.put("subjects",sub);
                        return params;
                    }
                };
                request.addMarker("subjects");
                VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
            else
                Toast.makeText(this,"please select subjects",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"please enter name",Toast.LENGTH_SHORT).show();
    }

    private void load(final String standard) {
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
                    selected = new boolean[json.length()];
                    for(int i=0;i<json.length();i++)
                    {
                        selected[i] = false;
                        JSONObject j = json.getJSONObject(i);
                        String name = j.getString("name");
                        String id = j.getString("id");
                        Subject subject = new Subject(name, id);
                        subjects.add(subject);
                    }
                    subjectsName.notifyDataSetChanged();
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
                Toast.makeText(AddTeacher.this,"please refresh",Toast.LENGTH_LONG).show();
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

    class CustomAdapter extends BaseAdapter
    {
        ArrayList<Subject> subjects;
        CustomAdapter(ArrayList<Subject> subjects)
        {
            this.subjects= subjects;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.subject_select_item, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.check);
                name.setText(subjects.get(position).getName());
                checkbox.setChecked(false);
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        selected[position] = isChecked;
                    }
                });
            }
            else
            {
                TextView name = (TextView) convertView.findViewById(R.id.name);
                CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.check);
                name.setText(subjects.get(position).getName());
                checkbox.setChecked(false);
            }
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        if(!name.getText().toString().equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ABHYAS");
            builder.setMessage("All the data will be lost, go back?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(AddTeacher.this,AdminPortal.class);
                    startActivity(i);
                    finish();
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
        else
            super.onBackPressed();
    }
}
