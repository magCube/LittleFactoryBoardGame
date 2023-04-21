package org.magcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.gameboard.GameBoard;
import org.magcube.gameboard.GameBoards;
import org.magcube.player.NumOfPlayers;
import org.magcube.player.Player;

@Getter
public class GameInstance {

  private GameBoard gameBoard;
  private List<Player> players;
  private Player currentPlayer; // only accessible by this object itself, need to be thread safe
  private boolean isTradedOrPlayerProduced;

  public GameInstance() {
    players = new ArrayList<>();
  }

  // todo: the following is temp implementation
  public void setPlayers(NumOfPlayers numOfPlayers) throws DisplayPileException {
    players = new ArrayList<Player>();
    for (var i = 1; i <= numOfPlayers.getValue(); i++) {
      var tempPlayer = new Player(String.valueOf(i), "Player" + i);
      players.add(tempPlayer);
    }
    gameBoard = new GameBoard(numOfPlayers);
  }

  public void startGame() throws GameStartupException {
    if (gameBoard == null || players == null || players.isEmpty() ||
        !players.stream().allMatch(player ->
            player.getResources().isEmpty() &&
                player.getBuildings().isEmpty() &&
                player.points() == 0)
    ) {
      throw new GameStartupException();
    }
    Collections.shuffle(players);
    players = Collections.unmodifiableList(players);
    distributeCoin();
    currentPlayer = players.get(0);
    isTradedOrPlayerProduced = false;
//        REPEAT for Each user (in order)
//        currentuser = reference of the user in action
//        availabeFactories = new ArrayList(currentuser.getFactories());
//        isTraded = false
//        wait for actions = actions
//        SWITCH(actions)
//        case TRADE:
//        check isTraded == false
//        according to the target Card type , call the correct trade function
//        1. if trade FirstTier resources, call tradeFirstTierResource(payment, target);
//        2. if trade factory, call tradeFactory(payment, target);
//        isTraded = true
//        case PRODUCE:
//        check the Factory's produces Card, produce accordingly
//        this.produceCard(card)
//        availableFactories.remove(factory)
//
//        case BUILDING_EFFECT:
//          doBuildEffect()
//        case END:
//        check if the current user have enough points to win, if
//        yes: break
//                no: continue
//                END REPEAT
//        display winner
  }

  @Nullable
  public HashMap<CardType, List<? extends Card>> availableCardsInGameBoard(List<CardIdentity> cardIdentities) throws DisplayPileException {
    return gameBoard.cardsInDisplay(cardIdentities);
  }

  private int sumOfCardsValue(List<? extends Card> cards) {
    return cards.stream().map(Card::getValue).reduce(0, Integer::sum);
  }

  private int sumOfCardsValue(HashMap<CardType, List<? extends Card>> categorizedCards) {
    var value = 0;
    for (var cardType : categorizedCards.keySet()) {
      value += categorizedCards.get(cardType).stream().map(Card::getValue).reduce(0, Integer::sum);
    }
    return value;
  }

  public void tradeCardsByCoins(List<CardIdentity> targets) throws DisplayPileException {
    if (isTradedOrPlayerProduced) {
      throw new DisplayPileException("Player already traded!");
    }

    if (!GameBoards.isCardIdentitiesValid(targets)) {
      throw new DisplayPileException("Invalid card identities!");
    }

    if (currentPlayer.willExceedMaxNumOfResourceCard(targets.size())) {
      throw new DisplayPileException("Player will exceed max number of resource cards!");
    }

    var availableCardsInGameBoard = availableCardsInGameBoard(targets);
    if (availableCardsInGameBoard == null) {
      throw new DisplayPileException("Target cards are not available!");
    }
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);

    var playerCoin = currentPlayer.getCoin();
    if (playerCoin < sumOfTargetsValue) {
      throw new DisplayPileException("Player do not have enough coin to trade!");
    }

    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.spendCoin();
    isTradedOrPlayerProduced = true;
  }

  public void tradeCardsByCards(List<CardIdentity> payment, List<CardIdentity> targets) throws DisplayPileException {
    if (isTradedOrPlayerProduced) {
      throw new DisplayPileException("Player already traded!");
    }

    if (payment.size() != 1 || targets.size() != 1) {
      throw new DisplayPileException("Trading must be 1:n or n:1!");
    }

    if (currentPlayer.willExceedMaxNumOfResourceCard(targets.size())) {
      throw new DisplayPileException("Player will exceed max number of resource cards!");
    }

    if (!GameBoards.isCardIdentitiesValid(payment) || !GameBoards.isCardIdentitiesValid(targets)) {
      throw new DisplayPileException("Invalid card identities!");
    }

    var playerEquivalentResources = currentPlayer.equivalentResources(payment);
    if (playerEquivalentResources == null) {
      throw new DisplayPileException("Player do not own the payment cards!");
    }
    var categorizeDiscardCards = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);

    var sumOfPaymentValue = sumOfCardsValue(playerEquivalentResources);

    var availableCardsInGameBoard = availableCardsInGameBoard(targets);
    if (availableCardsInGameBoard == null) {
      throw new DisplayPileException("Target cards are not available!");
    }
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (sumOfPaymentValue < sumOfTargetsValue) {
      throw new DisplayPileException("Payment does not have enough value to trade!");
    }

    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizeDiscardCards);
    isTradedOrPlayerProduced = true;
  }

  private List<ResourceCard> flattenResourceCardsFromCategorizedCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    var resourceCards = new ArrayList<ResourceCard>();
    for (var cardType : categorizedCards.keySet()) {
      if (cardType == CardType.BASIC_RESOURCE ||
          cardType == CardType.LEVEL_ONE_RESOURCE ||
          cardType == CardType.LEVEL_TWO_RESOURCE) {
        if (categorizedCards.containsKey(cardType)) {
          resourceCards.addAll(categorizedCards.get(cardType).stream()
              .map(card -> (ResourceCard) card)
              .toList());
        }
      }
    }
    return resourceCards;
  }

  public void playerProduceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity) throws DisplayPileException {
    if (isTradedOrPlayerProduced) {
      throw new DisplayPileException("Player already produced!");
    }

    if (currentPlayer.willExceedMaxNumOfResourceCard(1)) {
      throw new DisplayPileException("Player will exceed max number of resource cards!");
    }

    if (!GameBoards.isCardIdentitiesValid(costCardIdentities)) {
      throw new DisplayPileException("Invalid card identities!");
    }

    var playerEquivalentResources = currentPlayer.equivalentResources(costCardIdentities);
    if (playerEquivalentResources == null) {
      throw new DisplayPileException("Player do not own the cost cards!");
    }
    var categorizeDiscardCards = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);

    var cardsInGameBoard = gameBoard.cardsInDisplay(List.of(productCardIdentity));
    if (cardsInGameBoard == null) {
      throw new DisplayPileException("Target card is not available!");
    }

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);
    boolean costMatch = productCard.cardType() == CardType.BUILDING
        ? ((BuildingCard) productCard).costMatch(costCardIdentities)
        : ((ResourceCard) productCard).costMatch(costCardIdentities);
    if (!costMatch) {
      throw new DisplayPileException("Cost does not match!");
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizeDiscardCards);
  }

  public void playerProduceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity) throws DisplayPileException {
    if (isTradedOrPlayerProduced) {
      throw new DisplayPileException("Player already produced!");
    }

    if (currentPlayer.willExceedMaxNumOfResourceCard(1)) {
      throw new DisplayPileException("Player will exceed max number of resource cards!");
    }

    if (!GameBoards.isCardIdentitiesValid(capitalCardIdentities)) {
      throw new DisplayPileException("Invalid card identities!");
    }

    var playerEquivalentResources = currentPlayer.equivalentResources(capitalCardIdentities);
    if (playerEquivalentResources == null) {
      throw new DisplayPileException("Player do not own the capital cards!");
    }

    var cardsInGameBoard = gameBoard.cardsInDisplay(List.of(productCardIdentity));
    if (cardsInGameBoard == null) {
      throw new DisplayPileException("Target card is not available!");
    }

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);
    boolean capitalMatch = productCard.cardType() != CardType.BUILDING && ((ResourceCard) productCard).capitalMatch(capitalCardIdentities);
    if (!capitalMatch) {
      throw new DisplayPileException("Cost does not match!");
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
  }

  private void playerTakeCardsFormGameBoard(HashMap<CardType, List<? extends Card>> categorizedCards) throws DisplayPileException {
    gameBoard.takeCards(categorizedCards);

    var targetResourceCards = flattenResourceCardsFromCategorizedCards(categorizedCards);
    if (targetResourceCards.size() > 0) {
      currentPlayer.takeResourceCards(targetResourceCards);
    }

    if (categorizedCards.containsKey(CardType.BUILDING)) {
      var targetBuildingCards = categorizedCards.get(CardType.BUILDING).stream()
          .map(card -> (BuildingCard) card)
          .toList();
      currentPlayer.takeBuildingCards(targetBuildingCards);
    }
  }

  public void activateBuilding(BuildingCard card, List<ResourceCard> effectCost, List<ResourceCard> effectCapital) {
    // todo: implement
  }

  private void distributeCoin() {
    var coins = 3;
    for (var player : players) {
      player.receiveCoin(coins++);
    }
  }

  public void endTurn() throws DisplayPileException {
    currentPlayer.resetActivatedBuildings();
    currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    gameBoard.refillCards();
    isTradedOrPlayerProduced = false;
  }
}
