package org.magcube.card;

import lombok.Builder;

public class Building extends Card {

  @Builder
  public Building(int value, String name, Card[] cost, Card[] capital, int typeId) {
    super(value, name, cost, capital, typeId);
  }
}
