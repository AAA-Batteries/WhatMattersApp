package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.parse.ParseQuery.getQuery;


public class NotificationFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    ParseUser user;
    public static List<Message> notificationList;
    public final String TAG = "NotificationFragment";
    NotificationAdapter adapter;
    RecyclerView rvNotifications;
    // Create a handler which can run code periodically
    private NotificationFragmentListener listener;
    public static String NOTIFICATION_CHANNEL ="notif_channel";

    static final int POLL_INTERVAL = 2000;
    TextView myBadge;
    int myNotifs;

    Handler myHandler = new Handler();
    Runnable mRefreshNotifsRunnable =
            new Runnable() {
                @Override
                public void run() {

                    fetchNotifications();


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
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //make toolbar
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar_notification);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        user = ParseUser.getCurrentUser();
        notificationList = new ArrayList<>();
        myBadge = view.findViewById(R.id.myBadge);

        adapter = new NotificationAdapter(notificationList);

        rvNotifications = (RecyclerView) getActivity().findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotifications.setAdapter(adapter);

        //adding the item touch helperI
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvNotifications);
      //  fetchNotifications();

    }
    //make tool bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clearNotifs:
                adapter.clear();
                fetchNotifications();
                Log.d(TAG, "I clicked on the refresh button");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationList.clear();
        adapter.notifyDataSetChanged();

        fetchNotifications();
        myNotifs = notificationList.size();

        //makeTestMessages();
        Log.d(TAG, "hello");
    }


    public void fetchNotifications() {
        //try to get the notifications of the current user
        String username = user.getUsername();
        String i = user.getObjectId();
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class).whereEqualTo("UserReceived", user);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Notification mnotifciation = objects.get(0);
                        notificationList.addAll(mnotifciation.getReceived());


                        //hopefully returns a size not null
                        Log.d(TAG, Integer.toString(notificationList.size()));

                        adapter.notifyItemInserted(notificationList.size() - 1);
                        sendBackResult();


                    }
                } else {
                    e.printStackTrace();
                }
                Collections.sort(notificationList);
                for (int i = 0; i < notificationList.size(); i++) {
                    Log.d(TAG, Integer.toString(notificationList.get(i).getMessageRanking()));
                }
            }
        });

    }

    @Override

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        String sentID = notificationList.get(position).getUserSent();
        adapter.removeItem(position);

        // delete the current user's notifications of messages received from the current chat's "recipient"
        ParseQuery<Notification> query = getQuery(Notification.class)
                .whereEqualTo("UserReceived", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Notification>() {
            @Override
            public void done(final Notification notif, ParseException e) {
                TextView usernameView = (TextView) getView().findViewById(R.id.tvNotificationUsername);
                String sender = usernameView.getText().toString();
                ParseQuery<ParseUser> query = getQuery(ParseUser.class).whereEqualTo("username", sender);
                query.getFirstInBackground(new GetCallback<ParseUser>() {
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            final String senderId = user.getObjectId();

                            notif.removeSingular(senderId, position);
                            notif.saveInBackground();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //ANGELA S TESTING MESSAGE CLASSIFICATION:
    public void makeTestMessages() {
        final Message mMessage1 = new Message();


        mMessage1.setBody("hello3");
        mMessage1.setUserSent(user.getObjectId());
        mMessage1.setUserReceived("soDouscmAG");
        mMessage1.setMessageRanking(500);

        mMessage1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "sent message 3");
                setNotif(mMessage1);

            }
        });


    }

    // update the notification object with newly received messages
    public void setNotif(final Message message) {
        try {
            ParseQuery<Notification> notifQuery = new ParseQuery<>(Notification.class)
                    .whereEqualTo("UserReceived", ParseUser.getQuery().get("soDouscmAG"));
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

    //end of testing code

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (NotificationFragmentListener) context;


    }

    public interface NotificationFragmentListener {

        void upDate(int notifs);
    }

    public void sendBackResult() {
        listener.upDate(notificationList.size());

    }


}
