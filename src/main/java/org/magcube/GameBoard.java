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
import org.magcube.displayingpile.LevelOneResourcePile;
import org.magcube.displayingpile.LevelTwoResourcePile;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

@Getter
public class GameBoard {

  private final DisplayingPile<ResourceCard> basicResourcesPile;
  private final DisplayingPile<ResourceCard> levelOneResourcesPile;
  private final DisplayingPile<ResourceCard> levelTwoResourcesPile;
  private final DisplayingPile<BuildingCard> buildingPile;

  public GameBoard(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var deck = CardDeck.get(numOfPlayers);
    basicResourcesPile = new BasicResourceDisplayingPile(deck.basicResource);
    levelOneResourcesPile = new LevelOneResourcePile(deck.levelOneResource);
    levelTwoResourcesPile = new LevelTwoResourcePile(deck.levelTwoResource);
    buildingPile = new BuildingPile(deck.building);
  }

  public List<List<ResourceCard>> getDisplayingBasicResource() {
    return basicResourcesPile.getDisplaying();
  }

  public List<List<ResourceCard>> getDisplayingLevel1Resource() {
    return levelOneResourcesPile.getDisplaying();
  }

  public List<List<ResourceCard>> getDisplayingLevel2Resource() {
    return levelTwoResourcesPile.getDisplaying();
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
    levelOneResourcesPile.refillCards();
    levelTwoResourcesPile.refillCards();
  }
}
