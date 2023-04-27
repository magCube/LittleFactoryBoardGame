package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;

public abstract class UniqueCardPile<T extends Card> implements DisplayingPile<T> {

  protected final CardType cardType;
  protected final List<T> availableCards;
  protected final List<T> deck;
  protected final List<T> discardPile;
  protected final int maxDisplayingSize = 5;

  public UniqueCardPile(CardType cardType, List<T> deck) {
    this.cardType = cardType;
    availableCards = new ArrayList<>(Collections.nCopies(maxDisplayingSize, null));
    this.deck = new ArrayList<>();
    discardPile = new ArrayList<>(deck);
  }

  @Override
  public CardType getCardType() {
    return cardType;
  }

  @Override
  public List<List<T>> getDisplaying() {
    return availableCards.stream().map(x -> x == null ? Collections.<T>emptyList() : List.of(x)).collect(Collectors.toList());
  }

  @Override
  public List<T> getDeck() {
    return deck;
  }

  @Override
  public List<T> getDiscardPile() {
    return discardPile;
  }

  @Override
  public int getMaxDisplayingSize() {
    return maxDisplayingSize;
  }

  @Override
  public int deckSize() {
    return deck.size();
  }

  @Override
  public int discardPileSize() {
    return discardPile.size();
  }

  protected boolean haveDuplicatedCardIdentities(List<CardIdentity> cardIdentities) {
    var cardIdentitiesSet = new HashSet<>(cardIdentities);
    return cardIdentitiesSet.size() != cardIdentities.size();
  }

  private T cardInDisplay(CardIdentity cardIdentity) {
    var card = availableCards.stream().filter(x -> x != null && x.isIdentical(cardIdentity)).findFirst();
    return card.orElse(null);
  }

  @Override
  public List<T> cardsInDisplay(List<CardIdentity> cardIdentities) {
    if (haveDuplicatedCardIdentities(cardIdentities)) {
      return null;
    }

    if (cardIdentities.size() == 1) {
      var card = cardInDisplay(cardIdentities.get(0));
      return card == null ? null : List.of(card);
    }

    var cardsInDisplaying = new ArrayList<T>();

    for (CardIdentity cardIdentity : cardIdentities) {
      var card = availableCards.stream().filter(x -> x != null && x.isIdentical(cardIdentity)).findFirst();
      if (card.isPresent()) {
        cardsInDisplaying.add(card.get());
      } else {
        return null;
      }
    }

    return cardsInDisplaying;
  }

  @Override
  public void takeCards(List<T> cardsInDisplaying) {
    for (T card : cardsInDisplaying) {
      int index = availableCards.indexOf(card);
      availableCards.set(index, null);
    }
  }

  protected List<Integer> nullIndexesInAvailableCards() {
    return IntStream.range(0, availableCards.size())
        .filter(i -> availableCards.get(i) == null)
        .boxed()
        .collect(Collectors.toList());
  }
}
