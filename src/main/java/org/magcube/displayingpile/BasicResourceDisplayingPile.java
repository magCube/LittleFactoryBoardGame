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
import org.magcube.exception.DisplayPileException;

public class BasicResourceDisplayingPile implements DisplayingPile<ResourceCard> {

  private final CardType cardType = CardType.BASIC_RESOURCE;
  private final Map<Integer, List<ResourceCard>> availableCards = new HashMap<>();
  private final int maxDisplayingSize;

  public BasicResourceDisplayingPile(List<ResourceCard> deck) throws DisplayPileException {
    if (!isConsistentCardType(deck)) {
      throw new DisplayPileException("Deck cardType is not consistent");
    }
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

  public List<ResourceCard> takeCards(List<CardIdentity> cardIdentities) throws DisplayPileException {
    if (!isConsistentCardTypeInCardIdentity(cardIdentities)) {
      throw new DisplayPileException("Not all cards are in displaying pile");
    }

    if (cardIdentities.size() == 1) {
      return List.of(takeCard(cardIdentities.get(0)));
    }

    var cardsInDisplaying = new ArrayList<ResourceCard>();

    var quantityMap = new HashMap<Integer, Integer>();
    cardIdentities.forEach(cardIdentity -> quantityMap.compute(cardIdentity.getTypeId(), (k, v) -> v == null ? 1 : v + 1));

    for (Entry<Integer, Integer> entry : quantityMap.entrySet()) {
      Integer typeId = entry.getKey();
      Integer quantity = entry.getValue();
      var cards = availableCards.get(typeId);
      if (cards == null || cards.size() < quantity) {
        throw new DisplayPileException("Not all cards are in displaying pile");
      } else {
        cardsInDisplaying.addAll(cards.subList(0, quantity));
      }
    }

    cardsInDisplaying.forEach(card -> availableCards.get(card.typeId()).remove(card));
    return cardsInDisplaying;
  }

  private ResourceCard takeCard(CardIdentity cardIdentity) throws DisplayPileException {
    var cards = availableCards.get(cardIdentity.getTypeId());
    if (cards == null || cards.isEmpty()) {
      throw new DisplayPileException("Not all cards are in displaying pile");
    } else {
      return cards.remove(0);
    }
  }

  @Override
  public void discardCards(List<ResourceCard> cards) throws DisplayPileException {
    if (!isConsistentCardType(cards)) {
      throw new DisplayPileException("Cards to discard are not consistent");
    }

    for (var card : cards) {
      var cardList = availableCards.get(card.typeId());
      if (cardList == null) {
        throw new DisplayPileException("Unknown typeId");
      } else {
        cardList.add(card);
      }
    }
  }

  @Override
  public void refillCards() throws DisplayPileException {
    // do nothing
  }
}
