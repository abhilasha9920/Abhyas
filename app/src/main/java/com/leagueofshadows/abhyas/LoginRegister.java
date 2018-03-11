package com.leagueofshadows.abhyas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;
import static android.view.View.resolveSize;


public class LoginRegister extends AppCompatActivity {
    static ProgressDialog pd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        pd = new ProgressDialog(this);
        pd.setTitle("Abhyas");
        pd.setMessage("Logging in");
        pd.setCancelable(false);

    }

    public static class PlaceholderFragment extends Fragment
    {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int pos =  getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            if(pos==1)
            {
                rootView= inflater.inflate(R.layout.fragment_login, container, false);
                final TextView ID = (TextView)rootView.findViewById(R.id.login);
                Button login = (Button)rootView.findViewById(R.id.loginbutton);
                final ImageView animImageView = (ImageView) rootView.findViewById(R.id.iv_animation);
                animImageView.setBackgroundResource(R.drawable.anim_girl);
                animImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable)
                                animImageView.getBackground();
                        frameAnimation.start()
                        ;
                    }
                });
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String id = ID.getText().toString();
                        if(!id.equals(""))
                        {
                            pd.show();
                            String url = "http://araniisansthan.com/Abhyas/login/";
                            StringRequest request = new StringRequest(Request.Method.POST,url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String rep) {
                                            Log.d("Json",rep);
                                            try
                                            {

                                                JSONObject response = new JSONObject(rep);
                                                int x = response.getInt("success");
                                                if(x==1)
                                                {
                                                    String standard = response.getString("standard");
                                                    String name = response.getString("name");
                                                    String fname = response.getString("fathername");
                                                    String roll = response.getString("roll");
                                                    String dob = response.getString("dob");
                                                    Intent i = new Intent();
                                                    SharedPreferences sp = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor edit = sp.edit();
                                                    edit.putString("current_user_name",name);
                                                    edit.putString("current_user_id",id);
                                                    edit.putString("current_user_standard",standard);
                                                    edit.putString("user_type","student");
                                                    edit.putString("current_user_dob",dob);
                                                    edit.putString("current_user_fathername",fname);
                                                    edit.putString("current_user_roll",roll);
                                                    edit.apply();
                                                    i.putExtra("user_type","student");
                                                    i.putExtra("current_user_name",name);
                                                    i.putExtra("current_user_id",id);
                                                    i.putExtra("current_user_standard",standard);
                                                    i.putExtra("current_user_dob",dob);
                                                    i.putExtra("current_user_fathername",fname);
                                                    i.putExtra("current_user_roll",roll);
                                                    getActivity().setResult(RESULT_OK,i);
                                                    pd.dismiss();
                                                    getActivity().finish();
                                                }

                                                else
                                                {
                                                    Toast.makeText(getContext(),response.get("message").toString(),Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pd.dismiss();
                                    Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                            })
                            {
                                @Override
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<>();

                                    params.put("id",id);
                                    return params;
                                }

                            };
                            VolleyHelper.getInstance(getContext()).addToRequestQueue(request);
                        }
                        else
                        {
                            ID.setError("field is necessary");
                            ID.requestFocus();
                        }
                    }
                });
            }

            else
            {
                rootView = inflater.inflate(R.layout.fragment_adminlogin,container,false);
                final EditText id = (EditText) rootView.findViewById(R.id.login);
                final EditText pass = (EditText) rootView.findViewById(R.id.password);
                final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
                ArrayList<String> types = new ArrayList<>();
                types.add("Teacher");
                types.add("Admin");
                ArrayAdapter<String> adap = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,types);
                spinner.setAdapter(adap);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==1)
                            pass.setVisibility(VISIBLE);
                        else
                            pass.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Button login = (Button) rootView.findViewById(R.id.loginbutton);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.show();
                        if(spinner.getSelectedItemPosition()==1)
                        {
                            String userid = id.getText().toString();
                            String password = pass.getText().toString();
                            if(!userid.equals("")&&!password.equals(""))
                            {
                                if(userid.equals("admin")&&password.equals("admin"))
                                {
                                    Intent i = new Intent();
                                    i.putExtra("user_type","admin");
                                    SharedPreferences sp = getContext().getSharedPreferences("preferences",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit= sp.edit();
                                    edit.putString("user_type","admin");
                                    edit.putString("current_user_id","admin");
                                    edit.apply();
                                    getActivity().setResult(RESULT_OK,i);
                                    getActivity().finish();
                                }
                                else
                                    Toast.makeText(getContext(),"Wrong username or password",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getContext(),"Enter username and password Correctly",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                        else
                        {
                            final String userid = id.getText().toString();
                            if(!userid.equals(""))
                            {
                                String url= "http://araniisansthan.com/Abhyas/teachers/login.php";
                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try
                                        {
                                            JSONObject object = new JSONObject(response);
                                            int success = object.getInt("success");
                                            if(success==1)
                                            {
                                                String n = object.getString("name");
                                                String s = object.getString("standard");
                                                Intent i = new Intent();
                                                i.putExtra("user_type","teacher");
                                                i.putExtra("current_user_standard",s);
                                                i.putExtra("current_user_name",n);
                                                i.putExtra("current_user_id",userid);
                                                SharedPreferences sp = getContext().getSharedPreferences("preferences",Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit= sp.edit();
                                                edit.putString("current_user_name",n);
                                                edit.putString("current_user_standard",s);
                                                edit.putString("current_user_id",userid);
                                                edit.putString("user_type","teacher");
                                                edit.apply();
                                                getActivity().setResult(RESULT_OK,i);
                                                pd.dismiss();
                                                getActivity().finish();
                                            }
                                            else
                                                Toast.makeText(getContext(),"User doesn't exist",Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getContext(),"Please try again",Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("id",userid);
                                        return params;
                                    }
                                };
                                VolleyHelper.getInstance(getContext()).addToRequestQueue(request);
                            }
                            else
                                Toast.makeText(getContext(),"Enter id correctly",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN";
                case 1:
                    return "TEACHER";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}

