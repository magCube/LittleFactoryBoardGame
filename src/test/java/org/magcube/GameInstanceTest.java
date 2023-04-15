package org.magcube;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
  void setPlayersTest() throws DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(5);
    var users = gameInstance.getPlayers();
    assertEquals(5, users.size());
  }

  @Test
  void startGameNormally() throws DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(6);
    assertDoesNotThrow(gameInstance::startGame);
  }

  @Test
  void startGameWithoutInitializeUsersShouldThrowException()
      throws DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    assertThrows(GameStartupException.class, gameInstance::startGame);
  }

  @Test
  void usersAreUnmodifiableAfterStartGame()
      throws GameStartupException, DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(6);
    gameInstance.startGame();
    var resultList = gameInstance.getPlayers();
    assertThrows(UnsupportedOperationException.class,
        () -> resultList.add(new Player("1", "player1")));
  }

  @Test
  void usersWereDistributedWithCorrectAmountOfCoinsAfterStartGame()
      throws GameStartupException, DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(6);
    gameInstance.startGame();
    var resultList = gameInstance.getPlayers();
    var expectedCoins = 3;
    for (var user : resultList) {
      assertEquals(expectedCoins++, user.getCoin());
    }
  }

  @Test
  void haveCorrectCardsDisplayingAfterStartGame()
      throws GameStartupException, DisplayPileException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(6);
    gameInstance.startGame();
    var gameBoard = gameInstance.getGameBoard();
    assertFalse(gameBoard.getDisplayingBasicResource().isEmpty());
    assertFalse(gameBoard.getDisplayingBuildings().isEmpty());
  }

  @Test
  void tradeFirstTierResourceTest()
      throws DisplayPileException, GameStartupException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(3);
    gameInstance.startGame();
    var user = gameInstance.getPlayers().get(0);
    var coins = user.getCoin();
    var gameBoard = gameInstance.getGameBoard();
    var displayingFirstTiers = gameBoard.getDisplayingBasicResource();
    List<Card> targets = List.of(displayingFirstTiers.get(0).get(0),
        displayingFirstTiers.get(1).get(0),
        displayingFirstTiers.get(2).get(0));
    // todo
//    gameInstance.tradeCard(coins, targets);
//    assertTrue(gameInstance.isTraded());
    assertTrue(true);
  }

  @Test
  void endTurnTest() throws DisplayPileException, GameStartupException, NumOfPlayersException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(2);
    gameInstance.startGame();
    var user1 = gameInstance.getPlayers().get(0);
    var user2 = gameInstance.getPlayers().get(1);
    assertEquals(user1, gameInstance.getCurrentPlayer());
    gameInstance.endTurn();
    assertEquals(user2, gameInstance.getCurrentPlayer());
  }
}
