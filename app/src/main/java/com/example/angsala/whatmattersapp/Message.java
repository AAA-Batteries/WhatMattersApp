package com.example.angsala.whatmattersapp;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_SENT_KEY = "UserSent";
    public static final String USER_RECEIVED_KEY = "UserReceived";
    public static final String BODY_KEY = "body";

    public String getUserSent() {
        try {
            this.fetchIfNeeded();
            ParseUser userSent = (ParseUser) get(USER_SENT_KEY);
            return userSent.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserReceived() {
        try {
            this.fetchIfNeeded();
            ParseUser userReceived = (ParseUser) get(USER_RECEIVED_KEY);
            return userReceived.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserSent(String userId) {
        try {
            put(USER_SENT_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setUserReceived(String userId) {
        try {
            put(USER_RECEIVED_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}