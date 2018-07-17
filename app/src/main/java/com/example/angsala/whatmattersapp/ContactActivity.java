package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.angsala.whatmattersapp.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ContactActivity extends AppCompatActivity {
    User user;
    List<String> contacts;
    ContactsAdapter adapter;
    RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);
        contacts = user.getContacts();
        adapter = new ContactsAdapter(contacts);
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);



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
                DialogFragment dialogFragment = new ContactFragment();
                dialogFragment.show(getSupportFragmentManager(), "contacts");
                return true;

           /* case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;*/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    public void MyContacts(){
       List<String> parseContacts;
        parseContacts = new List<String>();


        ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("User");
        pQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size()>0) {
                        ParseObject p = list.get(0);
                        if (p.getList("contacts")!=null) {
                            for (int i = 0; i < parseContacts.size(); i++) {
                                parseContacts[0] = p.getList("contacts");
                            }
                        }
                        else
                        {
                            parseContacts= null;
                        }
                    }}
            }
        });



    }


}
