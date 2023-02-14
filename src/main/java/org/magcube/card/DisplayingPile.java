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

  public List<ArrayList<T>> getDisplaying() {
    return Collections.unmodifiableList(displaying);
  }

  public int deckSize() {
    return deck.size();
  }

  public boolean takeCard(T card) throws DisplayPileException {
    var containingListOpt = displaying.stream().filter(list -> list.contains(card))
        .findAny();
    var containingList = containingListOpt.orElseThrow();
    containingList.remove(card);
    if (displaying.stream().anyMatch(ArrayList::isEmpty)) {
      displaying.removeIf(ArrayList::isEmpty);
      refillCards();
    }
    return true;
  }

  public void insertCard(List<T> deck) {
    this.deck.addAll(deck);
  }

  public void refillCards() throws DisplayPileException {
    Collections.shuffle(deck);
    while (displaying.size() < 5 && !deck.isEmpty()) {
      var card = deck.remove(0);
      if (displaying.stream().anyMatch(ary -> ary.stream()
          .anyMatch(displaying -> card.getTypeId()
              == displaying.getTypeId()))) { //same kind of card already in display
        var arrayList = displaying.stream().filter(ary -> ary.stream()
            .anyMatch(displaying -> card.getTypeId() == (displaying.getTypeId()))).findFirst();
        if (arrayList.isEmpty()) {
          throw new DisplayPileException(
              "Found match type in display pile but cannot find the corresponding list in displaying piles!");
        }
        arrayList.get().add(card);
      } else {//no same kind displaying, can add as a new column directly
        var newArrayList = new ArrayList<T>();
        newArrayList.add(card);
        displaying.add(newArrayList);
      }
    }
  }
}
