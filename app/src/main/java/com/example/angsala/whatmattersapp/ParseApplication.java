package com.example.angsala.whatmattersapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Message.class);

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax

        final Parse.Configuration configuration =
                new Parse.Configuration.Builder(this)
                        .applicationId("what-matterz")
                        .clientKey("3Amatterz") // password
                        .clientBuilder(builder)
                        .server("http://what-matterz.herokuapp.com/parse") // server
                        .build();

        Parse.initialize(configuration);
    }

}
