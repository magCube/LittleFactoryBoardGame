package org.magcube;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.ResourceCard;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

public class GameBoard {

  @Getter
  private final DisplayingPile<ResourceCard> basicResourcesPile; //row A
  @Getter
  private final DisplayingPile<BuildingCard> factoriesPile; // row D

  public GameBoard() throws DisplayPileException {
    this.basicResourcesPile = new DisplayingPile<>(Main.BASIC_RESOURCES);
    this.factoriesPile = new DisplayingPile<>(Main.factories);
  }

  public List<ArrayList<ResourceCard>> getDisplayingResourceCards() {
    return basicResourcesPile.getDisplaying();
  }

  public List<ArrayList<BuildingCard>> getDisplayingFactories() {
    return factoriesPile.getDisplaying();
  }

  public void takeCards(List<Card> cards) throws DisplayPileException {
    var firstTierResourcesPileDisplaying = basicResourcesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var factoriesPileDisplaying = factoriesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    @SuppressWarnings("SuspiciousMethodCalls")
    var isValidRequest = cards.stream()
        .allMatch((card) ->
            firstTierResourcesPileDisplaying.contains(card) || factoriesPileDisplaying.contains(
                card));
    if (isValidRequest) {
      cards.forEach(card -> {
        if (card instanceof ResourceCard) {
          basicResourcesPile.takeCard((ResourceCard) card);
        } else if (card instanceof BuildingCard) {
          factoriesPile.takeCard((BuildingCard) card);
        }
      });
    } else {
      throw new DisplayPileException("take cards request invalid!");
    }
  }

  public void putCards(List<Card> cards) {
    cards.forEach(card -> {
      if (card instanceof ResourceCard) {
        try {
          basicResourcesPile.discardCard((ResourceCard) card);
        } catch (DisplayPileException e) {
          throw new RuntimeException(e);
        }
      } else if (card instanceof BuildingCard) {
        try {
          factoriesPile.discardCard((BuildingCard) card);
        } catch (DisplayPileException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public void refillCards() throws DisplayPileException {
    basicResourcesPile.refillCards();
    factoriesPile.refillCards();
  }
}
