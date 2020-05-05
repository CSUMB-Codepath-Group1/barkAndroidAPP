package com.example.bark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class EventsActivity extends AppCompatActivity {
    final private String EVENTS_ACTIVITY = "EventsActivity";

    private Button btnLogout;
    private  Button btnCreateEvent;

    private RecyclerView eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(EVENTS_ACTIVITY, "Entered events main activity");
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            goLoginActivity();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_compose);
        btnLogout = findViewById(R.id.logoutBtnEvents);
        btnCreateEvent = findViewById(R.id.newEvent);

        eventList = findViewById(R.id.listOfEvents);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(EVENTS_ACTIVITY, "Logging out.");
                //logs user out using firebase

                Log.d(EVENTS_ACTIVITY, "logged out user: " + auth.getCurrentUser());
                auth.signOut();
                goLoginActivity();
            }
        });
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Attempting to create new event", Toast.LENGTH_SHORT);
            }
        });
    }
        public void goLoginActivity()
        {
            //kicks user back out to the login page.
            Intent intent = new Intent(EventsActivity.this, LoginActivity.class);
            startActivity(intent);
        }


}