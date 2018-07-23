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

    public void setOwner(String ownerName){
        put(OWNER_KEY,ownerName);
    }

    public void setRelationship(String relationship){
        put(RELATIONSHIP_KEY, relationship);
    }

    public void setRanking(Integer ranking){
        put(RANKING_KEY, ranking);
    }

    public void setContactName(String contactName){
        put(CONTACT_NAME_KEY, contactName);
    }

    //when we want to display a particular contact
    public String getRelationship(){
        return getString(RELATIONSHIP_KEY);
    }

    public String getContactName(){
        return getString(CONTACT_NAME_KEY);
    }

    public Integer getRanking(){
        return getInt(RANKING_KEY);
    }

    public static int makeMessageRanking(String relationship, ParseUser ownerUser){
        String relationship1 = ownerUser.getString("Relationship1");
        String relationship2 = ownerUser.getString("Relationship2");
        String relationship3 = ownerUser.getString("Relationship3");
        String relationship4 = ownerUser.getString("Relationship4");
        String relationship5 = ownerUser.getString("Relationship5");
        int basepoints = 10;

        //look at the relationship ranking from the current User
        if(relationship.equalsIgnoreCase(relationship1)){return 3*basepoints;}
        else if(relationship.equalsIgnoreCase(relationship2)){return (int)(2.5*basepoints);}
        else if (relationship.equalsIgnoreCase(relationship3)){return 2*basepoints;}
        else if (relationship.equalsIgnoreCase(relationship4)){return (int) 1.5*basepoints;}
        else if (relationship.equalsIgnoreCase(relationship5)){return basepoints;}
        else{return 0;}
    }

}
