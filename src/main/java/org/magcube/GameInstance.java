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
import org.magcube.exception.AlreadyTradedOrProducedException;
import org.magcube.exception.BuildingActivationException;
import org.magcube.exception.CardIdentitiesException;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.ExceededMaxNumOfHandException;
import org.magcube.exception.GameStartupException;
import org.magcube.exception.InvalidTradingException;
import org.magcube.exception.NotAvailableInGameBoardException;
import org.magcube.exception.PlayerDoesNotOwnCardsException;
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
    players = new ArrayList<>();
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

  private HashMap<CardType, List<? extends Card>> availableCardsInGameBoard(List<CardIdentity> cardIdentities)
      throws DisplayPileException, NotAvailableInGameBoardException {
    var cards = gameBoard.cardsInDisplay(cardIdentities);
    if (cards == null) {
      throw new NotAvailableInGameBoardException();
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

  private void checkIsTradedOrPlayerProduced() throws AlreadyTradedOrProducedException {
    if (isTradedOrPlayerProduced) {
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

  private void checkWillExceedMaxNumOfResourceCard(CardIdentity cardIdentity) throws ExceededMaxNumOfHandException {
    if (cardIdentity.getCardType() != CardType.BUILDING) {
      checkWillExceedMaxNumOfResourceCard(1);
    }
  }

  private void checkWillExceedMaxNumOfResourceCard(List<CardIdentity> cardIdentities) throws ExceededMaxNumOfHandException {
    int take = (int) cardIdentities.stream().filter(cardIdentity -> cardIdentity.getCardType() != CardType.BUILDING).count();
    checkWillExceedMaxNumOfResourceCard(take);
  }

  private void checkWillExceedMaxNumOfResourceCard(List<CardIdentity> take, List<CardIdentity> discard) throws ExceededMaxNumOfHandException {
    int takeCount = (int) take.stream().filter(cardIdentity -> cardIdentity.getCardType() != CardType.BUILDING).count();
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

  private void checkPlayerOwnResourcesCards(List<CardIdentity> payment) throws DisplayPileException {
    if (currentPlayer.equivalentResources(payment) == null) {
      throw new DisplayPileException("Player do not own the payment cards!");
    }
  }

  public void tradeCardsByCoins(List<CardIdentity> targets)
      throws DisplayPileException, AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, NotAvailableInGameBoardException, InvalidTradingException {
    checkIsTradedOrPlayerProduced();
    validateCardIdentities(targets);
    checkWillExceedMaxNumOfResourceCard(targets);
    var availableCardsInGameBoard = availableCardsInGameBoard(targets);
    var sumOfTargetsValue = sumOfCardsValue(availableCardsInGameBoard);
    if (currentPlayer.getCoin() < sumOfTargetsValue) {
      throw new InvalidTradingException(InvalidTradingException.paymentNoEnough);
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.spendCoin();
    isTradedOrPlayerProduced = true;
  }

  public void tradeCardsByCards(List<CardIdentity> payment, List<CardIdentity> targets)
      throws DisplayPileException, AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, PlayerDoesNotOwnCardsException, NotAvailableInGameBoardException, InvalidTradingException {
    checkIsTradedOrPlayerProduced();

    if (!(payment.size() == 1 || targets.size() == 1)) {
      throw new InvalidTradingException(InvalidTradingException.notOneToNOrNToOne);
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
      throw new InvalidTradingException(InvalidTradingException.paymentNoEnough);
    }
    playerTakeCardsFormGameBoard(availableCardsInGameBoard);
    currentPlayer.discardCards(cardsForPayment);
    gameBoard.discardCards(categorizedCardsForDiscard);
    isTradedOrPlayerProduced = true;
  }

  private List<ResourceCard> flattenResourceCardsFromCategorizedCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    var resourceCards = new ArrayList<ResourceCard>();
    for (var cardType : categorizedCards.keySet()) {
      if (cardType != CardType.BUILDING) {
        resourceCards.addAll(categorizedCards.get(cardType).stream()
            .map(card -> (ResourceCard) card)
            .toList());
      }
    }
    return resourceCards;
  }

  private void produceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> costMatchFn)
      throws DisplayPileException, CardIdentitiesException, ExceededMaxNumOfHandException, PlayerDoesNotOwnCardsException, NotAvailableInGameBoardException, InvalidTradingException {
    validateCardIdentities(costCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));
    // currently, it is impossible to throw, because the cost size is always larger than or equal to 1 and the product size is always equal to 1
    checkWillExceedMaxNumOfResourceCard(List.of(productCardIdentity), costCardIdentities);

    var playerEquivalentResources = playerEquivalentResourcesCards(costCardIdentities);
    var categorizedCardsForDiscard = GameBoard.validateAndCategorizeDiscardCards(playerEquivalentResources);
    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);

    var costMatch = costMatchFn.test(costCardIdentities, productCard);
    if (!costMatch) {
      throw new InvalidTradingException(InvalidTradingException.costNotMatch);
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
    currentPlayer.discardCards(playerEquivalentResources);
    gameBoard.discardCards(categorizedCardsForDiscard);
  }

  public void playerProduceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity)
      throws DisplayPileException, AlreadyTradedOrProducedException, NotAvailableInGameBoardException, PlayerDoesNotOwnCardsException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException {
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCardIds, productCard) -> productCard.cardType() == CardType.BUILDING
        ? ((BuildingCard) productCard).costMatch(costCardIds)
        : ((ResourceCard) productCard).costMatch(costCardIds);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);

    isTradedOrPlayerProduced = true;
  }

  private void produceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity,
      BiPredicate<List<CardIdentity>, Card> capitalMatchFn)
      throws DisplayPileException, ExceededMaxNumOfHandException, CardIdentitiesException, NotAvailableInGameBoardException, InvalidTradingException {
    checkWillExceedMaxNumOfResourceCard(1);
    validateCardIdentities(capitalCardIdentities);
    validateCardIdentities(List.of(productCardIdentity));
    checkPlayerOwnResourcesCards(capitalCardIdentities);

    var cardsInGameBoard = availableCardsInGameBoard(List.of(productCardIdentity));

    var productCard = cardsInGameBoard.get(productCardIdentity.getCardType()).get(0);

    boolean capitalMatch = capitalMatchFn.test(capitalCardIdentities, productCard);
    if (!capitalMatch) {
      throw new InvalidTradingException(InvalidTradingException.capitalNotMatch);
    }

    playerTakeCardsFormGameBoard(cardsInGameBoard);
  }

  // todo: we don't need to provide capitalCardIdentities, currently we only have 1 option in capital
  public void playerProduceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity)
      throws DisplayPileException, AlreadyTradedOrProducedException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException {
    checkIsTradedOrPlayerProduced();

    BiPredicate<List<CardIdentity>, Card> capitalMatchFn = (capitalCardIds, productCard) -> productCard.cardType() != CardType.BUILDING
        && ((ResourceCard) productCard).capitalMatch(capitalCardIdentities);

    produceByOwningCapital(capitalCardIdentities, productCardIdentity, capitalMatchFn);
    isTradedOrPlayerProduced = true;
  }

  private void checkBuildingCanActivate(BuildingCard building) throws BuildingActivationException {
    if (currentPlayer.getActivatedBuildings().contains(building)) {
      throw new BuildingActivationException();
    }
  }

  private BuildingCard playerEquivalentBuildingCard(CardIdentity cardIdentity) throws PlayerDoesNotOwnCardsException {
    var playerEquivalentBuildingCard = currentPlayer.equivalentBuilding(cardIdentity);
    if (playerEquivalentBuildingCard == null) {
      throw new PlayerDoesNotOwnCardsException("building");
    }
    return playerEquivalentBuildingCard;
  }

  public void activateBuildingToGetPointsTokenBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws DisplayPileException, CardIdentitiesException, PlayerDoesNotOwnCardsException, BuildingActivationException {
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
      throws DisplayPileException, PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException {
    var building = playerEquivalentBuildingCard(buildingCardIdentity);
    checkBuildingCanActivate(building);
    var productCardIdentity = buildingProduct(building);

    BiPredicate<List<CardIdentity>, Card> costMatchFn = (costCards, productCard) -> building.effectCostMatch(costCards);

    produceBySpentCost(costCardIdentities, productCardIdentity, costMatchFn);
    currentPlayer.activateBuildings(building);
  }

  public void activateBuildingToProduceByOwningCapital(CardIdentity buildingCardIdentity, List<CardIdentity> capitalCardIdentities)
      throws DisplayPileException, PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException {
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