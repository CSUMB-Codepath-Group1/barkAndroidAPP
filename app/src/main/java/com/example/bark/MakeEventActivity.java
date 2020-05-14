package com.example.bark;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bark.fragments.EventsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MakeEventActivity extends AppCompatActivity {
    final private String MAKE_EVENT_ACTIVITY = "MakeEventActivity";
    EditText eventName;
    EditText eventDate;
    EditText eventTime;
    EditText eventDescription;
    Button submitEvent;
    ProgressDialog pd;


    //TODO: add the date and time edit texts

    FirebaseAuth auth;
    DocumentReference eventReference;
    FirebaseFirestore firebaseFirestore;
    //things needed to make the actual event in the database
    String finalName ="";
    String finalDate="";
    String finalTime="";
    String finalDescription ="";
    //-----------------------
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        //sets the content view
        setContentView(R.layout.activity_event_maker);
        eventName = findViewById(R.id.eventName);
        submitEvent = findViewById(R.id.submitEvent);
        eventDate = findViewById(R.id.chooseDate);
        eventTime = findViewById(R.id.chooseTime);
        eventDescription = findViewById(R.id.description);

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout ll= (LinearLayout)inflater.inflate(R.layout.activity_calendar, null, false);
                CalendarView cv = (CalendarView) ll.getChildAt(0);
                cv.setVisibility(View.VISIBLE);

                cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month,
                                                    int dayOfMonth) {
                        // TODO Auto-generated method stub
                        String date = (month+1) + "/" + dayOfMonth + "/" + year;
                        eventDate.setText(date);
                    }
                });
                new AlertDialog.Builder(MakeEventActivity.this)
                        .setTitle("Event Calendar")
                        .setMessage("Select a day for your event.")
                        .setView(ll)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //updates the edit text to the date selected by the user
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // goes back
                                eventDate.setText("");
                            }
                        }
                ).show();
            }
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout ll= (LinearLayout)inflater.inflate(R.layout.activity_time_picker, null, false);
                TimePicker tp = (TimePicker) ll.getChildAt(0);
                //sets date to put into the time edit text
                Integer hour = tp.getCurrentHour();
                Integer minute = tp.getCurrentMinute();
                tp.setIs24HourView(false);
                tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                        String AMorPM;
                        if(hourOfDay < 12)
                        {
//                            AMorPM = "AM";
                            eventTime.setText(hourOfDay+":" + minute + " ");
                        }
                        else
                        {
//                            AMorPM = "PM";
                            eventTime.setText((hourOfDay)+":" + minute + " ");
                        }
                    }
                });

                new AlertDialog.Builder(MakeEventActivity.this)
                        .setTitle("Event Time")
                        .setMessage("Select a Time for your event.")
                        .setView(ll)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //updates the edit text to the date selected by the user
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // goes back
                                eventTime.setText("");
                            }
                        }
                ).show();
            }
        });
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                finalName = eventName.getText().toString();
                finalDate = eventDate.getText().toString();
                finalTime = eventTime.getText().toString();
                finalDescription =eventDescription.getText().toString();
                if((finalName.isEmpty() || finalDate.isEmpty() || finalTime.isEmpty() || finalDescription.isEmpty()))
                {
                    Toast.makeText(getApplicationContext(), "Unable to make event. All fields are required.", Toast.LENGTH_SHORT).show();
                    eventName.setText("");
                    eventDate.setText("");
                    eventTime.setText("");
                    eventDescription.setText("");
                }
                else
                {
                    pd = new ProgressDialog(MakeEventActivity.this);
                    pd.setMessage("Please wait...");
                    pd.show();
                    registerEvent(finalName, finalDate, finalTime, finalDescription);
                }

            }
        });
    }
    public void registerEvent(final String eventName, final String eventDate, final String eventTime, final String description)
    {
        FirebaseUser curr_user = auth.getCurrentUser();
        String userID = curr_user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", eventName);
        eventMap.put("datetime", ""+ eventDate+ " at " + eventTime);
        eventMap.put("organizer", curr_user.getDisplayName());
        eventMap.put("description", description);
        eventMap.put("uID", userID);

        db.collection("events")
                .add(eventMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(MAKE_EVENT_ACTIVITY, "event added with this id: "+ documentReference.getId());
                pd.dismiss();
                Intent intent = new Intent(MakeEventActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(MakeEventActivity.this, "Event Made!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(MAKE_EVENT_ACTIVITY, "Error, exception caught: " + e);
                Toast.makeText(MakeEventActivity.this, "Unable to make your event. Try again later.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }




}
