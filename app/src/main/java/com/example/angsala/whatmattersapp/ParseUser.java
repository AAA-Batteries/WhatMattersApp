package com.example.angsala.whatmattersapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;



@ParseClassName("User")
public class ParseUser extends ParseObject{
    public static final String USER_ID_KEY = "username";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }





}
