package org.magcube.player;

public enum NumOfPlayers {
  TWO(2),
  THREE(3),
  FOUR(4);

  private final int value;

  NumOfPlayers(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
