package org.magcube.game;

import static org.magcube.enums.NumOfPlayers.fromValue;
import static org.magcube.gameboard.GameBoards.flattenResourceCardsFromCategorizedCards;
import static org.magcube.gameboard.GameBoards.sumOfCardsValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.Getter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.ResourceCard;
import org.magcube.enums.CardType;
import org.magcube.enums.InvalidTradingMsg;
import org.magcube.enums.NumOfPlayers;
import org.magcube.exception.AlreadyTradedOrProducedException;
import org.magcube.exception.BuildingActivationException;
import org.magcube.exception.CardIdentitiesException;
import org.magcube.exception.ExceededMaxNumOfHandException;
import org.magcube.exception.GameEndException;
import org.magcube.exception.GameStartupException;
import org.magcube.exception.InvalidTradingException;
import org.magcube.exception.NotAvailableInGameBoardException;
import org.magcube.exception.PlayerDoesNotOwnCardsException;
import org.magcube.gameboard.GameBoard;
import org.magcube.gameboard.GameBoardState;
import org.magcube.gameboard.GameBoards;
import org.magcube.player.Player;

@Getter
public class GameImpl implements Game {

  private GameBoard gameBoard;
  private List<Player> players;
  private Player currentPlayer;
  private Player winner;

  // todo: the following is temp implementation
  public GameImpl() {
  }

  public GameImpl(NumOfPlayers numOfPlayers) {
    players = new ArrayList<>();
    for (var i = 1; i <= numOfPlayers.getValue(); i++) {
      var tempPlayer = new Player(String.valueOf(i), "Player" + i);
      players.add(tempPlayer);
    }
  }

  // todo: the following is temp implementation
  @Override
  public void setPlayers(NumOfPlayers numOfPlayers) {
    players = new ArrayList<>();
    for (var i = 1; i <= numOfPlayers.getValue(); i++) {
      var tempPlayer = new Player(String.valueOf(i), "Player" + i);
      players.add(tempPlayer);
    }
  }

  @Override
  public void startGame() throws GameStartupException {
    if (players == null || players.isEmpty() || !players.stream().allMatch(player ->
        player.getResources().isEmpty() && player.getBuildings().isEmpty() && player.points() == 0)
    ) {
      throw new GameStartupException();
    }
    try {
      gameBoard = new GameBoard(fromValue(players.size()));
    } catch (IllegalArgumentException ex) {
      throw new GameStartupException("failed to create game board with given argument:" + players.size() + ex.getMessage());
    }
    Collections.shuffle(players);
    players = Collections.unmodifiableList(players);
    distributeCoin();
    currentPlayer = players.get(0);
    currentPlayer.setTradedOrPlayerProduced(false);
  }

  @Override
  public GameBoardState gameBoardState() {
    return gameBoard.gameBoardState();
  }

  @Override
  public void tradeCardsByCoins(List<CardIdentity> targets)
      throws AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, NotAvailableInGameBoardException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    checkIsTradedOrPlayerProduced();
    validateCardIdentities(targets);
    checkWillExceedMaxNumOfResourceCard(targets);
    var availableCardsInGameBoard = availableCardsInGameBoard(targets);
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (currentPlayer.getCoin() < sumOfTargetsValue) {
      throw new InvalidTradingException(InvalidTradingMsg.PAYMENT_NO_ENOUGH);
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.spendCoin();
    currentPlayer.setTradedOrPlayerProduced(true);
  }

  @Override
  public void tradeCardsByCards(List<CardIdentity> payment, List<CardIdentity> targets)
      throws AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, PlayerDoesNotOwnCardsException, NotAvailableInGameBoardException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    checkIsTradedOrPlayerProduced();

    if (!(payment.size() == 1 || targets.size() == 1)) {
      throw new InvalidTradingException(InvalidTradingMsg.NOT_ONE_TO_N_OR_N_TO_ONE);
    }
    validateCardIdentities(payment);
    validateCardIdentities(targets);
    checkWillExceedMaxNumOfResourceCard(targets, payment);

    var cardsForPayment = playerEquivalentResourcesCards(payment);
    var categorizedCardsForDiscard = GameBoard.validateAndCategorizeDiscardCards(cardsForPayment);
    var availableCardsInGameBoard = availableCardsInGameBoard(targets);

    var sumOfPaymentValue = sumOfCardsValue(cardsForPayment);
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (sumOfPaymentValue < sumOfTargetsValue) {
      throw new InvalidTradingException(InvalidTradingMsg.PAYMENT_NO_ENOUGH);
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.discardCards(cardsForPayment);
    gameBoard.discardCards(categorizedCardsForDiscard);
    currentPlayer.setTradedOrPlayerProduced(true);
  }

  private void produceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> costMatchFn)
      throws CardIdentitiesException, PlayerDoesNotOwnCardsException, NotAvailableInGameBoardException, InvalidTradingException {
    validateCardIdentities(costCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));

    // currently, it is impossible to throw
    // because the cost size is always larger than or equal to 1 and the product size is always equal to 1
    // checkWillExceedMaxNumOfResourceCard(List.of(productCardIdentity), costCardIdentities);

    var playerEquivalentResources = playerEquivalentResourcesCards(costCardIdentities);
    var categorizedCardsForDiscard = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);
    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.cardType()).get(0);

    var costMatch = costMatchFn.test(costCardIdentities, productCard);
    if (!costMatch) {
      throw new InvalidTradingException(InvalidTradingMsg.COST_NOT_MATCH);
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizedCardsForDiscard);
  }

  @Override
  public void playerProduceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity)
      throws AlreadyTradedOrProducedException, NotAvailableInGameBoardException, PlayerDoesNotOwnCardsException, CardIdentitiesException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCardIds, productCard) -> productCard.cardType() == CardType.BUILDING
        ? ((BuildingCard) productCard).costMatch(costCardIds)
        : ((ResourceCard) productCard).costMatch(costCardIds);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);

    currentPlayer.setTradedOrPlayerProduced(true);
  }

  private void produceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> capitalMatchFn)
      throws ExceededMaxNumOfHandException, CardIdentitiesException, NotAvailableInGameBoardException, InvalidTradingException, PlayerDoesNotOwnCardsException {
    checkWillExceedMaxNumOfResourceCard(1);
    validateCardIdentities(capitalCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));
    checkPlayerOwnResourcesCards(capitalCardIdentities);

    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.cardType()).get(0);

    boolean capitalMatch = capitalMatchFn.test(capitalCardIdentities, productCard);
    if (!capitalMatch) {
      throw new InvalidTradingException(InvalidTradingMsg.CAPITAL_NOT_MATCH);
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
  }

  // todo: we don't need to provide capitalCardIdentities, currently we only have 1 option in capital
  @Override
  public void playerProduceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity)
      throws AlreadyTradedOrProducedException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException, GameEndException, PlayerDoesNotOwnCardsException {
    checkIsGameEnd();
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> capitalMatchFn = (capitalCardIds, productCard) -> productCard.cardType() != CardType.BUILDING
        && ((ResourceCard) productCard).capitalMatch(capitalCardIdentities);

    produceByOwningCapital(capitalCardIdentities, productCardIdentity, capitalMatchFn);
    currentPlayer.setTradedOrPlayerProduced(true);
  }

  private BuildingCard playerEquivalentBuildingCardWhichCanActivate(CardIdentity cardIdentity)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException {
    var playerEquivalentBuildingCard = currentPlayer.equivalentBuilding(cardIdentity);
    if (playerEquivalentBuildingCard == null) {
      throw new PlayerDoesNotOwnCardsException("building");
    }
    if (currentPlayer.getActivatedBuildings().contains(playerEquivalentBuildingCard)) {
      throw new BuildingActivationException(BuildingActivationException.alreadyActivated);
    }
    return playerEquivalentBuildingCard;
  }

  @Override
  public void activateBuildingToGetPointsTokenBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws CardIdentitiesException, PlayerDoesNotOwnCardsException, BuildingActivationException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    var building = playerEquivalentBuildingCardWhichCanActivate(buildingCardIdentity);

    validateCardIdentities(costCardIdentities);

    var playerEquivalentResources = playerEquivalentResourcesCards(costCardIdentities);
    var categorizeDiscardCards = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);

    int effectPoints = building.getEffectPoints();
    if (effectPoints <= 0) {
      throw new BuildingActivationException(BuildingActivationException.cannotProducePoint);
    }

    boolean costMatch = building.effectCostMatch(costCardIdentities);
    if (!costMatch) {
      throw new InvalidTradingException(InvalidTradingMsg.COST_NOT_MATCH);
    }

    currentPlayer.activateBuilding(building);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizeDiscardCards);
    currentPlayer.addPointTokens(effectPoints);
  }

  private CardIdentity buildingProduct(BuildingCard card) throws BuildingActivationException {
    CardIdentity effectProduct = card.getEffectProduct();
    if (effectProduct != null) {
      return effectProduct;
    } else {
      throw new BuildingActivationException(BuildingActivationException.cannotProduceProduct);
    }
  }

  @Override
  public void activateBuildingToProduceBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    var building = playerEquivalentBuildingCardWhichCanActivate(buildingCardIdentity);

    var productCardIdentity = buildingProduct(building);

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCards, productCard) -> building.effectCostMatch(costCards);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);
    currentPlayer.activateBuilding(building);
  }

  @Override
  public void activateBuildingToProduceByOwningCapital(CardIdentity buildingCardIdentity, List<CardIdentity> capitalCardIdentities)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException, GameEndException {
    checkIsGameEnd();
    var building = playerEquivalentBuildingCardWhichCanActivate(buildingCardIdentity);

    var productCardIdentity = buildingProduct(building);

    BiPredicate<List<CardIdentity>, Card> capitalMatchFn = (capitalCardIds, productCard) -> building.effectCapitalMatch(capitalCardIds);

    produceByOwningCapital(capitalCardIdentities, productCardIdentity, capitalMatchFn);
    currentPlayer.activateBuilding(building);
  }

  @Override
  public void activateBuildingForSpecialEffect(CardIdentity buildingCardIdentity)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException, GameEndException {
    checkIsGameEnd();
    var building = playerEquivalentBuildingCardWhichCanActivate(buildingCardIdentity);
    System.out.println(building);
    throw new RuntimeException("Not implemented yet!");
  }

  private void playerTakeCardsFormGameBoard(HashMap<CardType, List<? extends Card>> categorizedCards) {
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

  @Override
  public void endTurn() {
    currentPlayer.resetActivatedBuildings();
    currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    gameBoard.refillCards();
    currentPlayer.setTradedOrPlayerProduced(false);

    if (currentPlayer.points() >= 10) {
      winner = currentPlayer;
    }
  }

  private void checkIsGameEnd() throws GameEndException {
    if (winner != null) {
      throw new GameEndException();
    }
  }

  private HashMap<CardType, List<? extends Card>> availableCardsInGameBoard(List<CardIdentity> cardIdentities)
      throws NotAvailableInGameBoardException {
    var optCards = gameBoard.cardsInDisplay(cardIdentities);
    if (optCards.isEmpty()) {
      throw new NotAvailableInGameBoardException();
    }
    return optCards.get();
  }

  private void checkIsTradedOrPlayerProduced() throws AlreadyTradedOrProducedException {
    if (currentPlayer.isTradedOrPlayerProduced()) {
      throw new AlreadyTradedOrProducedException();
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(int take) throws ExceededMaxNumOfHandException {
    if (currentPlayer.willExceedMaxNumOfResourceCard(take)) {
      throw new ExceededMaxNumOfHandException();
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(int take, int discard) throws ExceededMaxNumOfHandException {
    if (currentPlayer.willExceedMaxNumOfResourceCard(take, discard)) {
      throw new ExceededMaxNumOfHandException();
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(List<CardIdentity> cardIdentities) throws ExceededMaxNumOfHandException {
    int take = (int) cardIdentities.stream().filter(cardIdentity -> cardIdentity.cardType() != CardType.BUILDING).count();
    checkWillExceedMaxNumOfResourceCard(take);
  }

  private void checkWillExceedMaxNumOfResourceCard(List<CardIdentity> take, List<CardIdentity> discard) throws ExceededMaxNumOfHandException {
    int takeCount = (int) take.stream().filter(cardIdentity -> cardIdentity.cardType() != CardType.BUILDING).count();
    // building is not allow to discard
    int discardCount = discard.size();
    checkWillExceedMaxNumOfResourceCard(takeCount, discardCount);
  }

  private void validateCardIdentities(List<CardIdentity> cardIdentities) throws CardIdentitiesException {
    if (cardIdentities.isEmpty()) {
      throw new CardIdentitiesException();
    }
    if (!GameBoards.isCardIdentitiesValid(cardIdentities)) {
      throw new CardIdentitiesException();
    }
  }

  private List<ResourceCard> playerEquivalentResourcesCards(List<CardIdentity> payment) throws PlayerDoesNotOwnCardsException {
    var playerEquivalentResourcesCards = currentPlayer.equivalentResources(payment);
    if (playerEquivalentResourcesCards == null) {
      throw new PlayerDoesNotOwnCardsException("resource");
    }
    return playerEquivalentResourcesCards;
  }

  private void checkPlayerOwnResourcesCards(List<CardIdentity> payment) throws PlayerDoesNotOwnCardsException {
    if (currentPlayer.equivalentResources(payment) == null) {
      throw new PlayerDoesNotOwnCardsException("resource");
    }
  }
}