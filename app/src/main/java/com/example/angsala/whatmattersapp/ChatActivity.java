package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.User;
import com.parse.FindCallback;
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
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    Button btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    static String recipientId = "PWoOLhNXGX";
    static String currentId = ParseUser.getCurrentUser().getObjectId();

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // TODO

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        mMessages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                rvChat.scrollToPosition(0);
                            }
                        });
                    }
                });

        setContentView(R.layout.activity_chat);
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
        ParseAnonymousUtils.logIn(new LogInCallback() {
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
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();

                // Using new `Message` Parse-backed model now
                final Message message = new Message();
                message.setBody(data);
                message.setUserSent(ParseUser.getCurrentUser().getObjectId());
                message.setUserReceived(recipientId);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            // check for existing chat between the two specified users, current and recipient
                            // create chat if non-existent
                            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                            queries.add(ParseQuery.getQuery("Chat").whereEqualTo("User1", currentId).whereEqualTo("User2", recipientId));
                            queries.add(ParseQuery.getQuery("Chat").whereEqualTo("User2", currentId).whereEqualTo("User1", recipientId));

                            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

                            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> objects, ParseException ex) {
                                    if(ex != null) {
                                        final int statusCode = ex.getCode();
                                        if (statusCode == ParseException.OBJECT_NOT_FOUND) {
                                            // Object did not exist on the parse backend, create new chat object
                                            Chat chat = new Chat();
                                            chat.setUser1(currentId);
                                            chat.setUser2(recipientId);
                                            chat.addMessage(message);

                                            chat.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e == null) {
                                                        Toast.makeText(ChatActivity.this, "Successfully started a new chat!",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.e(TAG, "Failed to send", e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        // No exception means the object exists
                                    }
                                }
                            });
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });

                // check for existing chat between the two specified users, current and recipient
                // create chat if non-existent
                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(ParseQuery.getQuery("Chat").whereEqualTo("User1", currentId).whereEqualTo("User2", recipientId));
                queries.add(ParseQuery.getQuery("Chat").whereEqualTo("User2", currentId).whereEqualTo("User1", recipientId));

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

                mainQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException ex) {
                        if(ex != null) {
                            final int statusCode = ex.getCode();
                            if (statusCode == ParseException.OBJECT_NOT_FOUND) {
                                // Object did not exist on the parse backend, create new chat object
                                Chat chat = new Chat();
                                chat.setUser1(currentId);
                                chat.setUser2(recipientId);
                                chat.addMessage(message);

                                chat.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            Toast.makeText(ChatActivity.this, "Successfully started a new chat!",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e(TAG, "Failed to send", e);
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            // No exception means the object exists
                        }
                    }
                });

                etMessage.setText(null);
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct queries to execute, one for current user and one for the recipient user
        ParseQuery<Message> queryCurrent = ParseQuery.getQuery(Message.class);
        ParseQuery<Message> queryRecipient = ParseQuery.getQuery(Message.class);

        // limit messages loaded to only those between the current user and set recipient
        queryCurrent.whereEqualTo("UserSent", currentId);
        queryCurrent.whereEqualTo("UserReceived", recipientId);

        queryRecipient.whereEqualTo("UserSent", recipientId);
        queryRecipient.whereEqualTo("UserReceived", currentId);

        // list all queries condition
        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();
        queries.add(queryCurrent);
        queries.add(queryRecipient);

        // Compose the OR clause
        ParseQuery<Message> innerQuery = ParseQuery.or(queries);

        // Configure limit and sort order for combined query
        innerQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        innerQuery.orderByAscending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        innerQuery.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    public void setRecipient(String givenRecipient) {
        recipientId = givenRecipient;
    }

}