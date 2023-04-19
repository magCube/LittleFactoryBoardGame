package org.magcube.displayingpile;

import java.util.Collections;
import java.util.List;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;

public class LevelTwoResourcePile extends UniqueCardPile<ResourceCard> {

  public LevelTwoResourcePile(List<ResourceCard> deck) throws DisplayPileException {
    super(CardType.LEVEL_TWO_RESOURCE, deck);
  }

  @Override
  protected void initPile() {
    refillCards();
  }

  @Override
  public void discardCards(List<ResourceCard> cards) throws DisplayPileException {
    if (isConsistentCardType(cards) && !haveDuplicatedCards(cards)) {
      this.discardPile.addAll(cards);
    } else {
      // guarded by GameBoard, should not happen in real game
      throw new DisplayPileException("Cards to discard are not consistent");
    }
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
