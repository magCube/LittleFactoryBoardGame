package org.magcube.game;

import java.util.List;
import org.magcube.card.CardIdentity;
import org.magcube.exception.AlreadyTradedOrProducedException;
import org.magcube.exception.BuildingActivationException;
import org.magcube.exception.CardIdentitiesException;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.ExceededMaxNumOfHandException;
import org.magcube.exception.GameEndException;
import org.magcube.exception.GameStartupException;
import org.magcube.exception.InvalidTradingException;
import org.magcube.exception.NotAvailableInGameBoardException;
import org.magcube.exception.PlayerDoesNotOwnCardsException;
import org.magcube.gameboard.GameBoard;
import org.magcube.gameboard.GameBoardState;
import org.magcube.player.NumOfPlayers;
import org.magcube.player.Player;

public interface Game {

  GameBoard getGameBoard();

  List<Player> getPlayers();

  Player getCurrentPlayer();

  Player getWinner();

  void setPlayers(NumOfPlayers numOfPlayers);

  void startGame() throws GameStartupException;

  GameBoardState gameBoardState();

  void tradeCardsByCoins(List<CardIdentity> targets)
      throws AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, NotAvailableInGameBoardException, InvalidTradingException, GameEndException;

  void tradeCardsByCards(List<CardIdentity> payment, List<CardIdentity> targets)
      throws AlreadyTradedOrProducedException, CardIdentitiesException, ExceededMaxNumOfHandException, PlayerDoesNotOwnCardsException, NotAvailableInGameBoardException, InvalidTradingException, GameEndException;

  void playerProduceBySpentCost(List<CardIdentity> costCardIdentities, CardIdentity productCardIdentity)
      throws AlreadyTradedOrProducedException, NotAvailableInGameBoardException, PlayerDoesNotOwnCardsException, CardIdentitiesException, InvalidTradingException, GameEndException;

  void playerProduceByOwningCapital(List<CardIdentity> capitalCardIdentities, CardIdentity productCardIdentity)
      throws DisplayPileException, AlreadyTradedOrProducedException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException, GameEndException;

  void activateBuildingToGetPointsTokenBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws CardIdentitiesException, PlayerDoesNotOwnCardsException, BuildingActivationException, InvalidTradingException, GameEndException;

  void activateBuildingToProduceBySpendCost(CardIdentity buildingCardIdentity, List<CardIdentity> costCardIdentities)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, InvalidTradingException, GameEndException;

  void activateBuildingToProduceByOwningCapital(CardIdentity buildingCardIdentity, List<CardIdentity> capitalCardIdentities)
      throws DisplayPileException, PlayerDoesNotOwnCardsException, BuildingActivationException, NotAvailableInGameBoardException, CardIdentitiesException, ExceededMaxNumOfHandException, InvalidTradingException, GameEndException;

  void activateBuildingForSpecialEffect(CardIdentity buildingCardIdentity)
      throws PlayerDoesNotOwnCardsException, BuildingActivationException, GameEndException;

  void endTurn() throws DisplayPileException;
}
