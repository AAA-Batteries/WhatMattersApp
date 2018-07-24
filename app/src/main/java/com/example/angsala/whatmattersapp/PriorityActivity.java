package com.example.angsala.whatmattersapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.ParseUser;

public class PriorityActivity extends AppCompatActivity {

    Spinner spinnerParents;
    Spinner spinnerFamily;
    Spinner spinnerFriends;
    Spinner spinnerClassmates;
    Spinner spinnerProfessors;
    Button resetBtn;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);
        resetBtn = findViewById(R.id.resetBtn);

        //setting the different spinners
        spinnerParents = findViewById(R.id.respinnerParents);
        ArrayAdapter<CharSequence> adapterParents = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterParents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParents.setAdapter(adapterParents);

        spinnerFamily = findViewById(R.id.respinnerFamily);
        ArrayAdapter<CharSequence> adapterFamily = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFamily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFamily.setAdapter(adapterFamily);

        spinnerFriends = findViewById(R.id.respinnerFriends);
        ArrayAdapter<CharSequence> adapterFriends = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFriends.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFriends.setAdapter(adapterFriends);


        spinnerClassmates = findViewById(R.id.respinnerClassmates);
        ArrayAdapter<CharSequence> adapterClassmates = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterClassmates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassmates.setAdapter(adapterClassmates);


        spinnerProfessors = findViewById(R.id.respinnerProfessors);
        ArrayAdapter<CharSequence> adapterProfessors = ArrayAdapter.createFromResource(PriorityActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterProfessors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfessors.setAdapter(adapterProfessors);

        // handles resetting priorities upon the reset button being clicked
        resetBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //should give you 1-5 ranking
                        final int rankParents = Integer.parseInt(spinnerParents.getSelectedItem().toString());
                        final int rankFamily = Integer.parseInt(spinnerFamily.getSelectedItem().toString());
                        final int rankFriends = Integer.parseInt(spinnerFriends.getSelectedItem().toString());
                        final int rankClassmates = Integer.parseInt(spinnerClassmates.getSelectedItem().toString());
                        final int rankProfessors = Integer.parseInt(spinnerProfessors.getSelectedItem().toString());
                        reset(rankParents, rankFamily, rankFriends, rankClassmates, rankProfessors);
                        finish();
                    }
                });
    }

    // resets the priority values for the current user, given the inputs to the spinners
    public void reset(int rankParents, int rankFamily, int rankFriends, int rankClassmates, int rankProfessors) {
        ParseUser curr = ParseUser.getCurrentUser();

        curr.put("Parents", rankParents);
        curr.put("Family", rankFamily);
        curr.put("Friends", rankFriends);
        curr.put("Classmates", rankClassmates);
        curr.put("Professors", rankProfessors);

        curr.saveInBackground();
    }

}
