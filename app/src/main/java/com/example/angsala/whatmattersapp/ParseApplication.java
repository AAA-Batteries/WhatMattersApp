package com.example.angsala.whatmattersapp;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration =
                new Parse.Configuration.Builder(this)
                        .applicationId("what-matterz")
                        .clientKey("3Amatterz") // password
                        .server("http://what-matterz.herokuapp.com/parse") // server
                        .build();

        Parse.initialize(configuration);


    }


}
