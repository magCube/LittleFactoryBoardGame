package org.magcube.card.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.magcube.card.Card;
import org.magcube.exception.DisplayPileException;

public class DisplayingPile<T extends Card> {

  //TODO: discard pile
  private final ArrayList<T> deck;
  private final ArrayList<ArrayList<T>> displaying;
  private final ArrayList<T> discardPile;
  private final int displayingSize = 5; // default to 5, put it as a field so that flexible to change in future

  public DisplayingPile(List<T> deck) throws DisplayPileException {
    verifyDeck(deck);
    this.displaying = new ArrayList<>(new ArrayList<>());
    this.deck = new ArrayList<>(deck);
    this.discardPile = new ArrayList<>();
    refillCards();
  }

  protected void verifyDeck(List<T> deck) throws DisplayPileException {
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

  public int discardPileSize() {
    return discardPile.size();
  }

  public void takeCard(T card) {
    var containingListOpt = displaying.stream().filter(list -> list.contains(card))
        .findAny();
    var containingList = containingListOpt.orElseThrow();
    containingList.remove(card);
    if (displaying.stream().anyMatch(ArrayList::isEmpty)) {
      displaying.removeIf(ArrayList::isEmpty);
    }
  }

  public void discardCard(List<T> deck) {
    this.discardPile.addAll(deck);
  }

  public void discardCard(T card) {
    this.discardPile.add(card);
  }

  public void refillCards() throws DisplayPileException {
    fillDeckWithDiscardPileIfDeckUsedUp();
    while (displaying.size() < displayingSize && !deck.isEmpty()) {
      var card = deck.remove(0);
      if (displaying.stream().anyMatch(ary -> ary.stream()
          .anyMatch(displaying -> card.getTypeId()
              == displaying.getTypeId()))) { //same kind of card already in display
        addCardToSameTypeOfList(card);
      } else {//no same kind displaying, can add as a new column directly
        addANewColumnToDisplayingLists(card);
      }
      if (displaying.size() < displayingSize) {
        fillDeckWithDiscardPileIfDeckUsedUp();
      }
    }
  }

  private void addANewColumnToDisplayingLists(T card) {
    var newArrayList = new ArrayList<T>();
    newArrayList.add(card);
    displaying.add(newArrayList);
  }

  private void addCardToSameTypeOfList(T card) throws DisplayPileException {
    var arrayList = displaying.stream().filter(ary -> ary.stream()
        .anyMatch(displaying -> card.getTypeId() == (displaying.getTypeId()))).findFirst();
    if (arrayList.isEmpty()) {
      throw new DisplayPileException(
          "Found match type in display pile but cannot find the corresponding list in displaying piles!");
    }
    arrayList.get().add(card);
  }

  private void fillDeckWithDiscardPileIfDeckUsedUp() {
    if (deck.isEmpty() && displaying.size() < displayingSize) {
      deck.addAll(discardPile);
      Collections.shuffle(deck);
      discardPile.clear();
    }
  }

}
