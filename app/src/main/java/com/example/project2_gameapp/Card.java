package com.example.project2_gameapp;

public class Card {
    String value;//0-9, skip, draw 4
    String color;//red, yellow, green, blue, black for draw 4

    public Card() {
        //generate card of random value and color here?
    }

    public Card(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
