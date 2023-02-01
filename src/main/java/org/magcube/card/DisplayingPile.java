package org.magcube.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.magcube.exception.DisplayPileException;

public class DisplayingPile<T extends Card> {

  private final ArrayList<T> deck;
  private final ArrayList<ArrayList<T>> displaying;

  public DisplayingPile(List<T> deck) throws DisplayPileException {
    verifyDeck(deck);
    this.displaying = new ArrayList<>(new ArrayList<>());
    this.deck = new ArrayList<>(deck);
    refillCards();
  }

  private void verifyDeck(List<T> deck) throws DisplayPileException {
    if (deck.size() < 8) {
      throw new DisplayPileException("deck have too few cards! At least 8!");
    }
  }

  public ArrayList<ArrayList<T>> getDisplaying() {
    return displaying;
  }

  public void insertCard(ArrayList<T> deck) {

  }

  public void refillCards() throws DisplayPileException {
    Collections.shuffle(deck);
    while (displaying.size() < 5) {
      var card = deck.remove(0);
      if (displaying.stream().anyMatch(ary -> ary.stream()
          .anyMatch(displaying -> card.getTypeId() == (displaying.getTypeId())))) {
        var arrayList = displaying.stream().filter(ary -> ary.stream()
            .anyMatch(displaying -> card.getTypeId() == (displaying.getTypeId()))).findFirst();
        if (arrayList.isEmpty()) {
          throw new DisplayPileException(
              "Found match type in display pile but cannot find the corresponding list in displaying piles!");
        }
        arrayList.get().add(card);
      } else {
        var newArrayList = new ArrayList<Card>();
        newArrayList.add(card);
        displaying.add((ArrayList<T>) newArrayList);
      }
    }
  }
}
