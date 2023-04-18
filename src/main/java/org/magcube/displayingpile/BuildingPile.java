package org.magcube.displayingpile;

import java.util.Collections;
import java.util.List;
import org.magcube.card.BuildingCard;
import org.magcube.card.CardType;
import org.magcube.exception.DisplayPileException;

public class BuildingPile extends UniqueCardPile<BuildingCard> {

  public BuildingPile(List<BuildingCard> deck) throws DisplayPileException {
    super(CardType.BUILDING, deck);
  }

  @Override
  protected void initPile() {
    deck.addAll(discardPile);
    Collections.shuffle(deck);
    discardPile.clear();
  }

  @Override
  public void discardCards(List<BuildingCard> cards) throws DisplayPileException {
    throw new DisplayPileException("BuildingPile does not support discardCards");
  }

  @Override
  public void refillCards() throws DisplayPileException {
    throw new DisplayPileException("BuildingPile does not support refillCards");
  }
}
