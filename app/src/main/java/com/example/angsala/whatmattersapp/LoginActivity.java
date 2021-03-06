package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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
    AppCompatCheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // test

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            checkBox = (AppCompatCheckBox) findViewById(R.id.checkbox);
            loginUsername = (EditText) findViewById(R.id.loginUsername);
            loginPassword = (EditText) findViewById(R.id.loginPassword);
            loginButton = (Button) findViewById(R.id.loginButton);
            createButton = (Button) findViewById(R.id.signUpcreateButton);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (!isChecked){
                        loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            });

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
                            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            Intent i = new Intent(LoginActivity.this, BottomNavigation.class);
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
                            Intent intent = new Intent(LoginActivity.this, BottomNavigation.class);
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
}
