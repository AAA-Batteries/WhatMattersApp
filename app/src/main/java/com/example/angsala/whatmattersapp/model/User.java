package com.example.angsala.whatmattersapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseObject {

  private static final String CONTACTS = "contacts";
  private static final String KEY_USER = "username";

  public User() {}

  public static String getKeyUser() {
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
}
