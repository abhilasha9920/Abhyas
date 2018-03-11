package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class StudentProfile extends AppCompatActivity {

    TextView id;
    TextView name;
    TextView fathername;
    TextView dob;
    TextView roll;
    TextView standard;
    ArrayList<Quiz> quizes;
    RecyclerView rcv;
    Recycler2Adapter adap;
    String sid;
    String sname;
    String sfathername;
    String sstandard;
    String sroll;
    String sdob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        sid= i.getStringExtra("id");
        sname=i.getStringExtra("name");
        sfathername=i.getStringExtra("fathername");
        sstandard = i.getStringExtra("standard");
        sroll = i.getStringExtra("roll");
        sdob = i.getStringExtra("dob");
        quizes = new ArrayList<>();
        rcv = (RecyclerView)findViewById(R.id.rcv);
        name = (TextView)findViewById(R.id.name);
        fathername = (TextView)findViewById(R.id.fathername);
        dob = (TextView)findViewById(R.id.dob);
        roll = (TextView)findViewById(R.id.roll);
        id = (TextView)findViewById(R.id.ID);
        standard = (TextView)findViewById(R.id.standard);

        name.setText("Name : ");
        fathername.setText("Father Name : ");
        dob.setText("Date Of Birth : ");
        roll.setText("Roll Number : ");
        standard.setText("Standard : ");
        id.setText("Student ID : ");

        name.append(sname);
        id.append(sid);
        fathername.append(sfathername);
        standard.append(sstandard);
        roll.append(sroll);
        dob.append(sdob);

        LinearLayoutManager lnm = new LinearLayoutManager(this);
        rcv.setLayoutManager(lnm);
        adap = new Recycler2Adapter(quizes,this);
        rcv.setAdapter(adap);
        load();
    }

    private void load() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setTitle("ABHYAS");
        pd.show();
        String url = "http://araniisansthan.com/Abhyas/students/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response",response);
                try
                {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonarray =object.getJSONArray("quiz");
                    for (int i =0;i<jsonarray.length();i++)
                    {
                        JSONObject jsonObject = jsonarray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String score = jsonObject.getString("score");
                        String totalscore = jsonObject.getString("total");
                        Quiz quiz = new Quiz(name,Integer.parseInt(score),Integer.parseInt(totalscore));
                        quizes.add(quiz);
                    }
                    adap.notifyDataSetChanged();
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",sid);
                return params;
            }
        };
        request.addMarker("studentprofile");
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_refresh : {
                quizes.clear();
                load();
                return true;
            }
            case R.id.action_edit:
            {
                Intent i = new Intent(StudentProfile.this,EditStudent.class);
                i.putExtra("name",sname);
                i.putExtra("fathername",sfathername);
                i.putExtra("standard",sstandard);
                i.putExtra("roll",sroll);
                i.putExtra("dob",sdob);
                i.putExtra("id",sid);
                startActivityForResult(i,1);
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if(resultCode==RESULT_OK)
        {
            sid= i.getStringExtra("id");
            sname=i.getStringExtra("name");
            sfathername=i.getStringExtra("fathername");
            sstandard = i.getStringExtra("standard");
            sroll = i.getStringExtra("roll");
            sdob = i.getStringExtra("dob");

            name.setText("Name : ");
            fathername.setText("Father Name : ");
            dob.setText("Date Of Birth : ");
            roll.setText("Roll Number : ");
            standard.setText("Standard : ");
            id.setText("Student ID : ");

            name.append(sname);
            id.append(sid);
            fathername.append(sfathername);
            standard.append(sstandard);
            roll.append(sroll);
            dob.append(sdob);
        }
        else
            super.onActivityResult(requestCode,resultCode,i);
    }
}
class Recycler2Adapter extends RecyclerView.Adapter<Recycler2Adapter.ViewHolder> {

    private ArrayList<Quiz> quizes;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;
        TextView total;

        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            score = (TextView) v.findViewById(R.id.score);
            total = (TextView) v.findViewById(R.id.total);
        }
    }

    Recycler2Adapter(ArrayList<Quiz> quizes, Context context) {
        this.quizes = quizes;
        this.context=context;
    }

    @Override
    public Recycler2Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Recycler2Adapter.ViewHolder holder, int position) {
        final Quiz quiz = quizes.get(position);
        holder.name.setText("Quiz : ");
        holder.score.setText("Score : ");
        holder.total.setText("Total : ");
        holder.name.append(quiz.getName());
        holder.score.append(String.valueOf(quiz.getMarks()));
        holder.total.append(String.valueOf(quiz.getTotal()));

    }
    @Override
    public int getItemCount() {
        return quizes.size();
    }
}
