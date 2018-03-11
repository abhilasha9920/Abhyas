package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Add_Question extends AppCompatActivity {

    EditText question;
    EditText op1;
    EditText op2;
    EditText op3;
    EditText op4;
    Spinner spinner;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__question);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        question = (EditText)findViewById(R.id.question);
        op1 = (EditText)findViewById(R.id.op1);
        op2 = (EditText)findViewById(R.id.op2);
        op3 = (EditText)findViewById(R.id.op3);
        op4 = (EditText)findViewById(R.id.op4);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        spinnerAdapter.add("1");
        spinnerAdapter.add("2");
        spinnerAdapter.add("3");
        spinnerAdapter.add("4");
        spinner.setAdapter(spinnerAdapter);
        Button button =(Button)findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!question.getText().toString().equals("") && !op1.getText().toString().equals("") && !op2.getText().toString().equals("") && !op3.getText().toString().equals("") && !op4.getText().toString().equals(""))
                {

                    final ProgressDialog dialog = new ProgressDialog(Add_Question.this);
                    dialog.setTitle("Abhyas");
                    dialog.setMessage("Fetching Questions");
                    dialog.show();
                    String url = "http://araniisansthan.com/Abhyas/questions/add.php";
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            try
                            {
                                JSONObject json = new JSONObject(response);

                                int success = json.getInt("success");
                                if(success==1)
                                {
                                    Toast.makeText(Add_Question.this,"Addition successful",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Add_Question.this,Teachers_Questions.class);
                                    i.putExtra("id",id);
                                }
                                else
                                    Toast.makeText(Add_Question.this,"Please retry",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Add_Question.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("id",id);
                            params.put("question",question.getText().toString());
                            params.put("op1",op1.getText().toString());
                            params.put("op2",op2.getText().toString());
                            params.put("op3",op3.getText().toString());
                            params.put("op4",op4.getText().toString());
                            params.put("ans",spinner.getSelectedItem().toString());
                            return params;
                        }
                    };
                    request.addMarker("questions");
                    VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);

                }
                else
                    Toast.makeText(Add_Question.this,"please enter all the values",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
