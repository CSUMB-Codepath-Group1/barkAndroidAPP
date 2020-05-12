package com.example.bark.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bark.Event;
import com.example.bark.LoginActivity;
import com.example.bark.MakeEventActivity;
import com.example.bark.R;
import com.example.bark.eventHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE= 42;
    final private String EVENTS_FRAGMENT = "EventsFragment";

    private Button btnLogout;
    private FloatingActionButton btnCreateEvent;
    RecyclerView recyclerView;
    DatabaseReference ref;

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
        System.out.println("Entered events activity");
//TODO: figure out how to populate recycler view through firebase
        FirebaseRecyclerOptions<Event> options;
        final FirebaseRecyclerAdapter<Event, eventHolder> adapter;

        btnLogout = view.findViewById(R.id.logoutBtnEvents);
        btnCreateEvent = view.findViewById(R.id.newEvent);
        recyclerView = view.findViewById(R.id.listOfEvents);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.
        recyclerView.setHasFixedSize(false);
        ref = FirebaseDatabase.getInstance().getReference().child("events");
        final FirebaseAuth instance = FirebaseAuth.getInstance();

        options=new FirebaseRecyclerOptions.Builder<Event>().setQuery(ref, Event.class).build();
        adapter=new FirebaseRecyclerAdapter<Event, eventHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull eventHolder holder, int position, @NonNull Event model) {
                holder.name.setText(model.getName());
                holder.description.setText(model.getDescription());
                holder.datetime.setText(model.getDatetime());
                holder.organizer.setText(model.getOrganizer());
                System.out.println("This is the name of the thing: " + holder.name);
            }

            @NonNull
            @Override
            public eventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new eventHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


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
                Toast.makeText(getContext(),"Attempting to create new event", Toast.LENGTH_SHORT).show();
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
