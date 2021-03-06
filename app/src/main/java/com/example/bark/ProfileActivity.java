package com.example.bark;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bark.fragments.ProfileFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private static final String TAG = "ProfileActivity";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String docId;
    private CircleImageView profileImageView;
    private TextView displayNameEditText;
    private EditText displayEmailEditText;
    private TextView displayPhoneEditText;
    private TextView displayDescriptionEditText;
    private TextView displayUserNameEditText;
    private TextView displayAgeEditText;
    private Button updateProfileButton;
    private Button back_btn;
    private Button btnLogout;
    private Button editButton;
    private ProgressBar progressBar;

    private AlertDialog dialog;
    private String DISPLAY_NAME = null;
    private String PHONENUMBER =  null;
    private String DESCRIPTION = null;
    private String NAME = null;
    private String EMAIL  = null;
    private String AGE = null;
    private String PROFILE_IMAGE_URL = null;
    private int TAKE_IMAGE_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        displayEmailEditText = findViewById(R.id.displayEmailEditText);
        displayPhoneEditText = findViewById(R.id.displayPhoneEditText);
        displayDescriptionEditText = findViewById(R.id.displayDescriptionEditText);
        displayUserNameEditText = findViewById(R.id.displayUserNameEditText);
        displayAgeEditText = findViewById(R.id.displayAgeEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        back_btn = findViewById(R.id.back_btn);
        btnLogout = findViewById(R.id.logoutBtn);
        editButton = findViewById(R.id.editButton);
        progressBar = findViewById(R.id.progressBar);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


        final FirebaseAuth instance = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Logging out.");
                //logs user out using firebase
                Log.d(TAG, "logging off: "+ instance);
                signOut(instance);
                goLoginActivity();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            Log.d(TAG, "onCreate: " + user.getDisplayName());
//            if (user.getDisplayName() != null) {
//                displayNameEditText.setText(user.getDisplayName());
//                displayNameEditText.setSelection(user.getDisplayName().length());
//            }
            Log.d(TAG, "onCreate: " + user.getEmail());
            if(user.getEmail() != null){
                displayEmailEditText.setText(user.getEmail());
                displayEmailEditText.setSelection(user.getEmail().length());
            }
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(profileImageView);
            }
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.get("id").equals(user.getUid()) && document != null) {
                                        docId = document.getId();
                                        DISPLAY_NAME = document.get("username").toString();
                                        displayNameEditText.setText(DISPLAY_NAME);
                                        PHONENUMBER = document.get("phone").toString();
                                        displayPhoneEditText.setText(PHONENUMBER);
                                        DESCRIPTION = document.get("bio").toString();
                                        displayDescriptionEditText.setText(DESCRIPTION);
                                        NAME = document.get("fullname").toString();
                                        displayUserNameEditText.setText(NAME);
                                        AGE = document.get("age").toString();
                                        displayAgeEditText.setText(AGE);
                                        Log.d(TAG, document.get("id") + " " + user.getUid());
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        progressBar.setVisibility(View.GONE);

    }

    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "exampledialog");
    }

    @Override
    public void applyTexts(String fullname, String username, String phone, String age, String bio) {
        displayUserNameEditText.setText(fullname);
        displayNameEditText.setText(username);
        displayPhoneEditText.setText(phone);
        displayAgeEditText.setText(age);
        displayDescriptionEditText.setText(bio);

    }

    public void goLoginActivity()
    {
        //kicks user back out to the login page.
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        intent.putExtra("finish", true);
        // allows us to clear the activities that were being held in the stack prior to logging out
        // this means that the user cannot do a 'shadow login' when they hit back after signing out and be
        // signed in as a 'null' user
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void signOut(FirebaseAuth instance)
    {
        instance.signOut();
    }
    public void backButton(final View view){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void updateProfile (final View view) {
        view.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        DISPLAY_NAME = displayNameEditText.getText().toString();
        PHONENUMBER  = displayPhoneEditText.getText().toString();
        DESCRIPTION = displayDescriptionEditText.getText().toString();
        EMAIL =  displayEmailEditText.getText().toString();
        NAME = displayUserNameEditText.getText().toString();
        AGE = displayAgeEditText.getText().toString();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(docId);

        Uri imageUrl = firebaseUser.getPhotoUrl();

        Map <String, Object> profileInfo = new HashMap<>();
        profileInfo.put("username", DISPLAY_NAME);
        profileInfo.put("fullname", NAME);
        profileInfo.put("phone", PHONENUMBER);
        profileInfo.put("bio", DESCRIPTION);
        profileInfo.put("age", AGE);
        profileInfo.put("imageurl", imageUrl.toString());

        docRef.update(profileInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "OnSuccess: It worked!!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure", e);
                    }
                });


        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(DISPLAY_NAME)
                .build();

        firebaseUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Succesfully updated profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }
    //Handle Image Click (OnClick Listener)
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
                        startActivityForResult(intent, TAKE_IMAGE_CODE);
                    }
                }
                else if (options[which].equals("Choose from Gallery")){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if(options[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //Set Image to your Profile
                    profileImageView.setImageBitmap(bitmap);
                    //Save Profile Image to FireBase
                    handleUpload(bitmap);
            }
        }
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = null;
                    if (data != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            //Toast.makeText(AddPostActivity.this, "Error, " + e, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    profileImageView.setImageBitmap(bitmap);
                    handleUpload(bitmap);
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
                        getDownloadUrl(reference);
                        Log.e(TAG, "onSuccess: uploaded profile pic");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause() );
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
