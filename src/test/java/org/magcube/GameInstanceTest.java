package org.magcube;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.magcube.card.Card;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.exception.NumOfPlayersException;
import org.magcube.player.Player;

public class GameInstanceTest {

  @Test
  void setPlayersTest() throws DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.FOUR);
    assertEquals(4, gameInstance.getPlayers().size());
    assertNotNull(gameInstance.getGameBoard());
  }

  @Test
  void startGameNormally() throws DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.FOUR);
    assertDoesNotThrow(gameInstance::startGame);
  }

  @Test
  void startGameWithoutInitializePlayersShouldThrowException() {
    var gameInstance = new GameInstance();
    assertThrows(GameStartupException.class, gameInstance::startGame);
  }

  @Test
  void playersAreUnmodifiableAfterStartGame()
      throws GameStartupException, DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.FOUR);
    gameInstance.startGame();
    var resultList = gameInstance.getPlayers();
    assertThrows(UnsupportedOperationException.class,
        () -> resultList.add(new Player("1", "player1")));
  }

  @Test
  void playersWereDistributedWithCorrectAmountOfCoinsAfterStartGame()
      throws GameStartupException, DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.FOUR);
    gameInstance.startGame();
    var resultList = gameInstance.getPlayers();
    var expectedCoins = 3;
    for (var player : resultList) {
      assertEquals(expectedCoins++, player.getCoin());
    }
  }

  @Test
  void haveCorrectCardsDisplayingAfterStartGame()
      throws GameStartupException, DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.FOUR);
    gameInstance.startGame();
    var gameBoard = gameInstance.getGameBoard();
    assertFalse(gameBoard.getDisplayingBasicResource().isEmpty());
    assertFalse(gameBoard.getDisplayingLevel1Resource().isEmpty());
    assertFalse(gameBoard.getDisplayingLevel2Resource().isEmpty());
    assertFalse(gameBoard.getDisplayingBuildings().isEmpty());
  }

  @Test
  void tradeBasicTierResourceByCoinsTest()
      throws DisplayPileException, GameStartupException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.THREE);
    gameInstance.startGame();
    var player = gameInstance.getPlayers().get(0);
    var gameBoard = gameInstance.getGameBoard();
    var displayingBasicResource = gameBoard.getDisplayingBasicResource();
    List<Card> targets = List.of(displayingBasicResource.get(0).get(0),
        displayingBasicResource.get(1).get(0),
        displayingBasicResource.get(2).get(0));

    gameInstance.tradeCardByCoins(targets);
    assertEquals(0, player.getCoin());
    assertTrue(gameInstance.isTraded());
  }

  @Test
  void endTurnTest() throws DisplayPileException, GameStartupException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(NumOfPlayers.TWO);
    gameInstance.startGame();
    var player1 = gameInstance.getPlayers().get(0);
    var player2 = gameInstance.getPlayers().get(1);
    assertEquals(player1, gameInstance.getCurrentPlayer());
    gameInstance.endTurn();
    assertEquals(player2, gameInstance.getCurrentPlayer());
  }
}
