package org.magcube.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;

public class Player {

  private final ArrayList<BuildingCard> buildings;
  private final ArrayList<Card> cards;
  private String name;
  private int coin;
  private int points;

  public Player() {
    this.cards = new ArrayList<>();
    this.buildings = new ArrayList<>();
  }

  public List<Card> getCards() {
    return Collections.unmodifiableList(this.cards);
  }

  public List<Card> getBuildings() {
    return Collections.unmodifiableList(this.buildings);
  }

  public void takeCards(List<Card> cards) {
    cards.forEach(card -> {
      if (card instanceof BuildingCard) {
        this.buildings.add((BuildingCard) card);
      } else {
        this.cards.add(card);
      }
    });
  }

  public void giveCards(List<Card> cards) {
    this.cards.removeAll(cards);
  }

  public int getCoin() {
    return this.coin;
  }

  public void giveCoin(int coin) {
    this.coin = coin;
  }

  public int getPoints() {
    return points + this.buildings.size();
  }

  public void addPoints(int points) {
    if (points < 0) {
      System.out.println("Negative number is not expected in addPoints!");
      return;
    }
    this.points += points;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean ownCard(Card card) {
    return isOwnCard(card) || isOwnFactory(card);
  }

  private boolean isOwnCard(Card card) {
    return cards.contains(card);
  }

  private boolean isOwnFactory(Card card) {
    return buildings.contains(card);
  }

  private boolean isOwnCoin(int value) {
    return value == coin;
  }
}
