package com.example.bark.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bark.Event;
import com.example.bark.LoginActivity;
import com.example.bark.MakeEventActivity;
import com.example.bark.R;
import com.example.bark.eventAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE= 42;
    final private String EVENTS_FRAGMENT = "EventsFragment";

    private Button btnLogout;
    private FloatingActionButton btnCreateEvent;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");

    private eventAdapter adapter;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(view);

        btnLogout = view.findViewById(R.id.logoutBtnEvents);
        btnCreateEvent = view.findViewById(R.id.newEvent);

        final FirebaseAuth instance = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(EVENTS_FRAGMENT, "Logging out.");
                //logs user out using firebase
                Log.d(EVENTS_FRAGMENT, "logging off: "+instance.getCurrentUser());
                signOut(instance);
                goLoginActivity();
            }
        });
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MakeEventActivity.class);
                startActivity(intent);
//                Toast.makeText(getContext(),"Attempting to create new event", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUpRecyclerView(View view)
    {
        Query query = eventRef.orderBy("datetime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        adapter= new eventAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.listOfEvents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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
