package org.magcube.gameboard;

import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.displayingpile.BasicResourceDisplayingPile;
import org.magcube.displayingpile.BuildingPile;
import org.magcube.displayingpile.DisplayingPile;
import org.magcube.displayingpile.LevelOneResourcePile;
import org.magcube.displayingpile.LevelTwoResourcePile;
import org.magcube.displayingpile.PileState;
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

  public PileState<? extends Card> getPileState(CardType cardType) {
    DisplayingPile<? extends Card> pile = (cardType == CardType.BUILDING) ? getBuildingPile() : getResourcePile(cardType);
    return pile.pileState();
  }

  public GameBoardState gameBoardState() {
    return new GameBoardState(
        basicResourcesPile.pileState(),
        levelOneResourcesPile.pileState(),
        levelTwoResourcesPile.pileState(),
        buildingPile.pileState());
  }

  @Nullable
  public HashMap<CardType, List<? extends Card>> cardsInDisplay(List<CardIdentity> cardIdentities) throws DisplayPileException {
    var isValidate = GameBoards.isCardIdentitiesValid(cardIdentities);
    if (!isValidate) {
      throw new DisplayPileException("Card identities are not valid");
    }
    var categorizedCardIdentities = GameBoards.categorizeCardIdentities(cardIdentities);
    var categorizedCards = new HashMap<CardType, List<? extends Card>>();
    for (var entry : categorizedCardIdentities.entrySet()) {
      var cardType = entry.getKey();
      DisplayingPile<? extends Card> pile = cardType == CardType.BUILDING ? getBuildingPile() : getResourcePile(cardType);
      var cards = pile.cardsInDisplay(entry.getValue());
      if (cards == null) {
        return null;
      }
      categorizedCards.put(cardType, cards);
    }
    return categorizedCards;
  }

  public HashMap<CardType, List<? extends Card>> takeCards(HashMap<CardType, List<? extends Card>> categorizedCards) throws DisplayPileException {
    for (var entry : categorizedCards.entrySet()) {
      var cardType = entry.getKey();
      if (cardType == CardType.BUILDING) {
        var pile = getBuildingPile();
        pile.takeCards((List<BuildingCard>) entry.getValue());
      } else {
        var pile = getResourcePile(cardType);
        pile.takeCards((List<ResourceCard>) entry.getValue());
      }
    }
    return categorizedCards;
  }

  public HashMap<CardType, List<? extends Card>> validateAndCategorizeDiscardCards(List<? extends Card> cards) throws DisplayPileException {
    if (!GameBoards.isCardsValidate(cards)) {
      throw new DisplayPileException("Invalid cards");
    }
    if (!GameBoards.isNoBuildingCards(cards)) {
      throw new DisplayPileException("Cannot discard building cards");
    }

    return GameBoards.categorizeCards(cards);
  }

  public void discardCards(HashMap<CardType, List<ResourceCard>> categorizedCards) throws DisplayPileException {
    for (var entry : categorizedCards.entrySet()) {
      var cardType = entry.getKey();
      var pile = getResourcePile(cardType);
      pile.discardCards(entry.getValue());
    }
  }

  public void refillCards() throws DisplayPileException {
    basicResourcesPile.refillCards();
    levelOneResourcesPile.refillCards();
    levelTwoResourcesPile.refillCards();
  }

  private DisplayingPile<ResourceCard> getResourcePile(CardType cardType) {
    return switch (cardType) {
      case BASIC_RESOURCE -> basicResourcesPile;
      case LEVEL_ONE_RESOURCE -> levelOneResourcesPile;
      case LEVEL_TWO_RESOURCE -> levelTwoResourcesPile;
      default -> null;
    };
  }

  private DisplayingPile<BuildingCard> getBuildingPile() {
    return buildingPile;
  }
}
