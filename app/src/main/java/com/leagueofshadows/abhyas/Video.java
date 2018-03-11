package com.leagueofshadows.abhyas;

import android.graphics.Bitmap;


class Video {
    private String name;
    private String id;
    private String video_id;
    private String url;
    Video(String name,String id,String url,String video_id)
    {
        this.video_id=video_id;
        this.id=id;
        this.name=name;
        this.url = url;
    }

    public String getName() {
        return name;
    }



    public String getId() {
        return id;
    }

    String getUrl()
    {
        return url;
    }

    public String getVideo_id() {
        return video_id;
    }
}
