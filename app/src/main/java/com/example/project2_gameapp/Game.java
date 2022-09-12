package com.example.project2_gameapp;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    User player1;
    User player2;
    Card topCard;
    ArrayList<Card> player1Hand;
    ArrayList<Card> player2Hand;
    HashMap<String, Object> moves;
    String currentTurn;//ID of user whose turn it is

}
