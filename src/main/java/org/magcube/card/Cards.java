package org.magcube.card;

import java.util.Arrays;

public class Cards {

  private Cards() {
  }

  public static boolean isCardIdentitiesSame(CardIdentity[] actual, CardIdentity[] testing) {
    if (actual.length == 0 || actual.length != testing.length) {
      return false;
    }

    var actualSorted = Arrays.copyOf(actual, actual.length);
    var testingSorted = Arrays.copyOf(testing, testing.length);
    Arrays.sort(actualSorted);
    Arrays.sort(testingSorted);

    return Arrays.equals(actualSorted, testingSorted);
  }

  public static boolean isOneDimCardIdentitiesMatch(CardIdentity[] actual, CardIdentity[] checkingCardIdentities) {
    return Cards.isCardIdentitiesSame(actual, checkingCardIdentities);
  }

  // the outer layer is OR, the inner layer is AND
  public static boolean isTwoDimCardIdentitiesMatch(CardIdentity[][] actual, CardIdentity[] checkingCardIdentities) {
    return Arrays.stream(actual).anyMatch(option -> Cards.isCardIdentitiesSame(option, checkingCardIdentities));
  }
}
