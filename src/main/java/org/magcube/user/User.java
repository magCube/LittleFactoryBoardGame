package org.magcube.user;

import org.magcube.card.Card;
import org.magcube.card.Factory;

import java.util.ArrayList;

public class User {
    private String name;
    private ArrayList<Factory> factories;
    private ArrayList<Card> cards;
    private int coin;
    private int points;

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
