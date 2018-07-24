package com.example.angsala.whatmattersapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PriorityActivity extends AppCompatActivity {

    Spinner spinnerParents;
    Spinner spinnerFamily;
    Spinner spinnerFriends;
    Spinner spinnerClassmates;
    Spinner spinnerProfessors;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);

        //setting the different spinners
        spinnerParents = findViewById(R.id.spinnerParents);
        ArrayAdapter<CharSequence> adapterParents = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterParents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParents.setAdapter(adapterParents);

        spinnerFamily = findViewById(R.id.spinnerFamily);
        ArrayAdapter<CharSequence> adapterFamily = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFamily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFamily.setAdapter(adapterFamily);

        spinnerFriends = findViewById(R.id.spinnerFriends);
        ArrayAdapter<CharSequence> adapterFriends = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFriends.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFriends.setAdapter(adapterFriends);


        spinnerClassmates = findViewById(R.id.spinnerClassmates);
        ArrayAdapter<CharSequence> adapterClassmates = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterClassmates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassmates.setAdapter(adapterClassmates);


        spinnerProfessors = findViewById(R.id.spinnerProfessors);
        ArrayAdapter<CharSequence> adapterProfessors = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterProfessors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfessors.setAdapter(adapterProfessors);
    }

}
