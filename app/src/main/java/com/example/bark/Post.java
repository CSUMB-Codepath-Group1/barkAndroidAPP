package com.example.bark;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Post {
    final private String TAG = "Post";
    private String uName;
    private String description;
    private String image;
    private String postID;
    private String uId;
    private String name;
    private String userImage;



    public Post(){

    }
    public Post(String uName,String description,String image,String postID,String uId, String name, String userImage){
        this.userImage = userImage;
        this.uName = uName;
        this.uId = uId;
        this.description = description;
        this.image = image;
        this.postID = postID;
        this.name = name;
    }

    public String getUserImage() {
        Log.d(TAG, "getuimage: "+userImage);
        return userImage;
    }

    public void setUserImage(String userImage) {
//        Log.d(TAG, "getuimage: "+userImage);
        this.userImage = userImage;

    }
    public String getuName() {
        Log.d(TAG, "getuName: "+uName);
        return uName;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {

        Log.d(TAG, "IMAGE: "+image);
        return image;
    }

    public String getPostID() {
        return postID;
    }

    public String getuId() {
        return uId;
    }

    public String getName(){

        return name;};

}
