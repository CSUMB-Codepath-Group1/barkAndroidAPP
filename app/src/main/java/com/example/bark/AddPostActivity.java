package com.example.bark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";

    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private TextView cancelAdd;
    private boolean pictureAdded;

    private FirebaseAuth mAuth;

    ProgressDialog pd;

    String username;
    private String downLoadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        pictureAdded = false;
        mAuth = FirebaseAuth.getInstance();

        btnCaptureImage = findViewById(R.id.btnCapturePicture);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        etDescription = findViewById(R.id.etDescription);
        cancelAdd = findViewById(R.id.cancelAdd);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImageClick(v);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();

                if(description.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pictureAdded){
                    Toast.makeText(AddPostActivity.this, "No image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(description, String.valueOf(ivPostImage), mAuth, db);
            }
        });

    }

    private void savePost(final String description, final String ivPostImage, FirebaseAuth mAuth, final FirebaseFirestore db) {
        pd = new ProgressDialog(AddPostActivity.this);
        pd.setMessage("Publishing post...");
        pd.show();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String userID = firebaseUser.getUid();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> map = new HashMap<>();
        map.put("uId", userID);
        map.put("uName", firebaseUser.getDisplayName());
        map.put("postId", timestamp);
        map.put("description", description);
        map.put("image", downLoadUrl);
        db.collection("posts")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddPostActivity.this, "Post made", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        pd.dismiss();
                        Intent i = new Intent(AddPostActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPostActivity.this, "Error posting", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                        pd.dismiss();
                    }
                });
    }

    public void handleImageClick(View view) {
        //Take an image from the App. Start a new Intent to for Capturing an image.
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, 0);
                            }
                        }
                        else if (options[which].equals("Choose from Gallery")){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
                        }
                        else if(options[which].equals("Cancel")){
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //Set Image to your Profile
                    ivPostImage.setImageBitmap(bitmap);
                    //Save Profile Image to FireBase
                    handleUpload(bitmap);
            }
        }
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bm = null;
                    if(data != null){
                        try{
                            bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            Toast.makeText(AddPostActivity.this, "Error, " +e, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    ivPostImage.setImageBitmap(bm);
                    handleUpload(bm);
            }
        }
    }

    private void handleUpload(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("postImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downLoadUrl = getDownloadUrl(reference);
                        pictureAdded = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e.getCause() );
                    }
                });
    }

    private String getDownloadUrl(StorageReference reference) {
        downLoadUrl = String.valueOf(reference.getDownloadUrl());
        return downLoadUrl;
    }
}
