package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

  private ResourceCard cardInDisplay(CardIdentity cardIdentity) {
    var cards = availableCards.get(cardIdentity.typeId());
    if (cards == null || cards.isEmpty()) {
      return null;
    } else {
      return cards.get(0);
    }
  }

  @Override
  public List<ResourceCard> cardsInDisplay(List<CardIdentity> cardIdentities) {
    if (cardIdentities.size() == 1) {
      var card = cardInDisplay(cardIdentities.get(0));
      return card == null ? null : List.of(card);
    }

    var cardsInDisplaying = new ArrayList<ResourceCard>();

    var quantityMap = new HashMap<Integer, Integer>();
    cardIdentities.forEach(cardIdentity -> quantityMap.compute(cardIdentity.typeId(), (k, v) -> v == null ? 1 : v + 1));

    for (Entry<Integer, Integer> entry : quantityMap.entrySet()) {
      var typeId = entry.getKey();
      var quantity = entry.getValue();
      var cards = availableCards.get(typeId);
      if (cards == null || cards.size() < quantity) {
        return null;//TODO:@Tam null is consider anti-pattern now, may think of using Optional
      } else {
        cardsInDisplaying.addAll(cards.subList(0, quantity));
      }
    }

    return cardsInDisplaying;
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
