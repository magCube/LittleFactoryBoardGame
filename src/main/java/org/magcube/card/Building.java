package org.magcube.card;

import lombok.Builder;

public class Building extends Card {

  @Builder(builderMethodName = "factoryBuilder")
  public Building(int value, String name, Card[] cost, int typeId) {
    super(value, name, cost, typeId);
  }
}
