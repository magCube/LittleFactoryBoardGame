package org.magcube;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.ResourceCard;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.NumOfPlayersException;

@Getter
public class GameBoard {

  private final DisplayingPile<ResourceCard> basicResourcesPile;
  private final DisplayingPile<ResourceCard> level1ResourcesPile;
  private final DisplayingPile<ResourceCard> level2ResourcesPile;
  private final DisplayingPile<BuildingCard> buildingPile;

  public GameBoard(int numOfPlayers) throws DisplayPileException, NumOfPlayersException {
    var deck = CardDeck.get(numOfPlayers);
    basicResourcesPile = new DisplayingPile<>(deck.basicResource);
    level1ResourcesPile = new DisplayingPile<>(deck.level1Resource);
    level2ResourcesPile = new DisplayingPile<>(deck.level2Resource);
    buildingPile = new DisplayingPile<>(deck.building);
  }

  public List<ArrayList<ResourceCard>> getDisplayingBasicResource() {
    return basicResourcesPile.getDisplaying();
  }

  public List<ArrayList<ResourceCard>> getDisplayingLevel1Resource() {
    return level1ResourcesPile.getDisplaying();
  }

  public List<ArrayList<ResourceCard>> getDisplayingLevel2Resource() {
    return level2ResourcesPile.getDisplaying();
  }

  public List<ArrayList<BuildingCard>> getDisplayingBuildings() {
    return buildingPile.getDisplaying();
  }

  public void takeCards(List<Card> cards) throws DisplayPileException {
    var basicResourcesPileDisplaying = basicResourcesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var level1ResourcesPileDisplaying = level1ResourcesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var level2ResourcesPileDisplaying = level2ResourcesPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();
    var buildingPileDisplaying = buildingPile.getDisplaying().stream()
        .flatMap(List::stream)
        .toList();

    // todo:
    // 1. the checking of whether a card is available can be a method in DisplayingPile class
    // 2. should not compare the reference of card, but the cardType and typeId of card
    // 3. the following checking will have bug when cards contains duplicate card
    @SuppressWarnings("SuspiciousMethodCalls")
    var isValidRequest = cards.stream()
        .allMatch((card) ->
            basicResourcesPileDisplaying.contains(card) ||
                level1ResourcesPileDisplaying.contains(card) ||
                level2ResourcesPileDisplaying.contains(card) ||
                buildingPileDisplaying.contains(card));
    if (isValidRequest) {
      cards.forEach(card -> {
        switch (card.getCardType()) {
          case BASIC_RESOURCE -> basicResourcesPile.takeCard((ResourceCard) card);
          case LEVEL_1_RESOURCE -> level1ResourcesPile.takeCard((ResourceCard) card);
          case LEVEL_2_RESOURCE -> level2ResourcesPile.takeCard((ResourceCard) card);
          case BUILDING -> buildingPile.takeCard((BuildingCard) card);
        }
      });
    } else {
      throw new DisplayPileException("take cards request invalid!");
    }
  }

  public void discardCards(List<Card> cards) {
    cards.forEach(card -> {
      try {
        switch (card.getCardType()) {
          case BASIC_RESOURCE -> basicResourcesPile.discardCard((ResourceCard) card);
          case LEVEL_1_RESOURCE -> level1ResourcesPile.discardCard((ResourceCard) card);
          case LEVEL_2_RESOURCE -> level2ResourcesPile.discardCard((ResourceCard) card);
          case BUILDING -> buildingPile.discardCard((BuildingCard) card);
        }
      } catch (DisplayPileException e) {
        // this one should never happen
        throw new RuntimeException(e);
      }
    });
  }

  public void refillCards() throws DisplayPileException {
    basicResourcesPile.refillCards();
    level1ResourcesPile.refillCards();
    level2ResourcesPile.refillCards();
    buildingPile.refillCards();
  }
}
