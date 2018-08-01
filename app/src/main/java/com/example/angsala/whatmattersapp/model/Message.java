package com.example.angsala.whatmattersapp.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("Message")
public class Message extends ParseObject implements Comparable<Message>, Serializable {
    private static final String USER_SENT_KEY = "UserSent";
    private static final String USER_RECEIVED_KEY = "UserReceived";
    private static final String BODY_KEY = "body";
    private static final String MESSAGE_RANKING_KEY = "MessageRanking";
    private static final String USER_RECEIVED_PRIORITY_KEY = "UserReceivedPriority";
    private static final String BUZZWORDS_DETECTED_KEY = "BuzzwordsDetected";

    public String getUserSent() {
        try {
            this.fetchIfNeeded();
            ParseUser userSent = (ParseUser) get(USER_SENT_KEY);
            return userSent.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserReceived() {
        try {
            this.fetchIfNeeded();
            ParseUser userReceived = (ParseUser) get(USER_RECEIVED_KEY);
            return userReceived.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBody() {
        try {
            this.fetchIfNeeded();
            return getString(BODY_KEY);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setUserSent(String userId) {
        try {
            put(USER_SENT_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setUserReceived(String userId) {
        try {
            put(USER_RECEIVED_KEY, ParseUser.getQuery().get(userId));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public ParseUser getParseUserSender() {
        try {
            this.fetchIfNeeded();
            ParseUser userSent = (ParseUser) get(USER_SENT_KEY);
            return userSent;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer getMessageRanking() {
        try {
            this.fetchIfNeeded();
            return getInt(MESSAGE_RANKING_KEY);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setMessageRanking(int messageRankPoints) {
        put(MESSAGE_RANKING_KEY, messageRankPoints);
    }

    //new getters and setters
    public void setUserReceivedPriority(int userReceivedPriority){
        put(USER_RECEIVED_PRIORITY_KEY, userReceivedPriority);
    }

    public Integer getUserReceivedPriority(){
        try{
            this.fetchIfNeeded();
            return getInt(USER_RECEIVED_PRIORITY_KEY);
        } catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }



    public String getRelativeTimeAgo() {
        long dateMillis = getCreatedAt().getTime();
        return DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }


    @Override
    public int compareTo(@NonNull Message mMessage) {
        int compareRanking = mMessage.getMessageRanking();
        //for ascending order-
        return compareRanking - this.getMessageRanking();
    }

}