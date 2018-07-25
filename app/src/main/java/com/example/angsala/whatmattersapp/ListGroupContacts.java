package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.angsala.whatmattersapp.model.Contacts;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListGroupContacts extends AppCompatActivity {
    RecyclerView rvListGroupContact;
    ListGroupContactsAdapter adapter;
    List<Contacts> contactsList;
    ParseUser user;
    String username;
    String relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_contacts);
        contactsList = new ArrayList<>();
        adapter = new ListGroupContactsAdapter(contactsList);
        rvListGroupContact = findViewById(R.id.rvListGroupContacts);
        rvListGroupContact.setLayoutManager(new LinearLayoutManager(this));
        rvListGroupContact.setAdapter(adapter);
        loadContacts();

    }


    public void loadContacts(){
        user = ParseUser.getCurrentUser();
        username = user.getUsername();
        ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class)
                .whereEqualTo("Owner", username)
                .whereEqualTo("Relationship", relationship )
                .orderByDescending("Ranking");
        query.findInBackground(new FindCallback<Contacts>() {
            @Override
            public void done(List<Contacts> objects, ParseException e) {
                if (e == null){
                    contactsList.addAll(objects);
                    adapter.notifyItemInserted(contactsList.size() - 1);

                }
                else {
                    e.printStackTrace();
                }
            }
        });

    }
}
