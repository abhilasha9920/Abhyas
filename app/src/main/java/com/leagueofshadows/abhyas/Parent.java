package com.leagueofshadows.abhyas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


public class Parent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        final SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        final String current_user_id = sp.getString("current_user_id",null);
        final ImageView animImageView = (ImageView) findViewById(R.id.iv_animation);
        animImageView.setBackgroundResource(R.drawable.splash_anim);
        animImageView.post(new Runnable() {
                               @Override
                               public void run() {
                                   AnimationDrawable frameAnimation = (AnimationDrawable)
                                           animImageView.getBackground();
                                   frameAnimation.start();
                               }
                           });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(current_user_id==null)
                {
                    Intent i = new Intent(Parent.this,LoginRegister.class);
                    startActivityForResult(i,1);
                }
                else
                {
                    if(current_user_id.equals("admin"))
                    {
                        Intent i = new Intent(Parent.this,AdminPortal.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        String user_type= sp.getString("user_type","student");
                        if(user_type.equals("student"))
                        {
                            String standard = sp.getString("current_user_standard", "1");
                            String name = sp.getString("current_user_name", null);
                            String fname = sp.getString("current_user_fathername",null);
                            String roll = sp.getString("current_user_roll",null);
                            String dob = sp.getString("current_user_dob",null);
                            Intent i = new Intent(Parent.this, Subjects_3.class);
                            i.putExtra("current_user_id", current_user_id);
                            i.putExtra("current_user_name", name);
                            i.putExtra("current_user_standard", standard);
                            i.putExtra("current_user_dob",dob);
                            i.putExtra("current_user_fathername",fname);
                            i.putExtra("current_user_roll",roll);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            String standard = sp.getString("current_user_standard", "1");
                            String name = sp.getString("current_user_name", null);
                            Intent i = new Intent(Parent.this, Teacher_Main.class);
                            i.putExtra("current_user_id", current_user_id);
                            i.putExtra("current_user_name", name);
                            i.putExtra("current_user_standard", standard);
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }
        },1500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            String user_type = data.getStringExtra("user_type");
            if(!user_type.equals("admin"))
            {
                String id = data.getStringExtra("current_user_id");
                String name = data.getStringExtra("current_user_name");
                String standard = data.getStringExtra("current_user_standard");
                if(user_type.equals("student")) {
                    Intent i = new Intent(Parent.this, Subjects_3.class);
                    String fname = data.getStringExtra("current_user_fathername");
                    String roll = data.getStringExtra("current_user_roll");
                    String dob = data.getStringExtra("current_user_dob");
                    i.putExtra("current_user_id", id);
                    i.putExtra("current_user_name", name);
                    i.putExtra("current_user_standard", standard);
                    i.putExtra("current_user_dob",dob);
                    i.putExtra("current_user_fathername",fname);
                    i.putExtra("current_user_roll",roll);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(Parent.this, Teacher_Main.class);
                    i.putExtra("current_user_id", id);
                    i.putExtra("current_user_name", name);
                    i.putExtra("current_user_standard", standard);
                    startActivity(i);
                    finish();
                }
            }
            else
            {
                Intent i = new Intent(Parent.this,AdminPortal.class);
                startActivity(i);
                finish();
            }
        }
        else
        {
            finish();
        }
    }
}
