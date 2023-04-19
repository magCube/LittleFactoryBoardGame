package org.magcube.card;

public enum CardType {
  BASIC_RESOURCE(0),
  LEVEL_ONE_RESOURCE(1),
  LEVEL_TWO_RESOURCE(2),
  BUILDING(3);

  private final int value;

  CardType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
