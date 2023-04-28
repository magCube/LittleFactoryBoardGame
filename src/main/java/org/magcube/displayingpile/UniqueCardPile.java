package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.enums.CardType;

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

  @Override
  public Optional<List<T>> cardsInDisplay(List<CardIdentity> cardIdentities) {
    if (haveDuplicatedCardIdentities(cardIdentities)) {
      return Optional.empty();
    }

    List<T> cardsInDisplaying = availableCards.stream()
        .filter(Objects::nonNull)
        .filter(card -> cardIdentities.stream().anyMatch(card::isIdentical))
        .collect(Collectors.toList());

    return cardsInDisplaying.size() == cardIdentities.size()
        ? Optional.of(cardsInDisplaying)
        : Optional.empty();
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
