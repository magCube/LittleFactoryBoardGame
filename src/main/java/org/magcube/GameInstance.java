package org.magcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.Getter;
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
  }

  private HashMap<CardType, List<? extends Card>> availableCardsInGameBoard(List<CardIdentity> cardIdentities) throws DisplayPileException {
    var cards = gameBoard.cardsInDisplay(cardIdentities);
    if (cards == null) {
      throw new DisplayPileException("cards are not available in game board");
    }
    return cards;
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

  private void checkIsTradedOrPlayerProduced() throws DisplayPileException {
    if (isTradedOrPlayerProduced) {
      throw new DisplayPileException("Player already traded or produced.");
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(int add) throws DisplayPileException {
    if (currentPlayer.willExceedMaxNumOfResourceCard(add)) {
      throw new DisplayPileException("Player will exceed max number of resource cards!");
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(CardIdentity cardIdentity) throws DisplayPileException {
    if (cardIdentity.getCardType() != CardType.BUILDING) {
      checkWillExceedMaxNumOfResourceCard(1);
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(List<CardIdentity> cardIdentities) throws DisplayPileException {
    int add = (int) cardIdentities.stream().filter(cardIdentity -> cardIdentity.getCardType() != CardType.BUILDING).count();
    checkWillExceedMaxNumOfResourceCard(add);
  }

  private void validateCardIdentities(List<CardIdentity> cardIdentities) throws DisplayPileException {
    if (!GameBoards.isCardIdentitiesValid(cardIdentities)) {
      throw new DisplayPileException("Invalid card identities!");
    }
  }

  private List<ResourceCard> playerEquivalentResourcesCards(List<CardIdentity> payment) throws DisplayPileException {
    var playerEquivalentResourcesCards = currentPlayer.equivalentResources(payment);
    if (playerEquivalentResourcesCards == null) {
      throw new DisplayPileException("Player do not own the payment cards!");
    }
    return playerEquivalentResourcesCards;
  }

  private void checkPlayerOwnResourcesCards(List<CardIdentity> payment) throws DisplayPileException {
    if (currentPlayer.equivalentResources(payment) == null) {
      throw new DisplayPileException("Player do not own the payment cards!");
    }
  }

  public void tradeCardsByCoins(List<CardIdentity> targets) throws DisplayPileException {
    checkIsTradedOrPlayerProduced();
    validateCardIdentities(targets);
    checkWillExceedMaxNumOfResourceCard(targets);
    var availableCardsInGameBoard = availableCardsInGameBoard(targets);
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (currentPlayer.getCoin() < sumOfTargetsValue) {
      throw new DisplayPileException("Player do not have enough coin to trade!");
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.spendCoin();
    isTradedOrPlayerProduced = true;
  }

  public void tradeCardsByCards(List<CardIdentity> payment, List<CardIdentity> targets) throws DisplayPileException {
    checkIsTradedOrPlayerProduced();

    if (payment.size() != 1 || targets.size() != 1) {
      throw new DisplayPileException("Trading must be 1:n or n:1!");
    }
    validateCardIdentities(payment);
    validateCardIdentities(targets);
    checkWillExceedMaxNumOfResourceCard(targets);

    var cardsForPayment = playerEquivalentResourcesCards(payment);
    var categorizedCardsForDiscard = GameBoard.validateAndCategorizeDiscardCards(cardsForPayment);
    var availableCardsInGameBoard = availableCardsInGameBoard(targets);

    var sumOfPaymentValue = sumOfCardsValue(cardsForPayment);
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (sumOfPaymentValue < sumOfTargetsValue) {
      throw new DisplayPileException("Payment does not have enough value to trade!");
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.discardCards(cardsForPayment);
    gameBoard.discardCards(categorizedCardsForDiscard);
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

  private void produceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> costMatchFn) throws DisplayPileException {
    validateCardIdentities(costCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));
    checkWillExceedMaxNumOfResourceCard(productCardIdentity);

    var playerEquivalentResources = playerEquivalentResourcesCards(costCardIdentities);
    var categorizedCardsForDiscard = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);
    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);

    var costMatch = costMatchFn.test(costCardIdentities, productCard);
    if (!costMatch) {
      throw new DisplayPileException("Cost does not match!");
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizedCardsForDiscard);
  }

  public void playerProduceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity) throws DisplayPileException {
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCardIds, productCard) -> productCard.cardType() == CardType.BUILDING
        ? ((BuildingCard) productCard).costMatch(costCardIds)
        : ((ResourceCard) productCard).costMatch(costCardIds);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);

    isTradedOrPlayerProduced = true;
  }

  private void produceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> capitalMatchFn) throws DisplayPileException {
    checkWillExceedMaxNumOfResourceCard(1);
    validateCardIdentities(capitalCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));
    checkPlayerOwnResourcesCards(capitalCardIdentities);

    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);

    boolean capitalMatch = capitalMatchFn.test(capitalCardIdentities, productCard);
    if (!capitalMatch) {
      throw new DisplayPileException("Capital does not match!");
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
  }

  // todo: we don't need to provide capitalCardIdentities, currently we only have 1 option in capital
  public void playerProduceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity) throws DisplayPileException {
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> capitalMatchFn = (capitalCardIds, productCard) -> productCard.cardType() != CardType.BUILDING
        && ((ResourceCard) productCard).capitalMatch(capitalCardIdentities);

    produceByOwningCapital(capitalCardIdentities, productCardIdentity, capitalMatchFn);
    isTradedOrPlayerProduced = true;
  }

  private void checkBuildingCanActivate(BuildingCard building) throws DisplayPileException {
    if (currentPlayer.getActivatedBuildings().contains(building)) {
      throw new DisplayPileException("Building has already activated!");
    }
  }

  private BuildingCard playerEquivalentBuildingCard(CardIdentity cardIdentity) throws DisplayPileException {
    var playerEquivalentBuildingCard = currentPlayer.equivalentBuilding(cardIdentity);
    if (playerEquivalentBuildingCard == null) {
      throw new DisplayPileException("Player do not own the payment cards!");
    }
    return playerEquivalentBuildingCard;
  }

  public void activateBuildingToGetPointsTokenBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws DisplayPileException {
    var building = playerEquivalentBuildingCard(buildingCardIdentity);
    checkBuildingCanActivate(building);

    validateCardIdentities(costCardIdentities);

    var playerEquivalentResources = playerEquivalentResourcesCards(costCardIdentities);
    var categorizeDiscardCards = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);

    boolean costMatch = building.effectCostMatch(costCardIdentities);
    if (!costMatch) {
      throw new DisplayPileException("Cost does not match!");
    }

    currentPlayer.activateBuildings(building);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizeDiscardCards);
    currentPlayer.addPoints(building.getEffectPoints());
  }

  private CardIdentity buildingProduct(BuildingCard card) throws DisplayPileException {
    CardIdentity effectProduct = card.getEffectProduct();
    if (effectProduct != null) {
      return effectProduct;
    } else {
      throw new DisplayPileException("Building cannot produce!");
    }
  }

  public void activateBuildingToProduceBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws DisplayPileException {
    var building = playerEquivalentBuildingCard(buildingCardIdentity);
    checkBuildingCanActivate(building);
    var productCardIdentity = buildingProduct(building);

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCards, productCard) -> building.effectCostMatch(costCards);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);
    currentPlayer.activateBuildings(building);
  }

  public void activateBuildingToProduceByOwningCapital(CardIdentity buildingCardIdentity, List<CardIdentity> capitalCardIdentities)
      throws DisplayPileException {
    var building = playerEquivalentBuildingCard(buildingCardIdentity);
    checkBuildingCanActivate(building);
    var productCardIdentity = buildingProduct(building);

    BiPredicate<List<CardIdentity>, Card> capitalMatchFn = (capitalCardIds, productCard) -> building.effectCapitalMatch(capitalCardIds);

    produceByOwningCapital(capitalCardIdentities, productCardIdentity, capitalMatchFn);
    currentPlayer.activateBuildings(building);
  }

  public void activateBuildingForSpecialEffect(CardIdentity buildingCardIdentity) {
    throw new RuntimeException("Not implemented yet!");
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

    // todo: check if the current user have enough points to win, if
  }
}