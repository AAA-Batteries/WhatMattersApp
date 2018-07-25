package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class MyContactsFragment extends Fragment
    implements AddUserFragment.ContactFragmentListener {

  private static final String TAG = "MyContactsFragment";
  ParseUser user;
  ContactsAdapter adapter;
  RecyclerView rvContacts;
  String testuser;
  String testrelationship;
  boolean isVerified;
  boolean doesExist;
  List<Contacts> contactsList;

  SwipeRefreshLayout swipeRefreshLayout;
  Handler myHandler = new Handler();

  Runnable runnable =
      new Runnable() {
        @Override
        public void run() {

          if (isVerified && !doesExist) {
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
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    //

    swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
    swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
              @Override
              public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refreshContacts();
              }
            }
    );

  }

  public List<Contacts> addContacts(String contact, String relationship) {

    Contacts contactCurr = new Contacts();

    String uname = user.getUsername();
    contactCurr.setOwner(uname);
    contactCurr.setContactName(contact);
    contactCurr.setRelationship(relationship);
    // to implement, fix casting
    int rank = makeRanking(relationship, user);
    contactCurr.setRanking(rank);

    Log.d(
        "addContacts method",
        rank + " uname: " + uname + " contact name: " + contact + " relationship: " + relationship);

    contactCurr.saveInBackground(
        new SaveCallback() {
          @Override
          public void done(ParseException e) {
            Log.d("addContacts method", "posted");
          }
        });

    // put flag initialized to false:
    contactCurr.put("Flag", false);

    contactsList.add(contactCurr);

    // TODO- determine how other contact will be posted
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
    AddUserFragment contactFragment = new AddUserFragment();
    contactFragment.setTargetFragment(MyContactsFragment.this, 1);
    contactFragment.show(fm, "contact fragment");
  }

  public void receiveText(String userText) {
    Log.d("ContactActivity", testuser);
    checkVerified(testuser);
    userExists(testuser);
    myHandler.postDelayed(runnable, 2000);


  }

  public void checkVerified(String verified) {
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.whereEqualTo("username", verified);
    query.countInBackground(
        new CountCallback() {
          @Override
          public void done(int count, ParseException e) {
            if (e == null) {
              if (count == 1) {
                isVerified = true;
                Log.d("TestActivities", String.valueOf(isVerified));
              } else if (count == 0) {
                isVerified = false;
                Log.d(TAG, "This username is not verified");
                Toast.makeText(getContext(), "This username is not verified", Toast.LENGTH_SHORT)
                    .show();
              }
            } else {
              Log.d("TestActivity", "retrieval failed");
            }
          }
        });
  }

  public void userExists(String userExist) {
    ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class);
    // see if this exists in the contacts list
    query.whereEqualTo("ContactName", userExist).whereEqualTo("Owner", user.getUsername());
    query.countInBackground(
        new CountCallback() {
          @Override
          public void done(int count, ParseException e) {
            if (e == null) {
              if (count == 0) {
                Log.d(TAG, "User has not been added to contacts yet");
                doesExist = false;
                return;
              } else if (count >= 1) {
                Log.d(TAG, "User has already been added");
                doesExist = true;
                Toast.makeText(getContext(), "User has already been added", Toast.LENGTH_SHORT)
                    .show();
              }
            }
          }
        });
  }

  @Override
  public void applyTexts(String userName, String relationship) {
    Log.d(TAG, userName.toString());
    Log.d(TAG, relationship);
    if (!relationship.equalsIgnoreCase("Relationships…")) {
      testuser = userName;
      testrelationship = relationship;
      receiveText(testuser);
    } else {
      Log.d(TAG, "Failed to add contact. Select Relationship");
      Toast.makeText(getContext(), "Failed to add contact. Select Relationship", Toast.LENGTH_SHORT)
          .show();
    }
  }

  // function for reading in contacts

  public void myContacts() {
    String username;
    user = ParseUser.getCurrentUser();
    username = user.getUsername();
    ParseQuery<Contacts> query =
        ParseQuery.getQuery(Contacts.class)
            .whereEqualTo("Owner", username)
            .orderByDescending("Ranking");
    Log.d("Fragment", user.getUsername());
    query.findInBackground(
         new FindCallback<Contacts>() {
          @Override
          public void done(List<Contacts> objects, ParseException e) {
            if (e == null) {

              if(objects.size() > 0) {
                int halfway = (objects.size() / 2);
                String halfwayRelationship = objects.get(halfway).getRelationship();
                //cancel the corrected flags --> will get more difficult on time with bigger contacts list
                for (int i = 0; i < objects.size() - 1; i++) {
                  String obRelationship = objects.get(i).getRelationship();
                  String nextRelationship = objects.get(i + 1).getRelationship();
                  if (objects.get(i).getFlag() == true && user.getInt(obRelationship) <= user.getInt(nextRelationship)) {
                    objects.get(i).setFlag(false);
                    objects.get(i).saveInBackground();
                  }
                }

                //set flags
                if (objects.size() > 1) {
                  for (int i = halfway; i < objects.size(); i++) {
                    String rel = objects.get(i).getRelationship();
                    String prevRel = objects.get(i - 1).getRelationship();
                    String firstRel = objects.get(0).getRelationship();
                    // we found a low ranking user that should be put on top
                    if ((user.getInt(rel) < user.getInt(prevRel)) || (user.getInt(rel) < user.getInt(halfwayRelationship)) || user.getInt(rel) < user.getInt(firstRel)) {
                      // might be a problem because we have to basically make a bunch of network calls
                      // and async delays
                      objects.get(i).setFlag(true);
                      // hopefully will save
                      objects.get(i).saveInBackground();
                      adapter.notifyDataSetChanged();
                    }
                  }
                }
              }
              for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getFlag() == true) {
                  // will add it to the front
                  contactsList.add(0, objects.get(i));
                } else {
                  contactsList.add(objects.get(i));
                }
                adapter.notifyItemInserted(contactsList.size() - 1);
              }

            } else {
              e.printStackTrace();
            }
          }
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "automatic refresh");
    contactsList.clear();
    adapter.notifyDataSetChanged();
    myContacts();
  }

  public void refreshContacts(){
    adapter.clear();
    myContacts();
    adapter.addAll(contactsList);
    swipeRefreshLayout.setRefreshing(false);

  }

  public int makeRanking(String relationship, ParseUser ownerUser) {
    int rankRelationship = ownerUser.getInt(relationship);
    int basepoints = 10;

    // look at the relationship ranking from the current User
    if (rankRelationship == 1) {
      return 3 * basepoints;
    } else if (rankRelationship == 2) {
      return (int) (2.5 * basepoints);
    } else if (rankRelationship == 3) {
      return 2 * basepoints;
    } else if (rankRelationship == 4) {
      return (int) 1.5 * basepoints;
    } else if (rankRelationship == 5) {
      return basepoints;
    } else {
      return 0;
    }
  }
}
