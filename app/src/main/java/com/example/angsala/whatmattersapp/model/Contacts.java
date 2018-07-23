package com.example.angsala.whatmattersapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Contacts")
public class Contacts extends ParseObject {
    private static final String RANKING_KEY = "Ranking";
    private static final String RELATIONSHIP_KEY = "Relationship";
    private static final String OWNER_KEY = "Owner";
    private static final String CONTACT_NAME_KEY = "ContactName";

    public void setOwner(String ownerName) {
        put(OWNER_KEY, ownerName);
    }

    public void setRelationship(String relationship) {
        put(RELATIONSHIP_KEY, relationship);
    }

    public void setRanking(Integer ranking) {
        put(RANKING_KEY, ranking);
    }

    public void setContactName(String contactName) {
        put(CONTACT_NAME_KEY, contactName);
    }

    //when we want to display a particular contact
    public String getRelationship() {
        return getString(RELATIONSHIP_KEY);
    }

    public String getContactName() {
        return getString(CONTACT_NAME_KEY);
    }

    public Integer getRanking() {
        return getInt(RANKING_KEY);
    }

    public static int makeMessageRanking(ParseUser user, String relationship) {
        int priority = (int) user.get(relationship);
        switch (priority) {
            case 1:
                return 16;
            case 2:
                return 8;
            case 3:
                return 4;
            case 4:
                return 2;
            case 5:
                return 1;
        }
        return 0;
    }

}
