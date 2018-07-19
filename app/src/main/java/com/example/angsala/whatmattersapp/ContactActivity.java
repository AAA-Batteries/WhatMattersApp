package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactFragment.ContactFragmentListener {
    private static final String TAG = "1";
    ParseUser user;
    ArrayList<String> contacts;
    ContactsAdapter adapter;
    RecyclerView rvContacts;
    String testuser;
    boolean isVerified;
    public static boolean notfinished = true;
    Handler myHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (isVerified) {
                Log.d("TestActivity", String.valueOf(isVerified));
                addContacts(testuser);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        user = ParseUser.getCurrentUser();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);
        contacts = new ArrayList<>();
        // contacts = user.getContacts();
        adapter = new ContactsAdapter(contacts);
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
        if (user != null) {
            mContacts(user);
        }
        Log.d("ContactActivity", contacts.toString());
        // userExists("FakeJill");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addContact:
                openDialog();
                return true;

            case R.id.logOut:
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
                Log.d(TAG, "pushed the logout button");
                Intent i = new Intent(ContactActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    public void mContacts(ParseUser user) {
        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));
        List<String> contactsTest = (List<String>) user.get("contacts");
        if (contactsTest != null) {
            contacts.addAll(contactsTest);
            Log.d("Exist", contacts.toString());
            adapter.notifyItemInserted(contacts.size() - 1);
        }
    }

    public List<String> addContacts(String newUser) {
        user.add("contacts", newUser);
        contacts.add(newUser);
        user.saveInBackground();
        adapter.notifyItemInserted(contacts.size() - 1);
        Log.d("ContactzActivity", contacts.toString());
        return contacts;

    }

    public void openDialog() {
        ContactFragment contactFragment = new ContactFragment();
        contactFragment.show(getSupportFragmentManager(), "contact fragment");
    }

    @Override
    public void applyTexts(String userName) {
        testuser = userName;
        Log.d("ContactActivity", testuser);
        checkVerified(testuser);
        if (!userExists(testuser)) {
            myHandler.postDelayed(runnable, 5000);
        } else {
            Toast.makeText(getApplicationContext(), "You already have this user in your contacts", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkVerified(String verified) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", verified);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count == 1) {
                        isVerified = true;
                        Log.d("TestActivities", String.valueOf(isVerified));
                    } else if (count == 0) {
                        isVerified = false;

                    }
                } else {
                    Log.d("TestActivity", "retrieval failed");
                }
            }
        });
    }

    public boolean userExists(String userExist) {
        return contacts.contains(userExist);
    }
}
