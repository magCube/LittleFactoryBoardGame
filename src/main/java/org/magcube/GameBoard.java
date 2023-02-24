package org.magcube;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.magcube.card.Card;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.card.Factory;
import org.magcube.card.FirstTierResource;
import org.magcube.card.displayingpile.SingleRowDisplayingPile;
import org.magcube.exception.DisplayPileException;

public class GameBoard {

  @Getter
  private final DisplayingPile<FirstTierResource> firstTierResourcesPile;
  @Getter
  private final SingleRowDisplayingPile<Factory> factoriesPile;

  public GameBoard() throws DisplayPileException {
    this.firstTierResourcesPile = new DisplayingPile<>(Main.firstTierResources);
    this.factoriesPile = new SingleRowDisplayingPile<>(Main.factories);
  }

  public List<ArrayList<FirstTierResource>> getDisplayingFirstTierResources()
      throws DisplayPileException {
    return firstTierResourcesPile.getDisplaying();
  }

  public List<Factory> getDisplayingFactories() {
    return factoriesPile.getSingleRowDisplaying();
  }

  public boolean takeCards(List<Card> cards) {
    return true;
  }

  public boolean putCards(List<Card> cards) {
    return true;
  }
}
