package org.magcube.gameboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;


class GameBoardTest {

  @ParameterizedTest
  @MethodSource
  void getPileStateTest(GameBoard gameBoard, CardType cardType, Class<? extends Card> clazz) {
    var pileState = gameBoard.getPileState(cardType);
    assertEquals(cardType, pileState.cardType());
    assertFalse(pileState.displaying().isEmpty());
    pileState.displaying().forEach(
        innerList -> {
          assertFalse(innerList.isEmpty());
          innerList.forEach(card -> {
            assertEquals(clazz, card.getClass());
          });
        }
    );
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void getGameBoardState(GameBoard gameBoard) {
    var gameBoardState = gameBoard.getGameBoardState();
    assertNotNull(gameBoardState.basicResource());
    assertNotNull(gameBoardState.levelOneResource());
    assertNotNull(gameBoardState.levelTwoResource());
    assertNotNull(gameBoardState.building());
    assertEquals(gameBoard.getPileState(CardType.BASIC_RESOURCE), gameBoardState.basicResource());
    assertEquals(gameBoard.getPileState(CardType.LEVEL_ONE_RESOURCE), gameBoardState.levelOneResource());
    assertEquals(gameBoard.getPileState(CardType.LEVEL_TWO_RESOURCE), gameBoardState.levelTwoResource());
    assertEquals(gameBoard.getPileState(CardType.BUILDING), gameBoardState.building());
  }


  private static Stream<Arguments> getPileStateTest() throws DisplayPileException {
    return Stream.of(
        Arguments.of(new GameBoard(NumOfPlayers.TWO), CardType.BASIC_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.TWO), CardType.LEVEL_ONE_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.TWO), CardType.LEVEL_TWO_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.TWO), CardType.BUILDING, BuildingCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.THREE), CardType.BASIC_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.THREE), CardType.LEVEL_ONE_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.THREE), CardType.LEVEL_TWO_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.THREE), CardType.BUILDING, BuildingCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.FOUR), CardType.BASIC_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.FOUR), CardType.LEVEL_ONE_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.FOUR), CardType.LEVEL_TWO_RESOURCE, ResourceCard.class),
        Arguments.of(new GameBoard(NumOfPlayers.FOUR), CardType.BUILDING, BuildingCard.class)
    );
  }

  private static Stream<GameBoard> gameBoardProvider() throws DisplayPileException {
    return Stream.of(
        new GameBoard(NumOfPlayers.TWO),
        new GameBoard(NumOfPlayers.THREE),
        new GameBoard(NumOfPlayers.FOUR)
    );
  }
}