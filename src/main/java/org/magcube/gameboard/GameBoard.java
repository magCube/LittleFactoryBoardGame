package org.magcube.gameboard;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import org.magcube.exception.CardIdentitiesException;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

public class GameBoard {

  private final DisplayingPile<ResourceCard> basicResourcesPile;
  private final DisplayingPile<ResourceCard> levelOneResourcesPile;
  private final DisplayingPile<ResourceCard> levelTwoResourcesPile;
  private final DisplayingPile<BuildingCard> buildingPile;

  public GameBoard(NumOfPlayers numOfPlayers) {
    var deck = CardDeck.get(numOfPlayers);
    basicResourcesPile = new BasicResourceDisplayingPile(deck.basicResource);
    levelOneResourcesPile = new LevelOneResourcePile(deck.levelOneResource);
    levelTwoResourcesPile = new LevelTwoResourcePile(deck.levelTwoResource);
    buildingPile = new BuildingPile(deck.building, numOfPlayers);
  }

  public PileState<? extends Card> pileState(CardType cardType) {
    var pile = (cardType == CardType.BUILDING) ? getBuildingPile() : getResourcePile(cardType);
    return pile.pileState();
  }

  public GameBoardState gameBoardState() {
    return new GameBoardState(
        basicResourcesPile.pileState(),
        levelOneResourcesPile.pileState(),
        levelTwoResourcesPile.pileState(),
        buildingPile.pileState());
  }

  public Optional<HashMap<CardType, List<? extends Card>>> cardsInDisplay(List<CardIdentity> cardIdentities) {
    var categorizedCardIdentities = GameBoards.categorizeCardIdentities(cardIdentities);
    var categorizedCards = new HashMap<CardType, List<? extends Card>>();
    for (var entry : categorizedCardIdentities.entrySet()) {
      var cardType = entry.getKey();
      var pile = cardType == CardType.BUILDING ? getBuildingPile() : getResourcePile(cardType);
      var optCards = pile.cardsInDisplay(entry.getValue());
      if (optCards.isEmpty()) {
        return Optional.empty();
      }
      categorizedCards.put(cardType, optCards.get());
    }
    return Optional.of(categorizedCards);
  }

  public void takeCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    for (var entry : categorizedCards.entrySet()) {
      var cardType = entry.getKey();
      if (cardType == CardType.BUILDING) {
        var pile = getBuildingPile();
        @SuppressWarnings("unchecked")
        var cards = (List<BuildingCard>) entry.getValue();
        pile.takeCards(cards);
      } else {
        var pile = getResourcePile(cardType);
        @SuppressWarnings("unchecked")
        var cards = (List<ResourceCard>) entry.getValue();
        pile.takeCards(cards);
      }
    }
  }

  public static HashMap<CardType, List<? extends Card>> validateAndCategorizeDiscardCards(List<? extends Card> cards) throws CardIdentitiesException {
    if (!GameBoards.isCardsValidate(cards) || !GameBoards.isNoBuildingCards(cards)) {
      throw new CardIdentitiesException();
    }

    return GameBoards.categorizeCards(cards);
  }

  public void discardCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    for (var entry : categorizedCards.entrySet()) {
      var cardType = entry.getKey();
      var pile = getResourcePile(cardType);
      @SuppressWarnings("unchecked")
      var cards = (List<ResourceCard>) entry.getValue();
      pile.discardCards(cards);
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
