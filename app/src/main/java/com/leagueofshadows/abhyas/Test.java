package com.leagueofshadows.abhyas;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Test extends AppCompatActivity {

    ViewPager viewPager;
    MyAdapter myadapter;
    static ArrayList<Question> questions = new ArrayList<>();
    String id;
    String videoName;
    static int NUM_QUES;
    static int[] ans = new int[10];
    static TabLayout tabLayout;
    static int current_position;
    String user_id;
    String user_Standard;
    String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        videoName = i.getStringExtra("videoname");
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        user_id = sp.getString("current_user_id",null);
        user_name = sp.getString("current_user_name",null);
        user_Standard = sp.getString("current_user_standard",null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        questions.clear();
        load();
        for(int j=0;j<10;j++)
        {
            ans[j]=0;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        questions.clear();
        load();
        for(int i=0;i<10;i++)
        {
            ans[i]=0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewPager=null;
        myadapter=null;
        tabLayout=null;
        VolleyHelper.getInstance(this).cancel("questions");
        questions.clear();
        for(int i=0;i<10;i++)
        {
            ans[i]=0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings: {
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
            }
            case R.id.action_refresh: {
                if(questions.size()==0)
                {
                    load();
                }
                else
                {
                    Toast.makeText(this,"finished loading",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.action_submit: {
                int attempeted = 0;
                int correct=0;
                for(int i=0;i<NUM_QUES;i++)
                {
                    if(ans[i]!=0)
                    {
                        attempeted++;
                        if(ans[i]==questions.get(i).getAns())
                            correct++;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Abhyas");
                if(attempeted!=NUM_QUES)
                builder.setMessage("you have only attempted "+attempeted+" question/questions , do you wish to submit");
                else
                builder.setMessage("do you wish to proceed");
                final int finalCorrect = correct;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateScore(finalCorrect,questions.size());
                    }
                });
                AlertDialog alertdialog = builder.create();
                alertdialog.show();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void showScore() {
        int correct = 0;
        for(int i=0;i<NUM_QUES;i++)
        {
            if(ans[i]==questions.get(i).getAns())
            {
                correct++;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Abhyas");
        builder.setMessage("you have got "+correct+" answer correct,Congratulations!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Test.super.onBackPressed();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateScore(final int score , final int total) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Abhyas...");
        dialog.setMessage("Updating Score");
        dialog.show();
        String url = "http://araniisansthan.com/Abhyas/quiz/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    if(success==1)
                    {
                        Toast.makeText(Test.this,"updated successfully",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        showScore();
                    }
                    else
                    {
                        dialog.dismiss();
                        Toast.makeText(Test.this," some error has occurred, please try again",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Test.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("video_id",id);
                params.put("student_id",user_id);
                params.put("score",Integer.toString(score));
                params.put("total",Integer.toString(total));
                params.put("videoname",videoName);
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
                    NUM_QUES = array.length();
                    for(int i=0;i<NUM_QUES;i++)
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
                        Collections.shuffle(questions);
                    }
                    viewPager = (ViewPager) findViewById(R.id.container);
                    myadapter = new MyAdapter(getSupportFragmentManager());
                    viewPager.setAdapter(myadapter);
                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
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
                Toast.makeText(Test.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    public static class QuizFragment extends Fragment {
        int fragVal;
        public QuizFragment()
        {

        }
        static QuizFragment init(int val) {
            QuizFragment quiz = new QuizFragment();
            Bundle args = new Bundle();
            args.putInt("val", val);
            quiz.setArguments(args);
            return quiz;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View layoutView = inflater.inflate(R.layout.quiz_fragment, container,
                    false);
            RadioGroup radiogroup = (RadioGroup) layoutView.findViewById(R.id.radio);
            TextView textview = (TextView)layoutView.findViewById(R.id.question);
            RadioButton op1 = (RadioButton)layoutView.findViewById(R.id.op1);
            RadioButton op2 = (RadioButton)layoutView.findViewById(R.id.op2);
            RadioButton op3 = (RadioButton)layoutView.findViewById(R.id.op3);
            RadioButton op4 = (RadioButton)layoutView.findViewById(R.id.op4);
            Question question = questions.get(fragVal);
            textview.setText(question.getQuestion());
            op1.setText(question.getOption1());
            op2.setText(question.getOption2());
            op3.setText(question.getOption3());
            op4.setText(question.getOption4());
            radiogroup.clearCheck();
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId==R.id.op1) {
                        ans[fragVal] = 1;
                    }
                    else if(checkedId==R.id.op2) {
                        ans[fragVal] = 2;
                    }
                    else if(checkedId==R.id.op3) {
                        ans[fragVal] = 3;
                    }
                    else if(checkedId==R.id.op4) {
                        ans[fragVal] = 4;
                    }
                    TabLayout.Tab tab  =  tabLayout.getTabAt(fragVal);
                    if(tab!=null)
                    tab.setIcon(R.drawable.ic_done_black_48dp);
                }
            });
            return layoutView;
        }
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            current_position=position;
        }

        @Override
        public int getCount() {
            return NUM_QUES;
        }

        @Override
        public Fragment getItem(int position) {
            return QuizFragment.init(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            position++;
            return "Q"+position;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Abhyas");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to quit , all the progress will be lost ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Test.this,Videoview.class);
                i.putExtra("id",id);
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
}
