package com.example.project2_gameapp;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    public String id,  message, creator, creatorID;
    public Timestamp dateCreated;
    public ArrayList<String> likes;

    public Message() {
        this.id = "00000";
        this.message = "message";
        this.creator = "creator";
        this.creatorID = "id00000";
        this.dateCreated = Timestamp.now();
        this.likes = new ArrayList<>();
    }

    public Message(String id, String message, String creator, String creatorID, Timestamp dateCreated, ArrayList<String> likes) {
        this.id = id;
        this.message = message;
        this.creator = creator;
        this.creatorID = creatorID;
        this.dateCreated = dateCreated;
        this.likes = likes;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getCreator() {
        return creator;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String messageText) {
        this.message = messageText;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setLikes(ArrayList<String> numLikes) {
        this.likes = numLikes;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", messageText='" + message + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", creator='" + creator + '\'' +
                ", numLikes=" + likes +
                '}';
    }
}
