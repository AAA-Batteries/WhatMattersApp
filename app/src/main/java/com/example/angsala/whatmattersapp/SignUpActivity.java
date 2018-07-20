package com.example.angsala.whatmattersapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcels;

import static com.example.angsala.whatmattersapp.LoginActivity.TOAST_CODE;

public class SignUpActivity extends AppCompatActivity {
    static String TAG = "SignUpActivity";
    int CREATE_CODE = 2;
    Button btSignUp;
    EditText etUsername;
    EditText etPassword;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btSignUp = findViewById(R.id.btSignUp);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);




        btSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String newusername = etUsername.getText().toString();
                        final String newpassword = etPassword.getText().toString();
                        createAccountHelper(newusername, newpassword);
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
                                                    Intent intent = new Intent(SignUpActivity.this, ContactActivity.class);
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
}
