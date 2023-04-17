package org.magcube.littlefactory.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.magcube.littlefactory.card.Card;
import org.magcube.littlefactory.card.CardType;
import org.magcube.littlefactory.exception.DisplayPileException;

@Getter
//TODO: refactor this to be an interface

//TODO: need to return unmodifiableList for safety
public class DisplayingPile<T extends Card> {

  private final CardType cardType;
  private final ArrayList<T> deck;
  private final ArrayList<ArrayList<T>> displaying;
  private final ArrayList<T> discardPile;
  // default to 5, put it as a field so that flexible to change in future
  private final int maxDisplayingSize = 5;

  public DisplayingPile(List<T> deck) throws DisplayPileException {
    verifyDeck(deck);
    cardType = deck.get(0).cardType();
    displaying = new ArrayList<>(new ArrayList<>());
    this.deck = new ArrayList<>(deck);
    discardPile = new ArrayList<>();
    refillCards();
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

  public void discardCard(List<T> cards) throws DisplayPileException {
    if (consistentCardType(cards)) {
      this.discardPile.addAll(cards);
    } else {
      throw new DisplayPileException("Cards to discard are not consistent");
    }
  }

  public void discardCard(T card) throws DisplayPileException {
    if (consistentCardType(card)) {
      this.discardPile.add(card);
    } else {
      throw new DisplayPileException("Card to discard is not consistent");
    }
  }

  public void refillCards() throws DisplayPileException {
    fillDeckWithDiscardPileIfDeckUsedUp();
    while (displaying.size() < maxDisplayingSize && !deck.isEmpty()) {
      var card = deck.remove(0);
      if (displaying.stream().anyMatch(ary -> ary.stream().anyMatch(card::isIdentical))) {
        // same kind of card already in display
        addCardToSameTypeOfList(card);
      } else {
        // no same kind displaying, can add as a new column directly
        addANewColumnToDisplayingLists(card);
      }
    }
  }

  private void addANewColumnToDisplayingLists(T card) {
    var newArrayList = new ArrayList<T>();
    newArrayList.add(card);
    displaying.add(newArrayList);
  }

  private void addCardToSameTypeOfList(T card) throws DisplayPileException {
    var arrayList = displaying.stream().filter(ary -> ary.stream().anyMatch(card::isIdentical))
        .findFirst();
    if (arrayList.isEmpty()) {
      throw new DisplayPileException(
          "Found match type in display pile but cannot find the corresponding list in displaying piles!");
    }
    arrayList.get().add(card);
  }

  private void fillDeckWithDiscardPileIfDeckUsedUp() {
    if (deck.isEmpty() && displaying.size() < maxDisplayingSize) {
      deck.addAll(discardPile);
      Collections.shuffle(deck);
      discardPile.clear();
    }
  }

  private void verifyDeck(List<T> deck) throws DisplayPileException {
    if (deck.size() < 8) {
      throw new DisplayPileException("deck have too few cards! At least 9!");
    }
    var cardType = deck.get(0).cardType();
    if (deck.stream().anyMatch(card -> card.cardType() != cardType)) {
      throw new DisplayPileException("All cards in deck should be the same type!");
    }
  }

  private boolean consistentCardType(T card) {
    return card.cardType() == cardType;
  }

  private boolean consistentCardType(List<T> cards) {
    return cards.stream().allMatch(card -> card.cardType() == cardType);
  }
}
