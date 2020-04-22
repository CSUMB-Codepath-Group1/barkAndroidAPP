package com.example.bark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1234){

            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(),"Successfully Signed In",Toast.LENGTH_SHORT);

            }
        }else {
            Toast.makeText(getApplicationContext(),"Unable to Sign in",Toast.LENGTH_SHORT);
        }
    }
     public void signIn(View view){
         FirebaseAuth auth = FirebaseAuth.getInstance();
         if (auth.getCurrentUser() != null){
             // Already signed In
             Toast.makeText(getApplicationContext(),"User is Already signed in", Toast.LENGTH_SHORT);

         }else{
             // Not Signed In
             List<AuthUI.IdpConfig> providers = Arrays.asList(
                     new  AuthUI.IdpConfig.EmailBuilder().build());

             startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),1234);
         }

     }



}
