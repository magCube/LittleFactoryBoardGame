package org.magcube.gameboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;


class GameBoardTest {

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
    var gameBoardState = gameBoard.gameBoardState();
    assertNotNull(gameBoardState.basicResource());
    assertNotNull(gameBoardState.levelOneResource());
    assertNotNull(gameBoardState.levelTwoResource());
    assertNotNull(gameBoardState.building());
    assertEquals(gameBoard.getPileState(CardType.BASIC_RESOURCE), gameBoardState.basicResource());
    assertEquals(gameBoard.getPileState(CardType.LEVEL_ONE_RESOURCE), gameBoardState.levelOneResource());
    assertEquals(gameBoard.getPileState(CardType.LEVEL_TWO_RESOURCE), gameBoardState.levelTwoResource());
    assertEquals(gameBoard.getPileState(CardType.BUILDING), gameBoardState.building());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayTest1(GameBoard gameBoard) throws DisplayPileException {
    var valid1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var valid2 = new CardIdentity(CardType.BASIC_RESOURCE, 3);
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid1)));
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid2)));

    var categorizedCards1 = gameBoard.cardsInDisplay(List.of(valid1));
    assertNotNull(categorizedCards1);
    assertEquals(1, categorizedCards1.size());
    assertTrue(categorizedCards1.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(1, categorizedCards1.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards1.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());

    var categorizedCards2 = gameBoard.cardsInDisplay(List.of(valid1, valid2));
    assertNotNull(categorizedCards2);
    assertEquals(1, categorizedCards2.size());
    assertTrue(categorizedCards2.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(2, categorizedCards2.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());

    var categorizedCards3 = gameBoard.cardsInDisplay(List.of(valid2, valid1));
    assertNotNull(categorizedCards3);
    assertEquals(1, categorizedCards3.size());
    assertTrue(categorizedCards3.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(2, categorizedCards3.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());

    var categorizedCards4 = gameBoard.cardsInDisplay(List.of(valid2, valid1, valid2));
    assertNotNull(categorizedCards4);
    assertEquals(1, categorizedCards4.size());
    assertTrue(categorizedCards4.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(3, categorizedCards4.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards4.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(2, categorizedCards4.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayTest2(GameBoard gameBoard) throws DisplayPileException {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.levelOneResource().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid3 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid1)));
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid2)));
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid3)));

    var categorizedCards = gameBoard.cardsInDisplay(List.of(valid1, valid2, valid3));
    assertNotNull(categorizedCards);
    assertEquals(2, categorizedCards.size());
    assertTrue(categorizedCards.containsKey(CardType.LEVEL_ONE_RESOURCE));
    assertTrue(categorizedCards.containsKey(CardType.BUILDING));
    assertEquals(1, categorizedCards.get(CardType.LEVEL_ONE_RESOURCE).size());
    assertEquals(2, categorizedCards.get(CardType.BUILDING).size());
    assertEquals(1, categorizedCards.get(CardType.LEVEL_ONE_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(1, categorizedCards.get(CardType.BUILDING).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());
    assertEquals(1, categorizedCards.get(CardType.BUILDING).stream().filter(card -> card.getCardIdentity().equals(valid3)).count());
  }

  private static Stream<GameBoard> gameBoardProvider() throws DisplayPileException {
    return Stream.of(
        new GameBoard(NumOfPlayers.TWO),
        new GameBoard(NumOfPlayers.THREE),
        new GameBoard(NumOfPlayers.FOUR)
    );
  }
}