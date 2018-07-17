package com.example.angsala.whatmattersapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Chat")
public class Chat extends ParseObject {
    public static final String USER_SENT_KEY = "UserSent";
    public static final String USER_RECEIVED_KEY = "UserReceived";
    public static final String MESSAGE_KEY = "Message"

    public String getUserSent() {
        return getString(USER_SENT_KEY);
    }

    public String getUserReceived() { return getString(USER_RECEIVED_KEY); }

    public Message getMessage() { return (Message) getParseObject(MESSAGE_KEY); }

    public void setUserSent(String userId) {
        put(USER_SENT_KEY, userId);
    }

    public void setUserReceived(String userId) { put(USER_RECEIVED_KEY, userId); }

    public void setMessage(Message message) { put(MESSAGE_KEY, message); }

}