package com.example.angsala.whatmattersapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Chat")
public class Chat extends ParseObject {
    public static final String USER_1_KEY = "User1";
    public static final String USER_2_KEY = "User2";
    public static final String MESSAGE_KEY = "Messages";

    public Chat() {
        Message[] messages = new Message[1];
        put(MESSAGE_KEY, messages);
    }

    public String getUser1() { return getString(USER_1_KEY); }

    public String getUser2() { return getString(USER_2_KEY); }

    public Message[] getMessages() { return (Message[]) get(MESSAGE_KEY); }

    public void setUser1(String userId) {
        put(USER_1_KEY, userId);
    }

    public void setUser2(String userId) { put(USER_2_KEY, userId); }

    public void addMessage(Message message) {
        Message[] curr = getMessages();
        Message[] messages = new Message[curr.length + 1];
        for (int index = 0; index < curr.length; index++) {
            messages[index] = curr[index];
        }
        messages[messages.length - 1] = message;
        put(MESSAGE_KEY, messages);
    }

}