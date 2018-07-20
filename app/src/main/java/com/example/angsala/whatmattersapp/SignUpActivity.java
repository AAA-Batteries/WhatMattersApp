package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    static String TAG = "LoginActivity";
    static String TOAST_CODE = "CODE";
    int LOGIN_CODE = 1;
    int CREATE_CODE = 2;

    EditText signUpusername;
    EditText signUppassword;
    Button signUpcreatebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpusername = (EditText) findViewById(R.id.signUpUsername);
        signUppassword = (EditText) findViewById(R.id.signUpPassword);
        signUpcreatebutton = (Button) findViewById(R.id.signUpcreateButton);


        signUpcreatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = signUpusername.getText().toString();
                String passwordText = signUppassword.getText().toString();
                createAccountHelper(usernameText, passwordText);
            }
        });


    }


    public void createAccountHelper(String mUsername, String mPassword) {
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
                                newUser.signUpInBackground(
                                        new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // create notification object for the new user
                                                    Notification notif = new Notification();
                                                    notif.setUserReceived(newUser.getObjectId());
                                                    notif.setReceived(new ArrayList<Message>());

                                                    Intent intent = new Intent(SignUpActivity.this, ContactActivity.class);
                                                    intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(newUser));
                                                    intent.putExtra(TOAST_CODE, CREATE_CODE);
                                                    startActivity(intent);
                                                } else {
                                                    Log.e(TAG, "Failed to create Account");
                                                    Toast.makeText(
                                                            SignUpActivity.this, "Failed to Create Account", Toast.LENGTH_LONG)
                                                            .show();
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
}
