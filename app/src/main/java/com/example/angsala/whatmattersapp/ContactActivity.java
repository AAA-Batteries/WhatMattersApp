package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseUser;

public class ContactActivity extends AppCompatActivity {
static String TAG = "ContactActivity";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contact);

    makeToast();
  }

  private void makeToast() {
    // if 10, then something has gone wrong
    int i = getIntent().getIntExtra("CODE", 10);

    if (i == 1) {
      Toast.makeText(
              ContactActivity.this,
              "Welcome Back " + ParseUser.getCurrentUser().getUsername(),
              Toast.LENGTH_LONG)
          .show();
    } else if (i == 2) {
        Toast.makeText(
                ContactActivity.this,
                "Welcome " + ParseUser.getCurrentUser().getUsername(),
                Toast.LENGTH_LONG)
                .show();
    } else {
        Log.e(TAG, "Error, kind of user not found");
    }
  }
}
