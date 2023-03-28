package org.magcube.card;

import lombok.Builder;
import lombok.Getter;

@Getter
public abstract class Card {

  protected final int value; // the coin value of this card
  protected final String name;
  protected final Card[] cost;

  protected final Card[] capital;
  protected final int typeId;

  public Card(int value, String name, Card[] cost, Card[] capital, int typeId) {
    this.value = value;
    this.name = name;
    this.cost = cost;
    this.capital = capital;
    this.typeId = typeId;
  }

}
