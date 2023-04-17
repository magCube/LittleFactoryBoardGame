package org.magcube.displayingpile;

import java.util.List;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.exception.DisplayPileException;

public interface IDisplayingPile<T extends Card> {

  CardType getCardType();

  List<List<T>> getDisplaying();

  List<T> getDeck();

  List<T> getDiscardPile();

  int getMaxDisplayingSize();

  int deckSize();

  int discardPileSize();

  List<T> takeCards(List<CardIdentity> cardIdentities) throws DisplayPileException;

  void discardCards(List<T> cards) throws DisplayPileException;

  void refillCards() throws DisplayPileException;

  default boolean consistentCardTypeInCardIdentity(CardIdentity cardIdentity) {
    return cardIdentity.getCardType() == getCardType();
  }

  default boolean consistentCardTypeInCardIdentity(List<CardIdentity> cardIdentities) {
    return cardIdentities.stream().allMatch(this::consistentCardTypeInCardIdentity);
  }

  default boolean consistentCardType(T card) {
    return card.cardType() == getCardType();
  }

  default boolean consistentCardType(List<T> cards) {
    return cards.stream().allMatch(this::consistentCardType);
  }
}
