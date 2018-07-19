package com.example.angsala.whatmattersapp.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Notification")
public class Notification extends ParseObject {
    private static final String USER_SENT_KEY = "UserSent";
    private static final String USER_RECEIVED_KEY = "UserReceived";
    private static final String RECEIVED_MESSAGES_KEY = "ReceivedMessages";

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

    public ArrayList<Message> getReceived() {
        return (ArrayList<Message>) get(RECEIVED_MESSAGES_KEY);
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

    public void addReceived(Message message) {
       getReceived().add(message);
    }
}
