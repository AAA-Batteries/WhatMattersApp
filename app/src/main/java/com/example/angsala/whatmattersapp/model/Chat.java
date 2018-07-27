package com.example.angsala.whatmattersapp.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Chat")
public class Chat extends ParseObject {
    private static final String USER_1_KEY = "User1";
    private static final String USER_2_KEY = "User2";
    private static final String MESSAGE_KEY = "Messages";

    public String getUser1() {
        ParseUser user1 = (ParseUser) get(USER_1_KEY);
        return user1.getObjectId();
    }

    public String getUser2() {
        ParseUser user2 = (ParseUser) get(USER_2_KEY);
        return user2.getObjectId();
    }

    public ArrayList<Message> getMessages() {
        return (ArrayList<Message>) get(MESSAGE_KEY);
    }

    public void setUser1(String userId) {
        try {
            put(USER_1_KEY, ParseUser.getQuery().get(userId));
            this.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setUser2(String userId) {
        try {
            put(USER_2_KEY, ParseUser.getQuery().get(userId));
            this.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setMessages(ArrayList list) {
        put(MESSAGE_KEY, list);
        this.saveInBackground();
    }

    public void addMessage(Message message) {
        ArrayList<Message> curr = getMessages();
        if (curr == null) {
            curr = new ArrayList<>();
        }
        curr.add(message);
        this.put(MESSAGE_KEY, curr);
        this.saveInBackground();
    }
}