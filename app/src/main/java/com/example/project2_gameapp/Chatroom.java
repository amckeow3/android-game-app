package com.example.project2_gameapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Chatroom implements Serializable {
    String name;
    String id;
    User user;
    ArrayList<User> members = new ArrayList<>();

    public Chatroom() {

    }

    public Chatroom(String name, String id, ArrayList<User> members) {
        this.name = name;
        this.id = id;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", members=" + members +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}