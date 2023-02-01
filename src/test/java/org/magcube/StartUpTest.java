package org.magcube;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.user.User;

public class StartUpTest {

  @Test
  void setUsersTest() throws DisplayPileException {
    var startup = new Startup();
    startup.setUsers(5);
    var users = startup.getUsers();
    assertEquals(5, users.size());
  }

  @Test
  void startGameNormally() throws DisplayPileException {
    var startup = new Startup();
    startup.setUsers(6);
    assertDoesNotThrow(startup::startGame);
  }

  @Test
  void startGameWithoutInitializeUsersShouldThrowException() throws DisplayPileException {
    var startup = new Startup();
    assertThrows(GameStartupException.class, startup::startGame);
  }

  @Test
  void usersAreUnmodifiableAfterStartGame() throws GameStartupException, DisplayPileException {
    var startup = new Startup();
    startup.setUsers(6);
    startup.startGame();
    var resultList = startup.getUsers();
    assertThrows(UnsupportedOperationException.class, () -> resultList.add(new User()));
  }

  @Test
  void usersWereDistributedWithCorrectAmountOfCoinsAfterStartGame()
      throws GameStartupException, DisplayPileException {
    var startup = new Startup();
    startup.setUsers(6);
    startup.startGame();
    var resultList = startup.getUsers();
    var expectedCoins = 3;
    for (var user : resultList) {
      assertEquals(expectedCoins++, user.getCoin());
    }
  }

  @Test
  void haveCorrectCardsDisplayingAfterStartGame()
      throws GameStartupException, DisplayPileException {
    var startup = new Startup();
    startup.setUsers(6);
    startup.startGame();
    assertFalse(startup.getDisplayingFirstTierResources().isEmpty());
    assertFalse(startup.getDisplayingFactories().isEmpty());
  }
}
