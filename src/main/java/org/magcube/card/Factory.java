package org.magcube.card;

import lombok.Builder;

public class Factory extends Card {

  @Builder(builderMethodName = "factoryBuilder")
  public Factory(int value, String name, Card[] cost, int typeId) {
    super(value, name, cost, typeId);
  }
}
