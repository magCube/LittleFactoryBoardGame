package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;

public class LevelOneResourcePile implements DisplayingPile<ResourceCard> {

  private final CardType cardType = CardType.LEVEL_ONE_RESOURCE;
  private final List<List<ResourceCard>> displaying;
  private final List<ResourceCard> deck;
  private final List<ResourceCard> discardPile;
  // default to 5, put it as a field so that flexible to change in future
  private final int maxDisplayingSize = 5;

  public LevelOneResourcePile(List<ResourceCard> deck) throws DisplayPileException {
    if (!isConsistentCardType(deck)) {
      throw new DisplayPileException("Deck cardType is not consistent");
    }
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

  private ResourceCard cardInDisplay(CardIdentity cardIdentity) {
    var listWithSameCardIdentity = findTheListWithTheSameCardIdentity(cardIdentity);
    return listWithSameCardIdentity.map(cards -> cards.get(0)).orElse(null);
  }

  @Override
  public List<ResourceCard> cardsInDisplay(List<CardIdentity> cardIdentities) throws DisplayPileException {
    if (!isConsistentCardTypeInCardIdentity(cardIdentities)) {
      throw new DisplayPileException("Inconsistent card type in requesting cards");
    }

    if (cardIdentities.size() == 1) {
      var card = cardInDisplay(cardIdentities.get(0));
      return card == null ? null : List.of(card);
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
        return null;
      }
    }

    return cardsInDisplaying;
  }

  @NotNull
  @Override
  public List<ResourceCard> takeCards(List<ResourceCard> cardsInDisplaying) throws DisplayPileException {
    var correspondingInnerLists = new ArrayList<List<ResourceCard>>();
    for (ResourceCard card : cardsInDisplaying) {
      var list = findTheListWithTheSameCardIdentity(card.getCardIdentity());
      if (list.isPresent() && list.get().contains(card)) {
        correspondingInnerLists.add(list.get());
      } else {
        // guarded by GameBoard, should not happen in real game
        throw new DisplayPileException("Cards not in displaying");
      }
    }

    for (int i = 0; i < cardsInDisplaying.size(); i++) {
      var card = cardsInDisplaying.get(i);
      var list = correspondingInnerLists.get(i);
      list.remove(card);
    }

    return cardsInDisplaying;
  }

  @Override
  public void discardCards(List<ResourceCard> cards) throws DisplayPileException {
    if (isConsistentCardType(cards)) {
      this.discardPile.addAll(cards);
    } else {
      // guarded by GameBoard, should not happen in real game
      throw new DisplayPileException("Cards to discard are not consistent");
    }
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
  public void refillCards() throws DisplayPileException {
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
