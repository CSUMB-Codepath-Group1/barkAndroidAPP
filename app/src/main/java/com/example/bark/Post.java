package com.example.bark;

import android.util.Log;

public class Post {
    final private String TAG = "Post";
    private String uName;
    private String description;
    private String image;
    private String postID;
    private String uId;

    public Post(){

    }
    public Post(String uName,String description,String image,String postID,String uId){
        this.uName = uName;
        this.uId = uId;
        this.description = description;
        this.image = image;
        this.postID = postID;
    }

    public String getuName() {
        Log.d(TAG, "getuName: "+uName);
        return uName;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getPostID() {
        return postID;
    }

    public String getuId() {
        return uId;
    }
}
