package com.example.angsala.whatmattersapp.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class BuzzWords {
    ParseUser user1;
    Context context;
    ArrayList<String> buzzwords;
    String relationship;
    int priority;
    int buzzword_count;

    public BuzzWords(Context context) {
        this.context = context;
    }

    //this is what we will interface with
    public int Buzzer(final ParseUser user, String body) {

        buzzwords = new ArrayList<>();
        buzzwords.addAll(Arrays.asList(context.getResources().getStringArray(R.array.buzz_words)));
        user1 = ParseUser.getCurrentUser();
        String test1 = user.getUsername();
        String test2 = user1.getUsername();
        ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class).whereEqualTo("Owner", user).whereEqualTo("ContactName", user1);
        query.getFirstInBackground(new GetCallback<Contacts>() {
            @Override
            public void done(Contacts object, ParseException e) {
                if (e == null) {
                    relationship = object.getRelationship();
                    ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", user.getUsername());
                    query1.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {
                            priority = object.getInt(relationship);

                        }


                    });
                }

            }
        });


//        ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", user.getUsername());
//        query1.getFirstInBackground(new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser object, ParseException e) {
//                priority = object.getInt(relationship);
//
//            }
//
//
//        });


        return caseBuzzWord(body, myPriorities(priority));

    }

    public int myPriorities(int priorities) {
        if (priorities == 1) {
            return 25;
        } else if (priorities == 2) {
            return 20;
        } else if (priorities == 3) {
            return 15;
        } else if (priorities == 4) {
            return 10;
        } else if (priorities == 5) {
            return 5;
        }


        return 0;

    }



    public int caseBuzzWord(String myBody, int priorityy) {

        buzzwords = new ArrayList<>();
        buzzwords.addAll(Arrays.asList(context.getResources().getStringArray(R.array.buzz_words)));

        Log.d("MyArray", buzzwords.toString());
        Toast.makeText(context, "it works", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < buzzwords.size(); i++) {
            if (myBody.equalsIgnoreCase(buzzwords.get(i))) {
                buzzword_count++;
            }

        }
        return (buzzword_count * 10 + myPriorities(priorityy));


    }

}
