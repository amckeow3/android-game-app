package com.example.project2_gameapp;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    String gameTitle, gameID;
    User player1;
    User player2;
    Card topCard;
    ArrayList<Card> player1Hand;
    ArrayList<Card> player2Hand;
    HashMap<String, Object> moves;
    String currentTurn;
    boolean gameFinished;

    public Game() {
    }

    public Game(String gameTitle, String gameID, User player1, User player2, Card topCard, ArrayList<Card> player1Hand,
                ArrayList<Card> player2Hand, HashMap<String, Object> moves, String currentTurn, boolean gameFinished) {
        this.gameTitle = gameTitle;
        this.gameID = gameID;
        this.player1 = player1;
        this.player2 = player2;
        this.topCard = topCard;
        this.player1Hand = player1Hand;
        this.player2Hand = player2Hand;
        this.moves = moves;
        this.currentTurn = currentTurn;
        this.gameFinished = gameFinished;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public Card getTopCard() {
        return topCard;
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
    }

    public ArrayList<Card> getPlayer1Hand() {
        return player1Hand;
    }

    public void setPlayer1Hand(ArrayList<Card> player1Hand) {
        this.player1Hand = player1Hand;
    }

    public ArrayList<Card> getPlayer2Hand() {
        return player2Hand;
    }

    public void setPlayer2Hand(ArrayList<Card> player2Hand) {
        this.player2Hand = player2Hand;
    }

    public HashMap<String, Object> getMoves() {
        return moves;
    }

    public void setMoves(HashMap<String, Object> moves) {
        this.moves = moves;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }
}
