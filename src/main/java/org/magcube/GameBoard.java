package org.magcube;

import java.util.List;
import lombok.Getter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.ResourceCard;
import org.magcube.displayingpile.BasicResourceDisplayingPile;
import org.magcube.displayingpile.BuildingPile;
import org.magcube.displayingpile.DisplayingPile;
import org.magcube.displayingpile.Level1ResourcePile;
import org.magcube.displayingpile.Level2ResourcePile;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

@Getter
public class GameBoard {

  private final DisplayingPile<ResourceCard> basicResourcesPile;
  private final DisplayingPile<ResourceCard> level1ResourcesPile;
  private final DisplayingPile<ResourceCard> level2ResourcesPile;
  private final DisplayingPile<BuildingCard> buildingPile;

  public GameBoard(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var deck = CardDeck.get(numOfPlayers);
    basicResourcesPile = new BasicResourceDisplayingPile(deck.basicResource);
    level1ResourcesPile = new Level1ResourcePile(deck.level1Resource);
    level2ResourcesPile = new Level2ResourcePile(deck.level2Resource);
    buildingPile = new BuildingPile(deck.building);
  }

  public List<List<ResourceCard>> getDisplayingBasicResource() {
    return basicResourcesPile.getDisplaying();
  }

  public List<List<ResourceCard>> getDisplayingLevel1Resource() {
    return level1ResourcesPile.getDisplaying();
  }

  public List<List<ResourceCard>> getDisplayingLevel2Resource() {
    return level2ResourcesPile.getDisplaying();
  }

  public List<List<BuildingCard>> getDisplayingBuildings() {
    return buildingPile.getDisplaying();
  }

  public void takeCards(List<Card> cards) throws DisplayPileException {
//    todo
  }

  public void discardCards(List<Card> cards) {
    // todo
  }

  public void refillCards() throws DisplayPileException {
    basicResourcesPile.refillCards();
    level1ResourcesPile.refillCards();
    level2ResourcesPile.refillCards();
  }
}
