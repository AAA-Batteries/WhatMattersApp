package com.example.angsala.whatmattersapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BottomNavigation extends AppCompatActivity implements NotificationFragment.NotificationFragmentListener {

    private BottomNavigationView bottomNavigationView;
    String testUser;
    TextView myBadge;
    int size;
    public final String TAG = "BottomNavigation";
    ParseUser user;
    final int POLL_INTERVAL = 5000;

    //this is where AS will test having a "push notification"- new branch
    Handler myHandler = new Handler();
    Runnable mRefreshNotifsRunnable =
            new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "entering the runnable 1");
                    ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class).whereEqualTo("UserReceived", user);
                    query.getFirstInBackground(new GetCallback<Notification>() {
                        @Override
                        public void done(Notification object, ParseException e) {
                            if (e == null) {
                                if (object.getReceived().size() > NotificationFragment.notificationList.size()) {
                                    createNotificationChannel();
                                    Log.d(TAG, "entering the runnable 2");
                                    sendNotification();
                                }

                            }
                            myHandler.postDelayed(mRefreshNotifsRunnable, POLL_INTERVAL);
                        }

                    });


                }
            };

    @Override
    public void upDate(int notifs) {
        size = notifs;
        Log.d("notifs", String.valueOf(size));

        myBadge.setText(String.valueOf(size));


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments_controller);
        final int[] btnMemory = new int[4];
        myHandler.postDelayed(mRefreshNotifsRunnable, POLL_INTERVAL);
        user = ParseUser.getCurrentUser();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        // define your fragments here
        final Fragment fragment1 = new NotificationFragment();
        final Fragment fragment2 = new ProfileFragment();
        final Fragment fragment3 = new MyContactsFragment();
        final Fragment fragment4 = new GroupFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.my_placeholder, fragment1).commit();
        // handle navigation selection
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_notifications:
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment1).commit();


                                myBadge.setVisibility(View.GONE);
                                size = 0;


                                return true;
                            case R.id.profile:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment2).commit();
                                if (size != 0) {
                                    myBadge.setVisibility(View.VISIBLE);
                                }


                                return true;
                            case R.id.contacts:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment3).commit();
                                if (size != 0) {
                                    myBadge.setVisibility(View.VISIBLE);
                                }


                                return true;
                            case R.id.groups:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment4).commit();
                                if (size != 0) {
                                    myBadge.setVisibility(View.VISIBLE);
                                }

                                return true;
                        }
                        return false;
                    }
                });


        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(0);
        final BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        View badge = LayoutInflater.from(this).inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
        itemView.addView(badge);
        myBadge = findViewById(R.id.myBadge);

        Log.d("notifsss", String.valueOf(size));


        // itemView.removeView(myBadge);


        Notification notification = new Notification();
        // int size = notification.getReceived().size();
        // Log.d("Notification", String.valueOf(size));


    }

    public void removeBadge(BottomNavigationView navigationView, int index) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        itemView.removeViewAt(0);
    }

    public void sendNotification() {
        Intent intent = new Intent(BottomNavigation.this, BottomNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BottomNavigation.this, NotificationFragment.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.face_icon_chat_transparent)
                .setContentTitle("New Notification")
                .setContentText("Check out your notifications!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0})
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //removes notif when tapped
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BottomNavigation.this);
        notificationManager.notify(001, mBuilder.build());
    }

        public void createNotificationChannel () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "notifs_name";
                String description = "notifs_description";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(NotificationFragment.NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(description);
                channel.setShowBadge(true);
                channel.canShowBadge();

                //register notification with the system
                NotificationManager notificationManager = BottomNavigation.this.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);


            }

        }



}
