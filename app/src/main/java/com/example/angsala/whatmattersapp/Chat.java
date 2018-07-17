package com.example.angsala.whatmattersapp;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Chat")
public class Chat extends ParseObject {
    public static final String USER_1_KEY = "User1";
    public static final String USER_2_KEY = "User2";
    public static final String MESSAGE_KEY = "Messages";

    public String getUser1() { return getString(USER_1_KEY); }

    public String getUser2() { return getString(USER_2_KEY); }

    public Message[] getMessages() { return (Message[]) get(MESSAGE_KEY); }

    public void setUser1(String userId) {
        try {
            put(USER_1_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setUser2(String userId) {
        try {
            put(USER_2_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(Message message) {
        Object[] curr = getMessages();
        Object[] messages;
        if (curr == null) {
            messages = new Message[1];
            messages[0] = message;
        } else {
            messages = new Message[curr.length + 1];
            for (int index = 0; index < curr.length; index++) {
                messages[index] = curr[index];
            }
            messages[messages.length - 1] = message;
        }
        add(MESSAGE_KEY, message.getObjectId());
    }

}