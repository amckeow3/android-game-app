package com.example.project2_gameapp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String id;
    String firstName;
    String lastName;
    String email;
    String city;
    String gender;
    ArrayList<Chatroom> userChatrooms = new ArrayList<>();

    public User() {
        this.id = "asdf";
        this.firstName = "firstName";
        this.lastName = "lastName";
        this.email = "email";
        this.city = "city";
        this.gender = "gender";
        this.userChatrooms = new ArrayList<>();
    }

    public User(String id, String firstName, String lastName, String email, String city, String gender, ArrayList<Chatroom> userChatrooms) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.gender = gender;
        this.userChatrooms = userChatrooms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<Chatroom> getUserChatrooms() {
        return userChatrooms;
    }

    public void setUserChatrooms(ArrayList<Chatroom> userChatrooms) {
        this.userChatrooms = userChatrooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", gender='" + gender + '\'' +
                ", userChatrooms=" + userChatrooms +
                '}';
    }
}
