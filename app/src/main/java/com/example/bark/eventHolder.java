package com.example.bark;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class eventHolder extends RecyclerView.ViewHolder {
    public TextView name, datetime, description, organizer;

    public eventHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.eventNameCard);
        datetime = itemView.findViewById(R.id.eventTimeCard);
        description = itemView.findViewById(R.id.eventDescription);
        organizer = itemView.findViewById(R.id.eventOrganizer);
    }
}
