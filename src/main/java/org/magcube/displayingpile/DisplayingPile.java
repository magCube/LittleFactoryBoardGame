package org.magcube.displayingpile;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;

public interface DisplayingPile<T extends Card> {

  CardType getCardType();

  List<List<T>> getDisplaying();

  List<T> getDeck();

  List<T> getDiscardPile();

  int getMaxDisplayingSize();

  int deckSize();

  int discardPileSize();

  default PileState<T> pileState() {
    return new PileState<>(getCardType(), getDisplaying(), getDeck(), getDiscardPile());
  }

  @Nullable
  List<T> cardsInDisplay(List<CardIdentity> cardIdentities);

  void takeCards(List<T> cardsInDisplaying);

  void discardCards(List<T> cards);

  void refillCards();

  default boolean isConsistentCardType(T card) {
    return card.cardType() == getCardType();
  }

  default boolean isConsistentCardType(List<T> cards) {
    return cards.stream().allMatch(this::isConsistentCardType);
  }
}
