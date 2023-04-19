package org.magcube.displayingpile;

import java.util.Collections;
import java.util.List;
import org.magcube.card.BuildingCard;
import org.magcube.card.CardType;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

public class BuildingPile extends UniqueCardPile<BuildingCard> {

  public BuildingPile(List<BuildingCard> deck, NumOfPlayers numOfPlayers) throws DisplayPileException {
    super(CardType.BUILDING, deck);
    this.deck.addAll(discardPile);
    Collections.shuffle(this.deck);
    discardPile.clear();

    // don't return x directly, coz x.getIsStartingBuilding() can be null
    var startingBuilds = this.deck.stream().filter(x -> Boolean.TRUE.equals(x.getIsStartingBuilding())).toList();
    var maxNumOfStartingBuilds = Math.min(getMaxDisplayingSize(), startingBuilds.size());
    maxNumOfStartingBuilds = Math.min(maxNumOfStartingBuilds, numOfPlayers.getValue() + 1);

    for (int i = 0; i < startingBuilds.size(); i++) {
      var card = startingBuilds.get(i);
      this.deck.remove(card);
      if (i < maxNumOfStartingBuilds) {
        availableCards.set(i, card);
      } else {
        discardPile.add(card);
      }
    }

    refillCards();
  }

  @Override
  public void discardCards(List<BuildingCard> cards) throws DisplayPileException {
    throw new DisplayPileException("BuildingPile does not support discardCards");
  }

  @Override
  public void refillCards() {
    var nullIndexes = nullIndexesInAvailableCards();
    for (int index : nullIndexes) {
      if (deck.isEmpty()) {
        break;
      }
      var newCard = deck.remove(0);
      availableCards.set(index, newCard);
    }
  }
}
