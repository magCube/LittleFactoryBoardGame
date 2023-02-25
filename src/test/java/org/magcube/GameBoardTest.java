package org.magcube;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.magcube.card.Card;
import org.magcube.card.Factory;
import org.magcube.card.FirstTierResource;
import org.magcube.exception.DisplayPileException;

public class GameBoardTest {

  @Test
  void takeCardsTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = gameBoard.getDisplayingFirstTierResources().get(0).get(0);
    Assertions.assertDoesNotThrow(() -> gameBoard.takeCards(List.of(firstCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingFirstTierResources().get(0).get(0));
  }

  @Test
  void takeCardsTest2() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = gameBoard.getDisplayingFirstTierResources().get(0).get(0);
    var secondCard = gameBoard.getDisplayingFirstTierResources().get(1).get(0);
    var factoryCard = gameBoard.getDisplayingFactories().get(0).get(0);
    Assertions.assertDoesNotThrow(
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, factoryCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingFirstTierResources().get(0).get(0));
    Assertions.assertNotSame(secondCard, gameBoard.getDisplayingFirstTierResources().get(1).get(0));
    Assertions.assertNotSame(factoryCard, gameBoard.getDisplayingFactories().get(0).get(0));
  }

  @Test
  void takeCardsFailTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var firstCard = Card.builder().build();
    var secondCard = FirstTierResource.firstTierBuilder().build();
    var factoryCard = Factory.factoryBuilder().build();
    Assertions.assertThrows(DisplayPileException.class,
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, factoryCard)));
  }

  @Test
  void giveCardsTest() throws DisplayPileException {
    var gameBoard = new GameBoard();
    var oldFirstTierResourcesDeckSize = gameBoard.getFirstTierResourcesPile().deckSize();
    var card = FirstTierResource.firstTierBuilder()
        .name("test1")
        .build();
    Assertions.assertTrue(gameBoard.putCards(List.of(card)));
    Assertions.assertEquals(oldFirstTierResourcesDeckSize + 1,
        gameBoard.getFirstTierResourcesPile().deckSize());
  }

}
