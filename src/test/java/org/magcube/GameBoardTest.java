package org.magcube;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.NumOfPlayersException;

public class GameBoardTest {

  @ParameterizedTest
  @ValueSource(ints = {2, 3, 4})
  void constructorTest(int numOfPlayers) {
    assertDoesNotThrow(() -> new GameBoard(numOfPlayers));
  }

  @ParameterizedTest
  @ValueSource(ints = {-5, -1, 0, 1, 5})
  void constructorShouldThrowTest(int numOfPlayers) {
    assertThrows(NumOfPlayersException.class, () -> new GameBoard(numOfPlayers));
  }

  @Test
  void takeCardsTest() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    // todo: this test should change to take clone cards
    var firstCard = gameBoard.getDisplayingBasicResource().get(0).get(0);
    assertDoesNotThrow(() -> gameBoard.takeCards(List.of(firstCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingBasicResource().get(0).get(0));
  }

  @Test
  void takeCardsTest2() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    var firstCard = gameBoard.getDisplayingBasicResource().get(0).get(0);
    var secondCard = gameBoard.getDisplayingBasicResource().get(1).get(0);
    var buildingCard = gameBoard.getDisplayingBuildings().get(0).get(0);
    assertDoesNotThrow(
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, buildingCard)));
    Assertions.assertNotSame(firstCard, gameBoard.getDisplayingBasicResource().get(0).get(0));
    Assertions.assertNotSame(secondCard, gameBoard.getDisplayingBasicResource().get(1).get(0));
    Assertions.assertNotSame(buildingCard, gameBoard.getDisplayingBuildings().get(0).get(0));
  }

  @Test
  void takeCardsFailTest() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    var firstCard = ResourceCard.builder().build();
    var secondCard = ResourceCard.builder().build();
    var buildingCard = BuildingCard.builder().build();
    Assertions.assertThrows(DisplayPileException.class,
        () -> gameBoard.takeCards(List.of(firstCard, secondCard, buildingCard)));
  }

  @Test
  void discardCardsTest() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    var card = ResourceCard.builder()
        .cardType(CardType.BASIC_RESOURCE)
        .name("test1")
        .build();
    assertDoesNotThrow(() -> gameBoard.discardCards(List.of(card)));
    Assertions.assertEquals(1,
        gameBoard.getBasicResourcesPile().discardPileSize());
  }

  @Test
  void giveCardsTest2() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    var card = BuildingCard.builder()
        .cardType(CardType.BUILDING)
        .name("test1")
        .build();
    assertDoesNotThrow(() -> gameBoard.discardCards(List.of(card)));
    Assertions.assertEquals(1,
        gameBoard.getBuildingPile().discardPileSize());
  }

  @Test
  void refillGameBoardTest() throws DisplayPileException, NumOfPlayersException {
    var gameBoard = new GameBoard(4);
    assertNotNull(gameBoard.getDisplayingBasicResource().get(0).get(0));
    assertNotNull(gameBoard.getDisplayingBasicResource().get(1).get(0));
    assertNotNull(gameBoard.getDisplayingBuildings().get(0).get(0));
    gameBoard.refillCards();
    Assertions.assertEquals(5, gameBoard.getDisplayingBuildings().size());
    Assertions.assertTrue(gameBoard.getDisplayingBuildings().stream()
        .allMatch(cards -> cards != null && !cards.isEmpty()));
    Assertions.assertEquals(5, gameBoard.getDisplayingBasicResource().size());
    Assertions.assertTrue(gameBoard.getDisplayingBasicResource().stream()
        .allMatch(cards -> cards != null && !cards.isEmpty()));
  }
}
