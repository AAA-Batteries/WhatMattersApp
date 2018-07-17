package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ContactActivity extends AppCompatActivity {
static String TAG = "ContactActivity";
  User user;
    List<String> contacts;
    ContactsAdapter adapter;
   RecyclerView rvContacts;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contact);

//    Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
//    setSupportActionBar(myToolbar);
//    contacts = new ArrayList<>();
//    // contacts = user.getContacts();
//    adapter = new ContactsAdapter(contacts);
//    rvContacts = findViewById(R.id.rvContacts);
//    rvContacts.setLayoutManager(new LinearLayoutManager(this));
//    rvContacts.setAdapter(adapter);
    getmyContacts();



  }




//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.contact_items, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addContact:
//                DialogFragment dialogFragment = new ContactFragment();
//                dialogFragment.show(getSupportFragmentManager(), "contacts");
//                return true;
//
//           /* case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;*/
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//
//    }

    public void MyContacts() {

        final User.Query contactQuery = new User.Query();
        contactQuery.withUser();



        ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("User");
        Log.d("ContactActivity", "start of the the query");
        pQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseObject p = list.get(0);

                        if (p.getList("contacts") != null) {
                            List<String> listHolder = p.getList("contacts");

                            contacts.addAll(listHolder);
                            Log.d("ContactActivity", listHolder.toString());
                            adapter.notifyItemInserted(list.size() - 1);
                        } else {
                            contacts = null;
                            Log.d("ContactActivity", "hello");

                        }
                    }

                    Log.d("ContactActivity", contacts.toString());
                }
            }
        });

    }

 public void getmyContacts(){

       ParseQuery<ParseUser> query = ParseUser.getQuery();

       query.getInBackground("QsOMIfSlQf", new GetCallback<ParseUser>() {
           @Override
           public void done(ParseUser object, ParseException e) {

           }
       });
        //query.getInBackground("QsOMIfSlQf", new GetCallback<User>() {

//            @Override
//            public void done(User object, ParseException e) {
//                if (e == null){
//                    contacts = object.getContacts();
//                    Log.d("ContactsActivity", contacts.toString());
//                }
//                else{
//                    e.printStackTrace();
//                }
//            }
//        });
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
