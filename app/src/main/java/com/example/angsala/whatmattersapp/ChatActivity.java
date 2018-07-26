package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.Chat;
import com.example.angsala.whatmattersapp.model.Contacts;
import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.example.angsala.whatmattersapp.model.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    ImageView btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    String recipientId;
    String currentId;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 500; // milliseconds
    Handler myHandler = new Handler(); // android.os.Handler
    Runnable mRefreshMessagesRunnable =
            new Runnable() {
                @Override
                public void run() {

                    refreshMessages();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive recipient user reference from the contact activity
        setRecipient(getIntent().getStringExtra("Recipient"));
        // set current user reference for future use
        currentId = ParseUser.getCurrentUser().getObjectId();

        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        // This query can even be more granular (i.e. only refresh if message was
        // sent/received by current user)
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class).whereEqualTo("UserSent", currentId);
        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class).whereEqualTo("UserReceived", currentId);

        ArrayList<ParseQuery<Message>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<Message> parseQuery = ParseQuery.or(queries);

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events
        subscriptionHandling.handleEvent(
                SubscriptionHandling.Event.CREATE,
                new SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        mMessages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // delete the current user's notifications of messages received from the current chat's "recipient"
                                        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class)
                                                .whereEqualTo("UserReceived", currentId);
                                        query.getFirstInBackground(new GetCallback<Notification>() {
                                            @Override
                                            public void done(Notification object, ParseException e) {
                                                ArrayList<Message> messages = (ArrayList) object.get("ReceivedMessages");
                                                for (int i = 0; i < messages.size(); i++) {
                                                    Message m = messages.get(i);
                                                    String sender = m.getUserSent();
                                                    if (sender.equals(recipientId)) {
                                                        messages.remove(i);
                                                        i--;
                                                    }
                                                }
                                                object.saveInBackground();
                                            }
                                        });

                                        // refresh the chat screen
                                        mAdapter.notifyDataSetChanged();
                                        rvChat.scrollToPosition(0);
                                    }
                                });
                    }
                });

        setContentView(R.layout.activity_chat);
        Toolbar mytoolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setTitle("");
        mytoolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_18dp);
        mytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }


    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Anonymous login failed: ", e);
                        } else {
                            startWithCurrentUser();
                        }
                    }
                });
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String data = etMessage.getText().toString();

                        // Using new `Message` Parse-backed model now
                        Message message = new Message();
                        message.setBody(data);
                        message.setUserSent(ParseUser.getCurrentUser().getObjectId());
                        message.setUserReceived(recipientId);

                        final Message tempMessage = message;
                        message.saveInBackground(
                                new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            // check Parse server for existing chat object, else create a new chat object
                                            // between current and recipient users
                                            final ParseQuery<ParseObject> finalQuery = ParseQuery.or(orQuery());
                                            finalQuery.findInBackground(
                                                    new FindCallback<ParseObject>() {
                                                        public void done(List<ParseObject> object, ParseException e) {
                                                            // query object exists with current user as user1, recipient user as
                                                            // user2
                                                            if (!object.isEmpty()) {
                                                                // query object exists
                                                                // add new message to the chat log
                                                                Chat chat = (Chat) object.get(0);
                                                                chat.addMessage(tempMessage);
                                                                chat.saveInBackground();
                                                            } else if (recipientId != null) {
                                                                // query object between the two users did not exist on the
                                                                // parse backend, create new chat object
                                                                Chat chat = new Chat();
                                                                chat.setUser1(currentId);
                                                                chat.setUser2(recipientId);
                                                                chat.addMessage(tempMessage);

                                                                chat.saveInBackground(
                                                                        new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    Toast.makeText(
                                                                                            ChatActivity.this,
                                                                                            "Successfully started a new chat!",
                                                                                            Toast.LENGTH_SHORT)
                                                                                            .show();
                                                                                } else {
                                                                                    Log.e(TAG, "Failed to send", e);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                            // reload the screen and notify user of successful new message creation
                                                            refreshMessages();
                                                            mAdapter.notifyDataSetChanged();
                                                            rvChat.scrollToPosition(0);
                                                            Toast.makeText(
                                                                    ChatActivity.this,
                                                                    "Successfully sent message!",
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    });
                                            // after user sends a message, update the recipient contact ranking based on priority category
                                            try {
                                                ParseQuery<Contacts> contactsQuery = new ParseQuery<Contacts>(Contacts.class)
                                                        .whereEqualTo("Owner", ParseUser.getCurrentUser().getUsername())
                                                        .whereEqualTo("ContactName", ParseUser.getQuery().get(recipientId).getUsername());
                                                contactsQuery.getFirstInBackground(
                                                        new GetCallback<Contacts>() {
                                                            public void done(Contacts object, ParseException e) {
                                                                Contacts contact = (Contacts) object;
                                                                ParseUser user = ParseUser.getCurrentUser();
                                                                int addPoints = Contacts.makeMessageRanking(user, contact.getRelationship());
                                                                contact.setRanking(contact.getRanking() + addPoints);
                                                                contact.saveInBackground();
                                                            }
                                                        });
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                            // add the message to notifications
                                            setNotif(tempMessage);
                                        } else {
                                            Log.e(TAG, "Failed to save message", e);
                                        }
                                    }
                                });
                        etMessage.setText(null);
                    }
                });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        ParseQuery<ParseObject> query = ParseQuery.or(orQuery());

        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.getFirstInBackground(
                new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null && object != null) {
                            Chat chat = (Chat) object;
                            mMessages.clear();
                            for (Message m : chat.getMessages()) {
                                mMessages.add(0, m);
                            }
                            mAdapter.notifyDataSetChanged(); // update adapter
                            myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
                            // Scroll to the bottom of the list on initial load
                            if (mFirstLoad) {
                                rvChat.scrollToPosition(0);
                                mFirstLoad = false;
                            }
                        } else if (e != null) {
                            Log.e("message", "Error Loading Messages" + e);
                        }
                    }
                });
    }

    // using the name of the given recipient, find that user's object id and assign it to recipientId variable
    public void setRecipient(final String recipientName) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", recipientName);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                ParseUser recipient = object;
                if (e == null && object != null) {
                    recipientId = recipient.getObjectId();
                } else {
                    Log.d("User cannot be found ", recipientName);
                    e.printStackTrace();
                }
            }
        });
    }

    // create compound query, the or result of two queries, that is used to determine the chat corresponding
    // to current user and recipient user
    public ArrayList<ParseQuery<ParseObject>> orQuery() {
        // create queries to check for existing chat between the two specified users,
        // current and recipient
        // query1 and query2 accounts for the possible user configurations between
        // user1 and user2, current and recipient users
        ParseQuery<ParseObject> query1 =
                new ParseQuery<ParseObject>("Chat").whereDoesNotExist("User1");
        ParseQuery<ParseObject> query2 =
                new ParseQuery<ParseObject>("Chat").whereDoesNotExist("User1");
        try {
            query1 =
                    ParseQuery.getQuery("Chat")
                            .whereEqualTo("User1", ParseUser.getQuery().get(currentId))
                            .whereEqualTo("User2", ParseUser.getQuery().get(recipientId));
            query2 =
                    ParseQuery.getQuery("Chat")
                            .whereEqualTo("User1", ParseUser.getQuery().get(recipientId))
                            .whereEqualTo("User2", ParseUser.getQuery().get(currentId));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        Log.d("ChatActivity", "current: " + currentId + " recipientId" + recipientId);
        ArrayList<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        return queries;
    }

    // update the notification object with newly received messages
    public void setNotif(final Message message) {
        try {
            ParseQuery<Notification> notifQuery = new ParseQuery<Notification>(Notification.class)
                    .whereEqualTo("UserReceived", ParseUser.getQuery().get(currentId));
            notifQuery.getFirstInBackground(
                    new GetCallback<Notification>() {
                        public void done(Notification object1, ParseException e) {
                            Notification notif = object1;
                            if (e == null && notif != null) {
                                notif.addReceived(message);
                                notif.saveInBackground();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}