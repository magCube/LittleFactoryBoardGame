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
import org.magcube.exception.DisplayPileException;

public abstract class UniqueCardPile<T extends Card> implements IDisplayingPile<T> {

  protected final CardType cardType;
  protected final List<T> availableCards;
  protected final List<T> deck;
  protected final List<T> discardPile;
  // default to 5, put it as a field so that flexible to change in future
  protected final int maxDisplayingSize = 5;

  public UniqueCardPile(CardType cardType, List<T> deck) throws DisplayPileException {
    this.cardType = cardType;
    if (!isConsistentCardType(deck)) {
      throw new DisplayPileException("Deck cardType is not consistent");
    }
    if (haveDuplicatedCards(deck)) {
      throw new DisplayPileException("Deck has duplicated cards");
    }
    availableCards = new ArrayList<>(Collections.nCopies(maxDisplayingSize, null));
    this.deck = new ArrayList<>();
    discardPile = new ArrayList<>(deck);
    initPile();
  }

  protected abstract void initPile();

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

  private T takeCard(CardIdentity cardIdentity) throws DisplayPileException {
    var card = availableCards.stream().filter(x -> x != null && x.isIdentical(cardIdentity)).findFirst();
    if (card.isPresent()) {
      var returnCard = card.get();
      int index = availableCards.indexOf(returnCard);
      availableCards.set(index, null);
      return returnCard;
    } else {
      throw new DisplayPileException("Not all cards are in displaying pile");
    }
  }

  @Override
  public List<T> takeCards(List<CardIdentity> cardIdentities) throws DisplayPileException {
    if (!isConsistentCardTypeInCardIdentity(cardIdentities)) {
      throw new DisplayPileException("Not all cards are in displaying pile");
    }
    if (haveDuplicatedCardIdentities(cardIdentities)) {
      throw new DisplayPileException("Not all cards are in displaying pile");
    }
    if (cardIdentities.size() == 1) {
      return List.of(takeCard(cardIdentities.get(0)));
    }

    var cardsInDisplaying = new ArrayList<T>();
    var indexes = new ArrayList<Integer>();

    for (CardIdentity cardIdentity : cardIdentities) {
      var optionalCard = availableCards.stream().filter(x -> x != null && x.isIdentical(cardIdentity)).findFirst();
      if (optionalCard.isPresent()) {
        T card = optionalCard.get();
        cardsInDisplaying.add(card);
        int index = availableCards.indexOf(card);
        indexes.add(index);
      } else {
        throw new DisplayPileException("Not all cards are in displaying pile");
      }
    }

    IntStream.range(0, cardsInDisplaying.size()).forEach(i -> availableCards.set(indexes.get(i), null));
    return cardsInDisplaying;
  }

  protected boolean haveDuplicatedCards(List<T> cards) {
    var cardIdentities = cards.stream().map(Card::getCardIdentity).collect(Collectors.toSet());
    return cardIdentities.size() != cards.size();
  }

  protected boolean haveDuplicatedCardIdentities(List<CardIdentity> cardIdentities) {
    var cardIdentitiesSet = new HashSet<>(cardIdentities);
    return cardIdentitiesSet.size() != cardIdentities.size();
  }

  protected List<Integer> nullIndexesInAvailableCards() {
    return IntStream.range(0, availableCards.size())
        .filter(i -> availableCards.get(i) == null)
        .boxed()
        .collect(Collectors.toList());
  }
}
