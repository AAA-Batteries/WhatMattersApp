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

import com.example.angsala.whatmattersapp.model.BuzzWords;
import com.example.angsala.whatmattersapp.model.Chat;
import com.example.angsala.whatmattersapp.model.Contacts;
import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.parse.ParseQuery.getQuery;

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
    String recipUsername;

    Chat chat;
    NewChatAdapter adapter;


    //new queries
    String contactRelationship;
    int contactPriority;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler(); // android.os.Handler
    Runnable mRefreshMessagesRunnable =
            new Runnable() {
                @Override
                public void run() {
                    ParseQuery<Chat> chatQuery = ParseQuery.or(orQuery());
                    chatQuery.getFirstInBackground(new GetCallback<Chat>() {
                        @Override
                        public void done(Chat object, ParseException e) {
                            if (e == null) {
                                chat = object;
                                if (mMessages.size() < chat.getMessages().size()) {
                                    refreshMessages();
                                }
                            }
                        }
                    });
                    myHandler.postDelayed(this, POLL_INTERVAL);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirstLoad = true;

        // receive recipient user reference from the contact activity
        // also, checks for existing chat with recipient user and creates one if one is not found
        setRecipient(getIntent().getStringExtra("Recipient"));
        // set current user reference for future use
        recipUsername = getIntent().getStringExtra("Recipient");
        currentId = ParseUser.getCurrentUser().getObjectId();

        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        // This query can even be more granular (i.e. only refresh if message was
        // sent/received by current user)
        ParseQuery<Message> query1 = getQuery(Message.class).whereEqualTo("UserSent", currentId);
        ParseQuery<Message> query2 = getQuery(Message.class).whereEqualTo("UserReceived", currentId);

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
                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // refresh the chat screen
                                        refreshMessages();
                                        mAdapter.notifyDataSetChanged();
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
                Log.d(TAG, "I clicked the exit button");
                onBackPressed();
            }
        });

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
        final String userId = ParseUser.getCurrentUser().getObjectId();

        //where we will fill the adapter
        readItems();
        adapter = new NewChatAdapter(this, mMessages, userId);
        rvChat.setAdapter(adapter);

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);


        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String data = etMessage.getText().toString();
                        final BuzzWords buzz = new BuzzWords(ChatActivity.this);
                        // Using new `Message` Parse-backed model now
                        final Message message = new Message();
                        message.setBody(data);
                        message.setUserSent(ParseUser.getCurrentUser().getObjectId());
                        message.setUserReceived(recipientId);

                        try {
                            ParseUser recipUser = ParseUser.getQuery().get(recipientId);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        //start of new queries

                        ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class).whereEqualTo("Owner", recipUsername).whereEqualTo("ContactName", ParseUser.getCurrentUser().getUsername());
                        query.getFirstInBackground(new GetCallback<Contacts>() {
                            @Override
                            public void done(Contacts object, ParseException e) {
                                if (e == null) {
                                    contactRelationship = object.getRelationship();
                                    ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", recipUsername);
                                    query1.getFirstInBackground(new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser object, ParseException e) {
                                            contactPriority = object.getInt(contactRelationship);
                                            //set the priority between the recipient and sender
                                            message.setUserReceivedPriority(contactPriority);
                                            int score = buzz.caseBuzzWord(data, contactPriority);
                                            //access public member field of BuzzWords instance
                                            message.setBuzzwordsDetected(buzz.hasBuzzwords);
                                            message.setMessageRanking(score);

                                            final Message tempMessage = message;
                                            message.saveInBackground(
                                                    new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                // add new message to chat object between current and recipient users
                                                                chat.addMessage(tempMessage);
                                                                chat.saveInBackground();

                                                                // reload the screen and notify user of successful new message creation
                                                                refreshMessages();

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


                                        }


                                    });
                                }

                            }
                        });

                        //end of new queries
                        etMessage.setText(null);
                    }
                });
        rvChat.scrollToPosition(-100);
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        if (chat != null) {
            mMessages.clear();
            mMessages.addAll(chat.getMessages());
            //where we will write the items to update
            writeItems();
            adapter.notifyDataSetChanged(); // update adapter
            // Scroll to the bottom of the list on ALL loads
            rvChat.scrollToPosition(-100);
            if (mFirstLoad) {
                mFirstLoad = false;
            }
        } else {
            Log.d("Initializing: ", "chat is null");
        }
    }

    // using the name of the given recipient, find that user's object id and assign it to recipientId variable
    // checks that there is a chat with said user, otherwise create one
    public void setRecipient(final String recipientName) {
        ParseQuery<ParseUser> query = getQuery(ParseUser.class).whereEqualTo("username", recipientName);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                ParseUser recipient = object;
                if (e == null) {
                    recipientId = recipient.getObjectId();

                    // find or create the chat object for current and recipient users
                    ParseQuery<Chat> chatQuery = ParseQuery.or(orQuery());

                    // Execute query to fetch all messages from Parse asynchronously
                    // This is equivalent to a SELECT query with SQL
                    chatQuery.getFirstInBackground(
                            new GetCallback<Chat>() {
                                public void done(Chat object, ParseException e) {
                                    if (e == null) {
                                        if (object != null) {
                                            chat = (Chat) object;
                                            // populate screen with messages
                                            refreshMessages();
                                            makeFlagToast();
                                        } else {
                                            // query object between the two users did not exist on the
                                            // parse backend, create new chat object
                                            chat = new Chat();
                                            chat.setUser1(currentId);
                                            chat.setUser2(recipientId);
                                            chat.setMessages(new ArrayList<Message>());
                                            chat.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(
                                                                ChatActivity.this,
                                                                "Successfully started a new chat!",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                        makeFlagToast();
                                                    } else {
                                                        Log.e(TAG, "Failed to start chat", e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });

                    // delete the current user's notifications of messages received from the current chat's "recipient"
                    ParseQuery<Notification> query = getQuery(Notification.class)
                            .whereEqualTo("UserReceived", ParseUser.getCurrentUser());
                    query.getFirstInBackground(new GetCallback<Notification>() {
                        @Override
                        public void done(Notification object, ParseException e) {
                            object.removeReceived(recipientId);
                            object.saveInBackground();
                        }
                    });

                    // User login
                    if (ParseUser.getCurrentUser() != null) { // start with existing user
                        startWithCurrentUser();
                    } else { // If not logged in, login as a new anonymous user
                        login();
                    }
                } else {
                    Log.d("User cannot be found ", recipientName);
                    e.printStackTrace();
                }
            }
        });
    }

    // create compound query, the or result of two queries, that is used to determine the chat corresponding
    // to current user and recipient user
    public ArrayList<ParseQuery<Chat>> orQuery() {
        // create queries to check for existing chat between the two specified users,
        // current and recipient
        // query1 and query2 accounts for the possible user configurations between
        // user1 and user2, current and recipient users
        ParseUser current;
        ParseUser recipient;
        try {
            current = ParseUser.getQuery().get(currentId);
            recipient = ParseUser.getQuery().get(recipientId);
        } catch (ParseException e) {
            e.printStackTrace();
            current = ParseUser.getCurrentUser();
            recipient = null;
        }
        ParseQuery<Chat> query1 =
                getQuery(Chat.class)
                        .whereEqualTo("User1", current)
                        .whereEqualTo("User2", recipient);
        ParseQuery<Chat> query2 =
                getQuery(Chat.class)
                        .whereEqualTo("User1", recipient)
                        .whereEqualTo("User2", current);

        Log.d("ChatActivity", "current: " + currentId + " recipientId: " + recipientId);
        ArrayList<ParseQuery<Chat>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        return queries;
    }

    // update the notification object with newly received messages
    public void setNotif(final Message message) {
        try {
            ParseQuery<Notification> notifQuery = new ParseQuery<>(Notification.class)
                    .whereEqualTo("UserReceived", ParseUser.getQuery().get(recipientId));
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

    // returns the file in which the data is stored
    private File getDataFile() {
        String fileName;
        if (currentId.compareTo(recipientId) <= 0) {
            fileName = currentId + recipientId + ".txt";
        } else {
            fileName = recipientId + currentId + ".txt";
        }
        return new File(getFilesDir(), fileName);
    }

    // read the items from the file system
    private void readItems() {
        try {
            ObjectInputStream messageIO;
            if (currentId.compareTo(recipientId) <= 0) {
                messageIO = new ObjectInputStream(new FileInputStream(currentId + recipientId));
            } else {
                messageIO = new ObjectInputStream(new FileInputStream(recipientId + currentId));
            }
            // create the array using the content in the file
            try {
                mMessages = (ArrayList) messageIO.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
            // just load an empty list
            mMessages = new ArrayList<>();
        }
    }

    // write the items to the filesystem
    private void writeItems() {
        try {
            // save the item list as a line-delimited text file
            ObjectOutputStream messageIO;
            if (currentId.compareTo(recipientId) <= 0) {
                messageIO = new ObjectOutputStream(new FileOutputStream(currentId + recipientId));
            } else {
                messageIO = new ObjectOutputStream(new FileOutputStream(recipientId + currentId));
            }
            messageIO.writeObject(mMessages);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }

    // Check if the recipient contact is flagged for the current user, and display a toast if it is
    private void makeFlagToast() {
        try {
            ParseQuery<Contacts> contactQuery = getQuery(Contacts.class)
                    .whereEqualTo("Owner", ParseUser.getQuery().get(currentId).getUsername())
                    .whereEqualTo("ContactName", ParseUser.getQuery().get(recipientId).getUsername());
            contactQuery.getFirstInBackground(new GetCallback<Contacts>() {
                @Override
                public void done(Contacts object, ParseException e) {
                    if (e == null) {
                        if (object.getFlag()) {
                            try {
                                String message = ParseUser.getQuery().get(recipientId).getUsername()
                                        + " is a member of " + object.getRelationship() + ".";
                                if (chat.getMessages().isEmpty()) {
                                    message += "\nThey should be a prioritized contact!\nStart chatting with them now!";
                                } else {
                                    message += "\nYou don't chat enough with them.\nLet's catch up!";
                                }
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            } catch (ParseException e1) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}