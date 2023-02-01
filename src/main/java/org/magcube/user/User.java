package org.magcube.user;

import java.util.ArrayList;
import org.magcube.card.Card;
import org.magcube.card.Factory;

public class User {

  private String name;
  private final ArrayList<Factory> factories;
  private final ArrayList<Card> cards;
  private int coin;
  private int points;

  public User() {
    this.cards = new ArrayList<>();
    this.factories = new ArrayList<>();
  }

  public ArrayList<Card> getCards() {
    return cards;
  }

  public void takeCards(ArrayList<Card> cards) {
    //TODO take cards from user, should support factories as well
  }

  public void giveCards(ArrayList<Card> cards) {
    //TODO give cards to user
  }

  public int getCoin() {
    return coin;
  }

  public void giveCoin(int coin) {
    this.coin += coin;
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
