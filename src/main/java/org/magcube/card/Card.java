package org.magcube.card;

import lombok.Builder;

@Builder
public class Card {

  public Card(int value, String name, Card[] cost, int typeId) {
    this.value = value;
    this.name = name;
    this.cost = cost;
    this.typeId = typeId;
  }

  protected int value; // the coin value of this card
  protected String name;
  protected Card[] cost;
  protected int typeId;

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  public Card[] getCost() {
    return cost;
  }

  public int getTypeId() {
    return typeId;
  }
}
