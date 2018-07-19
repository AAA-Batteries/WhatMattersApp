package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

  static String TAG = "LoginActivity";
  static String TOAST_CODE = "CODE";
  int LOGIN_CODE = 1;
  int CREATE_CODE = 2;
  EditText loginUsername;
  EditText loginPassword;
  Button loginButton;
  Button createButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // test

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser == null) {
      loginUsername = (EditText) findViewById(R.id.loginUsername);
      loginPassword = (EditText) findViewById(R.id.loginPassword);
      loginButton = (Button) findViewById(R.id.loginButton);
      createButton = (Button) findViewById(R.id.signUpcreateButton);

      loginButton.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final String username = loginUsername.getText().toString();
              final String password = loginPassword.getText().toString();
              loginHelper(username, password);
            }
          });

      createButton.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              final String newusername = loginUsername.getText().toString();
//              final String newpassword = loginPassword.getText().toString();
//              createAccountHelper(newusername, newpassword);
                Intent createAccountIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(createAccountIntent);
            }
          });
    }
    else{
        Intent i = new Intent (LoginActivity.this, ContactActivity.class);
        i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(currentUser));

        startActivity(i);
    }
    }


  public void loginHelper(String mUsername, String mPassword) {

    ParseUser.logInInBackground(
        mUsername,
        mPassword,
        new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {
              Log.d(TAG, "Login successful");

              ArrayList<String> contactsTest = (ArrayList<String>) user.get("contacts");
             Log.d(TAG, contactsTest.toString());
              //  Log.d(TAG, contactsTest.get(0) + "THIS IS THE ONE JERRY");
                Intent intent = new Intent(LoginActivity.this, ContactActivity.class);
                intent.putExtra(TOAST_CODE, LOGIN_CODE);
                intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));

              startActivity(intent);
            } else {
              Log.e(TAG, "Failed to login");
              Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_LONG).show();
              e.printStackTrace();
            }
          }
        });
  }

  //    public void loginHelper(String mUsername, String mPassword){
  //
  //        ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
  //            @Override
  //            public void done(ParseUser user, ParseException e) {
  //                if (user!= null){
  //                    Log.d(TAG, "Login successful");
  //                    Intent intent = new Intent(LoginActivity.this, ContactActivity.class);
  //
  //                    intent.putExtra(TOAST_CODE, LOGIN_CODE);
  //                    ArrayList<String> contactsTest = (ArrayList<String>) user.get("contacts");
  //                   // Log.d(TAG, contactsTest.toString());
  //                   // Log.d(TAG, contactsTest.get(0) + "THIS IS THE ONE JERRY");
  //                    intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
  //
  //                    startActivity(intent);
  //                }
  //                else{
  //                    Log.e(TAG, "Failed to login");
  //                    Toast.makeText(LoginActivity.this, "Failed to login",
  // Toast.LENGTH_LONG).show();
  //                    e.printStackTrace();
  //                }
  //            }
  //          }
  //        });
  //  }

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
                          Intent intent = new Intent(LoginActivity.this, ContactActivity.class);
                          intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(newUser));
                          intent.putExtra(TOAST_CODE, CREATE_CODE);
                          startActivity(intent);
                        } else {
                          Log.e(TAG, "Failed to create Account");
                          Toast.makeText(
                                  LoginActivity.this, "Failed to Create Account", Toast.LENGTH_LONG)
                              .show();
                          e.printStackTrace();
                        }
                      }
                    });
              } else {
                Log.d(TAG, "Username has already been taken");
                Toast.makeText(LoginActivity.this, "Username taken", Toast.LENGTH_LONG).show();
              }
            }
          }
        });
  }
}
