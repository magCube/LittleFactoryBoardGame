package org.magcube.littlefactory.card;

public enum CardType {
  BASIC_RESOURCE(0),
  LEVEL_1_RESOURCE(1),
  LEVEL_2_RESOURCE(2),
  BUILDING(3);

  private final int value;

  CardType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
