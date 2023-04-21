package org.magcube.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cards {

  private Cards() {
  }

  public static boolean isCardIdentitiesSame(List<CardIdentity> actual, List<CardIdentity> testing) {
    if (actual.size() != testing.size()) {
      return false;
    }

    List<CardIdentity> actualSorted = new ArrayList<>(actual);
    List<CardIdentity> testingSorted = new ArrayList<>(testing);
    Collections.sort(actualSorted);
    Collections.sort(testingSorted);

    return actualSorted.equals(testingSorted);
  }
}
