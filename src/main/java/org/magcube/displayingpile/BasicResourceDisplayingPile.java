package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class BasicResourceDisplayingPile implements DisplayingPile<ResourceCard> {

  private final CardType cardType = CardType.BASIC_RESOURCE;
  private final Map<Integer, List<ResourceCard>> availableCards = new HashMap<>();
  private final int maxDisplayingSize;

  public BasicResourceDisplayingPile(List<ResourceCard> deck) {
    for (var card : deck) {
      availableCards.computeIfAbsent(card.typeId(), k -> new ArrayList<>()).add(card);
    }
    maxDisplayingSize = availableCards.size();
  }

  @Override
  public CardType getCardType() {
    return cardType;
  }

  @Override
  public List<List<ResourceCard>> getDisplaying() {
    return new ArrayList<>(availableCards.values());
  }

  @Override
  public List<ResourceCard> getDeck() {
    return Collections.emptyList();
  }

  @Override
  public List<ResourceCard> getDiscardPile() {
    return Collections.emptyList();
  }

  @Override
  public int getMaxDisplayingSize() {
    return maxDisplayingSize;
  }

  @Override
  public int deckSize() {
    return 0;
  }

  @Override
  public int discardPileSize() {
    return 0;
  }

  private Optional<ResourceCard> cardInDisplay(CardIdentity cardIdentity) {
    List<ResourceCard> cards = availableCards.getOrDefault(cardIdentity.typeId(), Collections.emptyList());
    return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(0));
  }

  @Override
  public Optional<List<ResourceCard>> cardsInDisplay(List<CardIdentity> cardIdentities) {
    if (cardIdentities.size() == 1) {
      return cardInDisplay(cardIdentities.get(0)).map(Collections::singletonList);
    }

    var cardsInDisplaying = new ArrayList<ResourceCard>();

    var quantityMap = new HashMap<Integer, Integer>();
    cardIdentities.forEach(cardIdentity -> quantityMap.compute(cardIdentity.typeId(), (k, v) -> v == null ? 1 : v + 1));

    for (Entry<Integer, Integer> entry : quantityMap.entrySet()) {
      var typeId = entry.getKey();
      var quantity = entry.getValue();
      var cards = availableCards.get(typeId);
      if (cards == null || cards.size() < quantity) {
        return Optional.empty();
      }
      cardsInDisplaying.addAll(cards.subList(0, quantity));

    }

    return Optional.of(cardsInDisplaying);
  }

  @Override
  public void takeCards(List<ResourceCard> cardsInDisplaying) {
    for (ResourceCard card : cardsInDisplaying) {
      List<ResourceCard> correspondingList = availableCards.get(card.typeId());
      correspondingList.remove(card);
    }
  }

  @Override
  public void discardCards(List<ResourceCard> cards) {
    for (var card : cards) {
      availableCards.get(card.typeId()).add(card);
    }
  }

  @Override
  public void refillCards() {
    // do nothing
  }
}
