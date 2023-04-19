package org.magcube.gameboard;

import java.util.List;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.displayingpile.BasicResourceDisplayingPile;
import org.magcube.displayingpile.BuildingPile;
import org.magcube.displayingpile.DisplayingPile;
import org.magcube.displayingpile.LevelOneResourcePile;
import org.magcube.displayingpile.LevelTwoResourcePile;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

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
    buildingPile = new BuildingPile(deck.building, numOfPlayers);
  }

  public <T extends Card> PileState<T> getPileState(CardType cardType) {
    @SuppressWarnings("unchecked")
    DisplayingPile<T> pile = (DisplayingPile<T>) getPile(cardType);
    return new PileState<>(cardType, pile.getDisplaying(), pile.getDeck(), pile.getDiscardPile());
  }

  public GameBoardState getGameBoardState() {
    return new GameBoardState(
        getPileState(CardType.BASIC_RESOURCE),
        getPileState(CardType.LEVEL_ONE_RESOURCE),
        getPileState(CardType.LEVEL_TWO_RESOURCE),
        getPileState(CardType.BUILDING));
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

  private DisplayingPile<? extends Card> getPile(CardType cardType) {
    return switch (cardType) {
      case BASIC_RESOURCE -> basicResourcesPile;
      case LEVEL_ONE_RESOURCE -> levelOneResourcesPile;
      case LEVEL_TWO_RESOURCE -> levelTwoResourcesPile;
      case BUILDING -> buildingPile;
    };
  }
}
