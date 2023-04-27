package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class LevelOneResourcePile implements DisplayingPile<ResourceCard> {

  private final CardType cardType = CardType.LEVEL_ONE_RESOURCE;
  private final List<List<ResourceCard>> displaying;
  private final List<ResourceCard> deck;
  private final List<ResourceCard> discardPile;
  private final int maxDisplayingSize = 5;

  public LevelOneResourcePile(List<ResourceCard> deck) {
    displaying = new ArrayList<>(new ArrayList<>());
    this.deck = new ArrayList<>();
    IntStream.range(0, maxDisplayingSize).forEach(i -> displaying.add(new ArrayList<>()));
    discardPile = new ArrayList<>(deck);
    refillCards();
  }

  @Override
  public CardType getCardType() {
    return cardType;
  }

  @Override
  public List<List<ResourceCard>> getDisplaying() {
    return displaying;
  }

  @Override
  public List<ResourceCard> getDeck() {
    return deck;
  }

  @Override
  public List<ResourceCard> getDiscardPile() {
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

  private Optional<ResourceCard> cardInDisplay(CardIdentity cardIdentity) {
    var listWithSameCardIdentity = findTheListWithTheSameCardIdentity(cardIdentity);
    return listWithSameCardIdentity.map(cards -> cards.get(0));
  }

  @Override
  public Optional<List<ResourceCard>> cardsInDisplay(List<CardIdentity> cardIdentities) {
    if (cardIdentities.size() == 1) {
      return cardInDisplay(cardIdentities.get(0)).map(Collections::singletonList);
    }

    var cardsInDisplaying = new ArrayList<ResourceCard>();

    var quantityMap = cardIdentities.stream().collect(Collectors.toMap(Function.identity(), x -> 1, Integer::sum));

    for (Entry<CardIdentity, Integer> entry : quantityMap.entrySet()) {
      CardIdentity cardIdentity = entry.getKey();
      Integer quantity = entry.getValue();
      var listWithSameCardIdentity = findTheListWithTheSameCardIdentity(cardIdentity, quantity);
      if (listWithSameCardIdentity.isPresent()) {
        var list = listWithSameCardIdentity.get();
        cardsInDisplaying.addAll(list.subList(0, quantity));
      } else {
        return Optional.empty();
      }
    }

    return Optional.of(cardsInDisplaying);
  }

  @Override
  public void takeCards(List<ResourceCard> cardsInDisplaying) {
    for (ResourceCard card : cardsInDisplaying) {
      var list = findTheListWithTheSameCardIdentity(card.getCardIdentity());
      list.ifPresent(cards -> cards.remove(card));
    }
  }

  @Override
  public void discardCards(List<ResourceCard> cards) {
    this.discardPile.addAll(cards);
  }

  private void fillDeckWithDiscardPileIfDeckUsedUp() {
    if (deck.isEmpty() && displaying.stream().anyMatch(List::isEmpty)) {
      deck.addAll(discardPile);
      Collections.shuffle(deck);
      discardPile.clear();
    }
  }

  private Optional<List<ResourceCard>> findEmptyListInDisplayingPile() {
    return displaying.stream()
        .filter(List::isEmpty)
        .findFirst();
  }

  @Override
  public void refillCards() {
    fillDeckWithDiscardPileIfDeckUsedUp();

    var emptyList = findEmptyListInDisplayingPile();
    while (emptyList.isPresent() && !deck.isEmpty()) {
      var newCard = deck.remove(0);
      var listWithSameCardIdentity = findTheListWithTheSameCardIdentity(newCard);
      if (listWithSameCardIdentity.isPresent()) {
        listWithSameCardIdentity.get().add(newCard);
      } else {
        emptyList.get().add(newCard);
      }
      emptyList = displaying.stream().filter(List::isEmpty).findFirst();
    }
  }

  private Optional<List<ResourceCard>> findTheListWithTheSameCardIdentity(CardIdentity cardIdentity) {
    return displaying.stream()
        .filter(innerList -> innerList.stream().anyMatch(card -> card.isIdentical(cardIdentity)))
        .findFirst();
  }

  private Optional<List<ResourceCard>> findTheListWithTheSameCardIdentity(CardIdentity cardIdentity, int minSize) {
    return displaying.stream()
        .filter(innerList -> innerList.stream().anyMatch(card -> card.isIdentical(cardIdentity)))
        .filter(innerList -> innerList.size() >= minSize)
        .findFirst();
  }

  private Optional<List<ResourceCard>> findTheListWithTheSameCardIdentity(ResourceCard card) {
    return displaying.stream()
        .filter(innerList -> innerList.stream().anyMatch(card::isIdentical))
        .findFirst();
  }
}
