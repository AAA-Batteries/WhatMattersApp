package com.example.angsala.whatmattersapp.model;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    private static final String USER_SENT_KEY = "UserSent";
    private static final String USER_RECEIVED_KEY = "UserReceived";
    private static final String BODY_KEY = "body";

    public ParseUser getUserSent() {
        try {
            this.fetchIfNeeded();
            ParseUser userSent = (ParseUser) get(USER_SENT_KEY);
            return userSent;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ParseUser getUserReceived() {
        try {
            this.fetchIfNeeded();
            ParseUser userReceived = (ParseUser) get(USER_RECEIVED_KEY);
            return userReceived;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserSent(ParseUser user) {
        put(USER_SENT_KEY, user);
    }

    public void setUserReceived(ParseUser user) {
        put(USER_RECEIVED_KEY, user);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}