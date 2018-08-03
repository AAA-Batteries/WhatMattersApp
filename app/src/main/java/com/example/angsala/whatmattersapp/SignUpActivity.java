package com.example.angsala.whatmattersapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.angsala.whatmattersapp.LoginActivity.TOAST_CODE;

public class SignUpActivity extends AppCompatActivity {
    static String TAG = "SignUpActivity";
    int CREATE_CODE = 2;
    Button btSignUp;
    EditText etUsername;
    EditText etPassword;
    Spinner spinnerParents;
    Spinner spinnerFamily;
    Spinner spinnerFriends;
    Spinner spinnerClassmates;
    Spinner spinnerProfessors;

    ParseUser currUser;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btSignUp = findViewById(R.id.btSignUp);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);


        //setting the different spinners
        spinnerParents = findViewById(R.id.spinnerParents);
        ArrayAdapter<CharSequence> adapterParents = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterParents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParents.setAdapter(adapterParents);

        spinnerFamily = findViewById(R.id.spinnerFamily);
        ArrayAdapter<CharSequence> adapterFamily = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFamily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFamily.setAdapter(adapterFamily);

        spinnerFriends = findViewById(R.id.spinnerFriends);
        ArrayAdapter<CharSequence> adapterFriends = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterFriends.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFriends.setAdapter(adapterFriends);


        spinnerClassmates = findViewById(R.id.spinnerClassmates);
        ArrayAdapter<CharSequence> adapterClassmates = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterClassmates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassmates.setAdapter(adapterClassmates);


        spinnerProfessors = findViewById(R.id.spinnerProfessors);
        ArrayAdapter<CharSequence> adapterProfessors = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.rankings, android.R.layout.simple_spinner_item);
        adapterProfessors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfessors.setAdapter(adapterProfessors);


        btSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String newusername = etUsername.getText().toString();
                        final String newpassword = etPassword.getText().toString();
                        //should give you 1-5 ranking
                        final String rankParents = (spinnerParents.getSelectedItem().toString());
                        final String rankFamily = (spinnerFamily.getSelectedItem().toString());
                        final String rankFriends = (spinnerFriends.getSelectedItem().toString());
                        final String rankClassmates = (spinnerClassmates.getSelectedItem().toString());
                        final String rankProfessors = (spinnerProfessors.getSelectedItem().toString());
                        if ((!rankParents.equalsIgnoreCase("Ranking…")) && (!rankFamily.equalsIgnoreCase("Ranking…"))
                                && (!rankFriends.equalsIgnoreCase("Ranking…")) && (!rankClassmates.equalsIgnoreCase("Ranking…")) &&
                                (!rankProfessors.equalsIgnoreCase("Ranking…"))) {
                            final int intParents = Integer.parseInt(rankParents);
                            final int intFamily = Integer.parseInt(rankFamily);
                            final int intFriends = Integer.parseInt(rankFriends);
                            final int intClassmates = Integer.parseInt(rankClassmates);
                            final int intProfessors = Integer.parseInt(rankProfessors);
                            createAccountHelper(newusername, newpassword, intParents, intFamily, intFriends, intClassmates, intProfessors);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Select ranking for all relationships", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void createAccountHelper(final String mUsername, String mPassword, final int rankParent, final int rankFamily, final int rankFriends, final int rankClassmates, final int rankProfessors) {
        // start of new code- determine if Username is taken
        final String u = mUsername;
        final String p = mPassword;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", u);
        query.countInBackground(
                new CountCallback() {
                    @Override
                    public void done(int count, ParseException e) {
                        if (e == null) {
                            if (count == 0) {
                                final ParseUser newUser = new ParseUser();
                                newUser.setUsername(u);
                                newUser.setPassword(p);
                                newUser.put("Parents", rankParent);
                                newUser.put("Family", rankFamily);
                                newUser.put("Friends", rankFriends);
                                newUser.put("Classmates", rankClassmates);
                                newUser.put("Professors", rankProfessors);
                                newUser.signUpInBackground(
                                        new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    createNotif(mUsername);
                                                    Intent intent = new Intent(SignUpActivity.this, BottomNavigation.class);
                                                    intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(newUser));
                                                    intent.putExtra(TOAST_CODE, CREATE_CODE);
                                                    startActivity(intent);
                                                } else {
                                                    Log.e(TAG, "Failed to create Account");
                                                    Toast.makeText(
                                                            SignUpActivity.this, "Failed to Create Account", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } else {
                                Log.d(TAG, "Username has already been taken");
                                Toast.makeText(SignUpActivity.this, "Username taken", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // check that a notification object exists for the current user
    // if doesn't exist, create one
    public void createNotif(String username) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                ParseUser user = object;
                if (e == null && object != null) {
                    currUser = user;

                    ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class)
                            .whereEqualTo("UserReceived", currUser);
                    query.getFirstInBackground(new GetCallback<Notification>() {
                        @Override
                        public void done(Notification notif, ParseException e) {
                            if (notif == null) {
                                Notification chatNotif = new Notification();
                                chatNotif.setReceived(new ArrayList<Message>());
                                chatNotif.setUserReceived(currUser.getObjectId());
                                chatNotif.saveInBackground();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
