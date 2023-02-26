package org.magcube.card;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Card {

  public Card(int value, String name, Card[] cost, int typeId) {
    this.value = value;
    this.name = name;
    this.cost = cost;
    this.typeId = typeId;
  }

  protected final int value; // the coin value of this card
  protected final String name;
  protected final Card[] cost;
  protected final int typeId;

}
