package com.example.angsala.whatmattersapp.model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Notification")
public class Notification extends ParseObject {
    private static final String USER_RECEIVED_KEY = "UserReceived";
    private static final String RECEIVED_MESSAGES_KEY = "ReceivedMessages";

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

    public void setUserReceived(String userId) {
        try {
            put(USER_RECEIVED_KEY, ParseUser.getQuery().get(userId));
            this.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setReceived(ArrayList<Message> list) {
        put(RECEIVED_MESSAGES_KEY, list);
        this.saveInBackground();
    }

    public void addReceived(Message message) {
        ArrayList<Message> messages = getReceived();
        messages.add(message);
        this.setReceived(messages);
    }

    public void removeReceived(String senderId) {
        ArrayList<Message> messages = getReceived();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            String sender = m.getUserSent();
            if (sender.equals(senderId)) {
                messages.remove(i);
                i--;
            }
        }
        setReceived(messages);
    }

    public void removeSingular(String senderId, int position) {
        ArrayList<Message> messages = getReceived();
        messages.remove(position);
        setReceived(messages);
    }

}
