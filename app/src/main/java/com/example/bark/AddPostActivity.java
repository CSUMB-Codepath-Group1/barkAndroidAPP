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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";

    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private TextView cancelAdd;
    //private boolean pictureAdded;

    private FirebaseAuth mAuth;

    ProgressDialog pd;
    private Bitmap bitmap = null;

    //String username;
    private String downLoadUrl;
    private String username;
    private Uri image_uri = null;
    private Task<Uri> image_bitmap_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //pictureAdded = false;
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
                if(image_uri == null && bitmap==null){
                    Toast.makeText(AddPostActivity.this, "No image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(description, String.valueOf(image_uri), mAuth, db, bitmap);
            }
        });

    }

    private void savePost(final String description, final String uri, FirebaseAuth mAuth, final FirebaseFirestore db, Bitmap bitmap) {
        if(bitmap == null){
            new ProgressDialog(AddPostActivity.this);
            pd.setMessage("Publishing post...");
            pd.show();

            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
            final String userID = firebaseUser.getUid();

            username = getUserName(firebaseUser, db);

            final String timestamp = String.valueOf(System.currentTimeMillis());

            final StorageReference reference = FirebaseStorage.getInstance().getReference()
                    .child("postImages")
                    .child("post_" + timestamp + ".jpeg");
            reference.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());

                            downLoadUrl = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){
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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                        }
                    });
        } else if (bitmap != null){
            handleUpload(bitmap);

            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
            final String userID = firebaseUser.getUid();
            final String timestamp = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> map = new HashMap<>();
            map.put("uId", userID);
            map.put("uName", firebaseUser.getDisplayName());
            map.put("postId", timestamp);
            map.put("description", description);
            map.put("image", image_bitmap_uri);
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
                    });}
        }


    public String getUserName(FirebaseUser firebaseUser, FirebaseFirestore db){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return null;
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
                                image_uri = null;
                                startActivityForResult(intent, 0);
                            }
                        }
                        else if (options[which].equals("Choose from Gallery")){
                            bitmap = null;
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
                    bitmap = (Bitmap) data.getExtras().get("data");
                    //Set Image to your Profile
                    ivPostImage.setImageBitmap(bitmap);
            }
        }
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    image_uri = data.getData();

                    ivPostImage.setImageURI(image_uri);
            }
        }
    }
    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_bitmap_uri = getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e.getCause() );
                    }
                });
    }

    private Task<Uri> getDownloadUrl(final StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);

                    }
                });
        return reference.getDownloadUrl();
    }
}
