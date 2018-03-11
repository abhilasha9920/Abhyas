package com.leagueofshadows.abhyas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class Videoview extends YouTubeBaseActivity {
    YouTubePlayerView ytv;
    YouTubePlayer yt;
    int RECOVERY_REQUEST=1;
    boolean fullscreen=false;
    String id;
    String name;
    String video_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        name = i.getStringExtra("name");
        video_id = i.getStringExtra("video_id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        ytv = (YouTubePlayerView)findViewById(R.id.youtubeview);
        ytv.setFitsSystemWindows(true);
        ytv.initialize(getString(R.string.api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                yt=youTubePlayer;
                if (!b) {
                    youTubePlayer.cueVideo(video_id);
                    yt.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            fullscreen=b;
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(Videoview.this, RECOVERY_REQUEST).show();
                } else {

                    Toast.makeText(Videoview.this,"edho aindhi saaavu", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button button  = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Videoview.this,Test.class);
                i.putExtra("id",id);
                i.putExtra("videoname",name);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings : Intent i = new Intent(this,Settings.class);
                startActivity(i);
                return true;
            case R.id.action_refresh :
               Toast.makeText(this,"reload the video",Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
