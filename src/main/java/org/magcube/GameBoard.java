package org.magcube;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.magcube.card.Card;
import org.magcube.card.Building;
import org.magcube.card.BasicResource;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

public class GameBoard {

  @Getter
  private final DisplayingPile<BasicResource> basicResourcesPile; //row A
  @Getter
  private final DisplayingPile<Building> factoriesPile; // row D

  public GameBoard() throws DisplayPileException {
    this.basicResourcesPile = new DisplayingPile<>(Main.BASIC_RESOURCES);
    this.factoriesPile = new DisplayingPile<>(Main.factories);
  }

  public List<ArrayList<BasicResource>> getDisplayingBasicResources() {
    return basicResourcesPile.getDisplaying();
  }

  public List<ArrayList<Building>> getDisplayingFactories() {
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
        if (card instanceof BasicResource) {
          basicResourcesPile.takeCard((BasicResource) card);
        } else if (card instanceof Building) {
          factoriesPile.takeCard((Building) card);
        }
      });
    } else {
      throw new DisplayPileException("take cards request invalid!");
    }
  }

  public void putCards(List<Card> cards) {
    cards.forEach(card -> {
      if (card instanceof BasicResource) {
        basicResourcesPile.discardCard((BasicResource) card);
      } else if (card instanceof Building){
        factoriesPile.discardCard((Building) card);
      }
    });
  }

  public void refillCards() throws DisplayPileException {
    basicResourcesPile.refillCards();
    factoriesPile.refillCards();
  }
}
