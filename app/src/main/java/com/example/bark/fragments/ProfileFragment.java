package com.example.bark.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.bark.AddPostActivity;
import com.example.bark.LoginActivity;
import com.example.bark.ProfileActivity;
import com.example.bark.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    final private String PROFILE_FRAGMENT = "ProfileFragment";

    private Button btnProfile;
    private Button btnLogout;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProfile = view.findViewById(R.id.btnProfile);
        btnLogout = view.findViewById(R.id.logoutBtn);

        final FirebaseAuth instance = FirebaseAuth.getInstance();


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(PROFILE_FRAGMENT, "Logging out.");
                //logs user out using firebase
                Log.d(PROFILE_FRAGMENT, "logging off: "+instance.getCurrentUser());
                signOut(instance);
                goLoginActivity();
            }
        });
    }
    public void goLoginActivity()
    {
        //kicks user back out to the login page.
        Intent intent = new Intent(getContext(), LoginActivity.class);
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

}
