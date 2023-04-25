package org.magcube.displayingpile;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.exception.DisplayPileException;

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
  List<T> cardsInDisplay(List<CardIdentity> cardIdentities) throws DisplayPileException;

  @NotNull
  List<T> takeCards(List<T> cardsInDisplaying) throws DisplayPileException;

  void discardCards(List<T> cards) throws DisplayPileException;

  void refillCards() throws DisplayPileException;

  default boolean isConsistentCardTypeInCardIdentity(CardIdentity cardIdentity) {
    return cardIdentity.cardType() == getCardType();
  }

  default boolean isConsistentCardTypeInCardIdentity(List<CardIdentity> cardIdentities) {
    return cardIdentities.stream().allMatch(this::isConsistentCardTypeInCardIdentity);
  }

  default boolean isConsistentCardType(T card) {
    return card.cardType() == getCardType();
  }

  default boolean isConsistentCardType(List<T> cards) {
    return cards.stream().allMatch(this::isConsistentCardType);
  }
}
