package org.magcube;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.magcube.card.Card;
import org.magcube.card.Factory;
import org.magcube.card.BasicResource;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

public class GameBoard {

  @Getter
  private final DisplayingPile<BasicResource> firstTierResourcesPile;
  @Getter
  private final DisplayingPile<Factory> factoriesPile;

  public GameBoard() throws DisplayPileException {
    this.firstTierResourcesPile = new DisplayingPile<>(Main.BASIC_RESOURCES);
    this.factoriesPile = new DisplayingPile<>(Main.factories);
  }

  public List<ArrayList<BasicResource>> getDisplayingFirstTierResources() {
    return firstTierResourcesPile.getDisplaying();
  }

  public List<ArrayList<Factory>> getDisplayingFactories() {
    return factoriesPile.getDisplaying();
  }

  public void takeCards(List<Card> cards) throws DisplayPileException {
    var firstTierResourcesPileDisplaying = firstTierResourcesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var factoriesPileDisplaying = factoriesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var isValidRequest = cards.stream().allMatch((card) ->
        firstTierResourcesPileDisplaying.contains(card) || factoriesPileDisplaying.contains(card));
    if (isValidRequest) {
      cards.forEach(card -> {
        if (card instanceof BasicResource) {
          firstTierResourcesPile.takeCard((BasicResource) card);
        } else if (card instanceof Factory) {
          factoriesPile.takeCard((Factory) card);
        }
      });
    } else {
      throw new DisplayPileException("take cards request invalid!");
    }
  }

  public void putCards(List<Card> cards) {
    cards.forEach(card -> {
      if (card instanceof BasicResource) {
        firstTierResourcesPile.insertCard((BasicResource) card);
      } else if (card instanceof Factory){
        factoriesPile.insertCard((Factory) card);
      }
    });
  }
}
