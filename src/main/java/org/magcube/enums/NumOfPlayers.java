package org.magcube.enums;

public enum NumOfPlayers {
  TWO(2),
  THREE(3),
  FOUR(4);

  private final int value;

  NumOfPlayers(int value) {
    this.value = value;
  }

  public static NumOfPlayers fromValue(int value) {
    for (NumOfPlayers num : NumOfPlayers.values()) {
      if (num.getValue() == value) {
        return num;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }

  public int getValue() {
    return value;
  }
}
