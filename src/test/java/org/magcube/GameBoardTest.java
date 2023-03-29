package org.magcube;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.magcube.card.BasicResource;
import org.magcube.card.Building;
import org.magcube.exception.DisplayPileException;

public class GameBoardTest {

  @Test
  void takeCardsTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = gameBoard.getDisplayingBasicResources().get(0).get(0);
    Assertions.assertDoesNotThrow(() -> gameBoard.takeCards(List.of(firstCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingBasicResources().get(0).get(0));
  }

  @Test
  void takeCardsTest2() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = gameBoard.getDisplayingBasicResources().get(0).get(0);
    var secondCard = gameBoard.getDisplayingBasicResources().get(1).get(0);
    var factoryCard = gameBoard.getDisplayingFactories().get(0).get(0);
    Assertions.assertDoesNotThrow(
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, factoryCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingBasicResources().get(0).get(0));
    Assertions.assertNotSame(secondCard, gameBoard.getDisplayingBasicResources().get(1).get(0));
    Assertions.assertNotSame(factoryCard, gameBoard.getDisplayingFactories().get(0).get(0));
  }

  @Test
  void takeCardsFailTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = BasicResource.builder().build();
    var secondCard = BasicResource.builder().build();
    var factoryCard = Building.builder().build();
    Assertions.assertThrows(DisplayPileException.class,
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, factoryCard)));
  }

  @Test
  void giveCardsTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var card = BasicResource.builder()
        .name("test1")
        .build();
    Assertions.assertDoesNotThrow(() -> gameBoard.putCards(List.of(card)));
    Assertions.assertEquals( 1,
        gameBoard.getBasicResourcesPile().discardPileSize());
  }

  @Test
  void giveCardsTest2() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var card = Building.builder().build();
    Assertions.assertDoesNotThrow(() -> gameBoard.putCards(List.of(card)));
    Assertions.assertEquals(1,
        gameBoard.getFactoriesPile().discardPileSize());
  }

  @Test
  void refillGameBoardTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    Assertions.assertNotNull(gameBoard.getDisplayingBasicResources().get(0).get(0));
    Assertions.assertNotNull(gameBoard.getDisplayingBasicResources().get(1).get(0));
    Assertions.assertNotNull(gameBoard.getDisplayingFactories().get(0).get(0));
    gameBoard.refillCards();
    Assertions.assertEquals(5, gameBoard.getDisplayingFactories().size());
    Assertions.assertTrue(gameBoard.getDisplayingFactories().stream()
        .allMatch(cards -> cards != null && !cards.isEmpty()));
    Assertions.assertEquals(5, gameBoard.getDisplayingBasicResources().size());
    Assertions.assertTrue(gameBoard.getDisplayingBasicResources().stream()
        .allMatch(cards -> cards != null && !cards.isEmpty()));
  }

}
