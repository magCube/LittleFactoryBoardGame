package org.magcube.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
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

  private ResourceCard cardInDisplay(CardIdentity cardIdentity) {
    var cards = availableCards.get(cardIdentity.typeId());
    if (cards == null || cards.isEmpty()) {
      return null;
    } else {
      return cards.get(0);
    }
  }

  @Override
  public List<ResourceCard> cardsInDisplay(List<CardIdentity> cardIdentities) throws DisplayPileException {
    if (!isConsistentCardTypeInCardIdentity(cardIdentities)) {
      // guarded by GameBoard, should not happen in real game
      throw new DisplayPileException("Inconsistent card type in requesting cards");
    }

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
  @NotNull
  public List<ResourceCard> takeCards(List<ResourceCard> cardsInDisplaying) throws DisplayPileException {
    var correspondingInnerLists = new ArrayList<List<ResourceCard>>();
    for (ResourceCard card : cardsInDisplaying) {
      var cards = availableCards.get(card.typeId());
      if (cards == null || !cards.contains(card)) {
        // guarded by GameBoard, should not happen in real game
        throw new DisplayPileException("Card not in displaying pile");
      }
      correspondingInnerLists.add(cards);
    }

    for (int i = 0; i < cardsInDisplaying.size(); i++) {
      var card = cardsInDisplaying.get(i);
      var list = correspondingInnerLists.get(i);
      list.remove(card);
    }

    return cardsInDisplaying;//TODO:@Tam do we need to return this list?
  }

  @Override
  public void discardCards(List<ResourceCard> cards) throws DisplayPileException {
    if (!isConsistentCardType(cards)) {
      // guarded by GameBoard, should not happen in real game
      throw new DisplayPileException("Cards to discard are not consistent");
    }

    if (!cards.stream().allMatch(card -> availableCards.containsKey(card.typeId()))) {
      throw new DisplayPileException("Cards to discard are not consistent");
    }

    for (var card : cards) {
      availableCards.get(card.typeId()).add(card);
    }
  }

  @Override
  public void refillCards() throws DisplayPileException {
    // do nothing
  }
}
