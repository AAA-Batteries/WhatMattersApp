package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Notification;

public class BottomNavigation extends AppCompatActivity implements NotificationFragment.NotificationFragmentListener {

    private BottomNavigationView bottomNavigationView;
    String testUser;
    TextView myBadge;
    int size;

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
                                removeBadge(bottomNavigationView, 0);
                                return true;
                            case R.id.profile:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment2).commit();

                                return true;
                            case R.id.contacts:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment3).commit();
                                return true;
                            case R.id.groups:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.my_placeholder, fragment4).commit();
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


        Notification notification = new Notification();
       // int size = notification.getReceived().size();
       // Log.d("Notification", String.valueOf(size));


    }

    public void removeBadge(BottomNavigationView navigationView, int index) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeViewAt(itemView.getChildCount() - 1);
    }




}
