package com.example.bark;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editTextFullname;
    private EditText editTextUsername;
    private EditText editTextPhone;
    private EditText editTextAge;
    private EditText editTextBio;
    private ExampleDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Enter Profile Information")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fullname = editTextFullname.getText().toString();
                        String username = editTextUsername.getText().toString();
                        String phone = editTextPhone.getText().toString();
                        String age = editTextAge.getText().toString();
                        String bio = editTextBio.getText().toString();
                        listener.applyTexts(fullname, username, phone, age, bio);
                    }
                });
        editTextFullname = view.findViewById(R.id.edit_fullname);
        editTextUsername = view.findViewById(R.id.edit_username);
        editTextPhone = view.findViewById(R.id.edit_phone);
        editTextAge = view.findViewById(R.id.edit_age);
        editTextBio = view.findViewById(R.id.edit_bio);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (ExampleDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String fullname, String username, String phone, String age, String bio);
    }
}
