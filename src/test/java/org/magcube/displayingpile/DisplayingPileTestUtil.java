package org.magcube.displayingpile;

import java.util.List;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;

public class DisplayingPileTestUtil {

  public static <T extends Card> List<T> takeCardHelper(DisplayingPile<T> pile, List<CardIdentity> cardIdentities) {
    var cardsInDisplay = pile.cardsInDisplay(cardIdentities);
    if (cardsInDisplay == null) {
      throw new RuntimeException("Not all cards are in display");
    }
    pile.takeCards(cardsInDisplay);
    return cardsInDisplay;
  }

  public static <T extends Card> int numOfCardsInDisplaying(List<List<T>> displaying, CardIdentity cardIdentity) {
    return Math.toIntExact(displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum));
  }
}
