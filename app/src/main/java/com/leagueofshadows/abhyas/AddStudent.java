package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddStudent extends AppCompatActivity {

    String standard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        final TextView student_name = (TextView) findViewById(R.id.name);
        final TextView student_fname = (TextView) findViewById(R.id.fathername);
        final TextView student_roll = (TextView) findViewById(R.id.roll);
        final TextView student_dob = (TextView) findViewById(R.id.dob);
        final Button register = (Button) findViewById(R.id.register);
        Intent i = getIntent();
        standard = i.getStringExtra("standard");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = student_name.getText().toString();
                final String fname = student_fname.getText().toString();
                final String roll = student_roll.getText().toString();
                final String dob = student_dob.getText().toString();
                if (!name.equals("")) {
                            if (!fname.equals("")) {
                                if (!roll.equals("")) {
                                    if (!dob.equals("")) {
                                        final ProgressDialog pd = new ProgressDialog(AddStudent.this);
                                        pd.setTitle("ABHYAS");
                                        pd.setMessage("Loading...");
                                        pd.show();
                                        String url = "http://araniisansthan.com/Abhyas/register/";
                                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String rep) {
                                                try {
                                                    JSONObject response = new JSONObject(rep);
                                                    int success = response.getInt("success");
                                                    if (success == 1) {
                                                        Toast.makeText(AddStudent.this,response.getString("message"),Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent();
                                                        setResult(RESULT_OK,i);
                                                        finish();
                                                    } else
                                                        Toast.makeText(AddStudent.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("name", name);
                                                params.put("fathername", fname);
                                                params.put("roll", roll);
                                                params.put("standard", standard);
                                                params.put("dob", dob);
                                                return params;
                                            }

                                        };
                                        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
                                    } else {
                                        student_dob.setError("field required");
                                        student_dob.requestFocus();
                                    }
                                } else {
                                    student_roll.setError("field required");
                                    student_roll.requestFocus();
                                }
                            } else {
                                student_fname.setError("field required");
                                student_fname.requestFocus();
                            }
                } else {
                    student_name.setError("field required");
                    student_name.requestFocus();
                }
            }
        });
    }

    public void onBackPressed() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ABHYAS");
            builder.setMessage("All the data will be lost, go back?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_CANCELED,new Intent());
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
}

