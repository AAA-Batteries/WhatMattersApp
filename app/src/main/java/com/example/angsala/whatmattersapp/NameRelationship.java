package com.example.angsala.whatmattersapp;

public class NameRelationship {

    public String name;
    public String Relationship;

    public static NameRelationship makeRelation(String n, String r){
        NameRelationship nameRelationship = new NameRelationship();
        nameRelationship.name = n;
        nameRelationship.Relationship = r;
        return nameRelationship;
    }

    public String getName(){
        return name;
    }

    public String getRelationship(){
        return Relationship;
    }


}
