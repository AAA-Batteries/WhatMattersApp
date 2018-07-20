package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.Contacts;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class MyContactsFragment extends Fragment implements ContactFragment.ContactFragmentListener {

    private static final String TAG = "1";
    ParseUser user;
    ArrayList<String> contacts;
    ContactsAdapter adapter;
    RecyclerView rvContacts;
    String testuser;
    boolean isVerified;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contacts = new ArrayList<>();
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }

    // sets the view for the contact screen, including the actionbar
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar_contacts);
        // myToolbar.setTitle("Contacts");
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");


        // contacts = user.getContacts();
        adapter = new ContactsAdapter(contacts);
        rvContacts = view.findViewById(R.id.rvContacts);
//        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
//        rvContacts.setAdapter(adapter);
        if (user != null) {
            mContacts(user);
        }
        Log.d("ContactActivity", contacts.toString());
        // userExists("FakeJill");
    }


    // get contacts for the user and populate the contacts list
    public void mContacts(ParseUser user) {
        user = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));
        List<String> contactsTest = (List<String>) user.get("contacts");
        if (contactsTest != null) {
            contacts.addAll(contactsTest);
            Log.d("Exist", contacts.toString());
            adapter.notifyItemInserted(contacts.size() - 1);
        }
    }

    // adds a contact to the users contacts
    public List<String> addContacts(String contact) {
        user.add("contacts", contact);
        contacts.add(contact);
        user.saveInBackground();

        // create a new contact item for the new contact where the current user is the owner
        Contacts contactCurr = new Contacts();

        contactCurr.setOwner(user.getUsername());
        contactCurr.setContactName(contact);
        // TODO implement relationship and ranking fields
        contactCurr.setRelationship("");
        contactCurr.setRanking(0);

        contactCurr.saveInBackground();

        // also create a matching contact item for the newly added contact where they are the owner
        Contacts contactOther = new Contacts();

        contactOther.setOwner(contact);
        contactOther.setContactName(user.getUsername());
        // TODO implement relationship and ranking fields
        contactOther.setRelationship("");
        contactOther.setRanking(0);

        contactOther.saveInBackground();


        adapter.notifyItemInserted(contacts.size() - 1);
        Log.d("ContactActivity", contacts.toString());
        return contacts;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addContact:

                openDialog();
                return true;

            case R.id.logOut:
                user.logOut();
                Log.d("MyContactsFragment", "I clicked the logout button");
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                return true;

            default:
                // If we got here, the user's action was not recognized
                return super.onOptionsItemSelected(item);
        }
    }

    public void openDialog() {
        FragmentManager fm = getFragmentManager();
        ContactFragment contactFragment = new ContactFragment();
        contactFragment.setTargetFragment(MyContactsFragment.this, 1);
        contactFragment.show(fm, "contact fragment");
    }


    public void receiveText(String userText) {
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


    @Override
    public void applyTexts(String userName) {
        Log.d("MyText", userName.toString());

    }
}


