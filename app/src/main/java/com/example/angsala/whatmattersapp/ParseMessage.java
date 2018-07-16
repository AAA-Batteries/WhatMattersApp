package com.example.angsala.whatmattersapp;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class ParseMessage extends ParseObject {
    public static final String USER_ID_KEY = "UserSent";
    public static final String BODY_KEY = "text";
    public static final String CONTACT_ARRAY = "contacts";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

}
