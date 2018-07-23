package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class MyContactsFragment extends Fragment implements ContactFragment.ContactFragmentListener {

    private static final String TAG = "MyContactsFragment";
    ParseUser user;
    ContactsAdapter adapter;
    RecyclerView rvContacts;
    String testuser;
    String testrelationship;
    boolean isVerified;
    List<Contacts> contactsList;
    Handler myHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (isVerified) {
                Log.d("TestActivity", String.valueOf(isVerified));
                addContacts(testuser, testrelationship);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar_contacts);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        contactsList = new ArrayList<>();
        adapter = new ContactsAdapter(contactsList);

        rvContacts = view.findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContacts.setAdapter(adapter);
        myContacts();
    }




    public List<Contacts> addContacts(String contact, String relationship) {

        Contacts contactCurr = new Contacts();

        String uname = user.getUsername();
        contactCurr.setOwner(uname);
        contactCurr.setContactName(contact);
        contactCurr.setRelationship(relationship);
        //to implement, fix casting
        int rank = makeRanking(relationship, user);
        contactCurr.setRanking(rank);

        Log.d("addContacts method", rank + " uname: "+ uname + " contact name: " + contact + " relationship: " + relationship);

        contactCurr.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("addContacts method", "posted");
            }
        });
        contactsList.add(contactCurr);


        //TODO- determine how other contact will be posted
        Contacts contactOther = new Contacts();
        contactOther.setOwner(contact);
        contactOther.setContactName(user.getUsername());
        contactOther.setRelationship(relationship);
        contactOther.saveInBackground();


        adapter.notifyItemInserted(contactsList.size() - 1);
        Log.d("ContactActivity", contactsList.toString());
        return contactsList;

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
                Log.d(TAG, "Clicked on logout button");
                user.logOut();
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
            myHandler.postDelayed(runnable, 2000);
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
        return contactsList.contains(userExist);
    }


    @Override
    public void applyTexts(String userName, String relationship) {
        Log.d(TAG, userName.toString());
        Log.d(TAG, relationship);
    if (!relationship.equalsIgnoreCase("Relationships…")) {
      testuser = userName;
      testrelationship = relationship;
      receiveText(testuser);
        }
        else {
        Log.d(TAG, "Failed to add contact. Select Relationship");
        Toast.makeText(getContext(),"Failed to add contact. Select Relationship", Toast.LENGTH_SHORT).show();
    }
            }




    public void myContacts() {
        String username;
        user = ParseUser.getCurrentUser();
        username = user.getUsername();
        ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class).whereEqualTo("Owner", username).orderByDescending("Ranking");
        Log.d("Fragment", user.getUsername());
        query.findInBackground(new FindCallback<Contacts>() {
            @Override
            public void done(List<Contacts> objects, ParseException e) {
                if (e == null) {
                    contactsList.addAll(objects);

                    adapter.notifyItemInserted(contactsList.size() - 1);
                    Log.d("Fragmentcontact", contactsList.toString());
                } else {
                    e.printStackTrace();
                }

            }
        });

    }


    public int makeRanking(String relationship, ParseUser OwnerUser){
        String relationship1 = user.getString("Relationship1");
        String relationship2 = user.getString("Relationship2");
        String relationship3 = user.getString("Relationship3");
        String relationship4 = user.getString("Relationship4");
        String relationship5 = user.getString("Relationship5");
        int basepoints = 10;

        //look at the relationship ranking from the current User
        if(relationship.equalsIgnoreCase(relationship1)){return 3*basepoints;}
        else if(relationship.equalsIgnoreCase(relationship2)){return (int)(2.5*basepoints);}
        else if (relationship.equalsIgnoreCase(relationship3)){return 2*basepoints;}
        else if (relationship.equalsIgnoreCase(relationship4)){return (int) 1.5*basepoints;}
        else if (relationship.equalsIgnoreCase(relationship5)){return basepoints;}
        else{return 0;}
    }

}


