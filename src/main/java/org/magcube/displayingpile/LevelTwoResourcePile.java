package org.magcube.displayingpile;

import java.util.Collections;
import java.util.List;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class LevelTwoResourcePile extends UniqueCardPile<ResourceCard> {

  public LevelTwoResourcePile(List<ResourceCard> deck) {
    super(CardType.LEVEL_TWO_RESOURCE, deck);
    refillCards();
  }

  @Override
  public void discardCards(List<ResourceCard> cards) {
    this.discardPile.addAll(cards);
  }

  @Override
  public void refillCards() {
    fillDeckWithDiscardPileIfDeckUsedUp();
    var nullIndexes = nullIndexesInAvailableCards();
    for (int index : nullIndexes) {
      if (deck.isEmpty()) {
        break;
      }
      var newCard = deck.remove(0);
      availableCards.set(index, newCard);
    }
  }

  private void fillDeckWithDiscardPileIfDeckUsedUp() {
    if (deck.isEmpty() && discardPile.size() > 0) {
      deck.addAll(discardPile);
      Collections.shuffle(deck);
      discardPile.clear();
    }
  }
}
