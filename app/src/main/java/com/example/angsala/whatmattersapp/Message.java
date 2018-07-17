package com.example.angsala.whatmattersapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_SENT_KEY = "UserSent";
    public static final String USER_RECEIVED_KEY = "UserReceived";
    public static final String BODY_KEY = "body";

    public String getUserSent() {
        return getString(USER_SENT_KEY);
    }

    public String getUserReceived() { return getString(USER_RECEIVED_KEY); }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserSent(String userId) {
        put(USER_SENT_KEY, userId);
    }

    public void setUserReceived(String userId) { put(USER_RECEIVED_KEY, userId); }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}