package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BottomNavigation extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    String testUser;

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

    }








}
