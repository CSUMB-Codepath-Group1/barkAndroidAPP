package com.example.bark;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MakeEventActivity extends AppCompatActivity {
    final private String MAKE_EVENT_ACTIVITY = "MakeEventActivity";
    EditText eventName;
    EditText eventDate;
    EditText eventTime;
    Button submitEvent;

    //TODO: add the date and time edit texts

    FirebaseAuth auth;
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
                tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String AMorPM;
                        if(hourOfDay < 12)
                        {
                            AMorPM = "AM";
                            eventTime.setText(hourOfDay+":" + minute + " " + AMorPM);
                        }
                        else
                        {
                            AMorPM = "PM";
                            eventTime.setText((hourOfDay-12)+":" + minute + " " + AMorPM);
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
                String finalName = eventName.getText().toString();
                String finalDate = eventDate.getText().toString();
                String finalTime = eventTime.getText().toString();

            }
        });
    }




}
