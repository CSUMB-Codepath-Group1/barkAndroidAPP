package com.example.bark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bark.Event;
import com.example.bark.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class eventAdapter extends FirestoreRecyclerAdapter<Event, eventAdapter.eventHolder> {


    public eventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull eventHolder holder, int position, @NonNull Event model) {
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
        holder.organizer.setText(model.getOrganizer());
        holder.date.setText(model.getDatetime());
    }

    @NonNull
    @Override
    public eventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);
        return new eventAdapter.eventHolder(v);
    }

    class eventHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView time;
        TextView description;
        TextView organizer;
        public eventHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.datetime);
//            time = itemView.findViewById(R.id.chooseTime);
            description = itemView.findViewById(R.id.description);
            organizer = itemView.findViewById(R.id.organizer);
        }
    }
}