package com.example.angsala.whatmattersapp.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseObject {

    private static final String CONTACTS = "contacts";
    private static final String KEY_USER = "username";
    private static final String KEY_IMAGE = "ProfileImage";

    public static String getUser() {
        return KEY_USER;
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setContacts(List<String> contacts) {
        put(CONTACTS, contacts);
    }

    public List<String> getContacts() {
        return getList(CONTACTS);
    }

    public static class Query extends ParseQuery<User> {

        public Query() {
            super(User.class);
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
//    public void addMessage(HashMap<String, String> hmap) {
//        ArrayList<Message> curr = getMessages();
//        if (curr == null) {
//            curr = new ArrayList<>();
//        }
//        curr.add(message);
//        this.put(CONTACTS, curr);
//    }



    public ParseFile getProfileImage() {
        return getParseFile(KEY_IMAGE);
    }
}
