package org.magcube.gameboard;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
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

  private static long numOfCardsWithCardIdentityInDisplaying(List<List<ResourceCard>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
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

  @ParameterizedTest
  @MethodSource
  void getPileStateTest(GameBoard gameBoard, CardType cardType, Class<? extends Card> clazz) {
    var pileState = gameBoard.getPileState(cardType);
    assertEquals(cardType, pileState.cardType());
    assertFalse(pileState.displaying().isEmpty());
    pileState.displaying().forEach(
        innerList -> {
          assertFalse(innerList.isEmpty());
          innerList.forEach(card -> assertEquals(clazz, card.getClass()));
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

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayTest3(GameBoard gameBoard) throws DisplayPileException {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();

    var invalid1 = gameBoardState.levelTwoResource().deck().get(0).getCardIdentity();
    var invalid2 = gameBoardState.building().deck().get(0).getCardIdentity();

    assertNotNull(gameBoard.cardsInDisplay(List.of(valid1, valid2)));

    assertNull(gameBoard.cardsInDisplay(List.of(invalid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid1, invalid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(valid1, invalid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid1, valid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(valid2, invalid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid2, valid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(valid1, invalid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid2, valid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid1)));
    assertNull(gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid2)));
    assertNull(gameBoard.cardsInDisplay(List.of(invalid2, valid1, valid2)));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayShouldThrowTest(GameBoard gameBoard) throws DisplayPileException {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();

    var invalid1 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);
    var invalid2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);
    var invalid3 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999);
    var invalid4 = new CardIdentity(CardType.BUILDING, 99999);

    assertDoesNotThrow(() -> gameBoard.cardsInDisplay(List.of(valid1, valid2)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid1)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid2)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid3)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid4)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(valid1, invalid1)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid1, valid1)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(valid2, invalid2)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(invalid2, valid2)));
    assertThrows(DisplayPileException.class, () -> gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid1)));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void takeCardsTest1(GameBoard gameBoard) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    var cardIdentity3 = new CardIdentity(CardType.BASIC_RESOURCE, 5);

    var cards = gameBoard.cardsInDisplay(List.of(cardIdentity1, cardIdentity2, cardIdentity3));
    assertNotNull(cards);
    var cardsToken = gameBoard.takeCards(cards);
    assertNotNull(cardsToken);
    assertEquals(1, cardsToken.size());
    assertTrue(cardsToken.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(3, cardsToken.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, cardsToken.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(cardIdentity1)).count());
    assertEquals(1, cardsToken.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(cardIdentity2)).count());
    assertEquals(1, cardsToken.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(cardIdentity3)).count());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void takeCardTest2(GameBoard gameBoard) throws DisplayPileException {
    var gameBoardState = gameBoard.gameBoardState();
    var card1 = gameBoardState.levelOneResource().displaying().get(0).get(0);
    var card2 = gameBoardState.levelTwoResource().displaying().get(1).get(0);
    var card3 = gameBoardState.building().displaying().get(2).get(0);

    var cardIdentities = List.of(card1.getCardIdentity(), card2.getCardIdentity(), card3.getCardIdentity());
    var cards = gameBoard.cardsInDisplay(cardIdentities);
    assertNotNull(cards);
    var cardsToken = gameBoard.takeCards(cards);
    assertEquals(3, cardsToken.size());
    assertTrue(cardsToken.containsKey(CardType.LEVEL_ONE_RESOURCE));
    assertTrue(cardsToken.containsKey(CardType.LEVEL_TWO_RESOURCE));
    assertTrue(cardsToken.containsKey(CardType.BUILDING));
    assertEquals(List.of(card1), cardsToken.get(CardType.LEVEL_ONE_RESOURCE));
    assertEquals(List.of(card2), cardsToken.get(CardType.LEVEL_TWO_RESOURCE));
    assertEquals(List.of(card3), cardsToken.get(CardType.BUILDING));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void takeCardTest3(GameBoard gameBoard) throws DisplayPileException {
    HashMap<CardType, List<? extends Card>> emptyMap = new HashMap<>();
    var cardsToken = gameBoard.takeCards(emptyMap);
    assertEquals(0, cardsToken.size());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsTest1() throws DisplayPileException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card5 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card6 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build();
    var card7 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4, card5, card6, card7));
    assertEquals(3, categorizedCards.size());
    assertTrue(categorizedCards.containsKey(CardType.BASIC_RESOURCE));
    assertTrue(categorizedCards.containsKey(CardType.LEVEL_ONE_RESOURCE));
    assertTrue(categorizedCards.containsKey(CardType.LEVEL_TWO_RESOURCE));
    assertEquals(3, categorizedCards.get(CardType.BASIC_RESOURCE).size());
    assertEquals(3, categorizedCards.get(CardType.LEVEL_ONE_RESOURCE).size());
    assertEquals(1, categorizedCards.get(CardType.LEVEL_TWO_RESOURCE).size());
    assertEquals(List.of(card1, card2, card3), categorizedCards.get(CardType.BASIC_RESOURCE));
    assertEquals(List.of(card4, card5, card6), categorizedCards.get(CardType.LEVEL_ONE_RESOURCE));
    assertEquals(List.of(card7), categorizedCards.get(CardType.LEVEL_TWO_RESOURCE));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsTest2() throws DisplayPileException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4));
    assertEquals(2, categorizedCards.size());
    assertTrue(categorizedCards.containsKey(CardType.BASIC_RESOURCE));
    assertTrue(categorizedCards.containsKey(CardType.LEVEL_ONE_RESOURCE));
    assertEquals(3, categorizedCards.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards.get(CardType.LEVEL_ONE_RESOURCE).size());
    assertEquals(List.of(card1, card2, card3), categorizedCards.get(CardType.BASIC_RESOURCE));
    assertEquals(List.of(card4), categorizedCards.get(CardType.LEVEL_ONE_RESOURCE));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsShouldThrowTest1(GameBoard gameBoard) {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var invalidCard1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 99999)).build();
    var invalidCard2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999)).build();
    var invalidCard3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999)).build();

    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard2)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard3)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard2)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard3)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard2)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard3)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard2)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard3)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1, invalidCard2)));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsShouldThrowTest2(GameBoard gameBoard) {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var invalidCard1 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();

    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard1)));
    assertThrows(DisplayPileException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, invalidCard1)));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void discardCardsTest(GameBoard gameBoard) throws DisplayPileException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card5 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card6 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build();
    var card7 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var gameBoardState = gameBoard.gameBoardState();
    var basicResourceDisplay = gameBoardState.basicResource().displaying();
    var initNumOfCard1 = numOfCardsWithCardIdentityInDisplaying(basicResourceDisplay, card1.getCardIdentity());
    var initNumOfCard3 = numOfCardsWithCardIdentityInDisplaying(basicResourceDisplay, card3.getCardIdentity());

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4, card5, card6, card7));
    gameBoard.discardCards(categorizedCards);

    var gameBoardStateAfterDiscard = gameBoard.gameBoardState();
    var basicResourceDisplayAfterDiscard = gameBoardStateAfterDiscard.basicResource().displaying();
    var numOfCard1AfterDiscard = numOfCardsWithCardIdentityInDisplaying(basicResourceDisplayAfterDiscard, card1.getCardIdentity());
    var numOfCard3AfterDiscard = numOfCardsWithCardIdentityInDisplaying(basicResourceDisplayAfterDiscard, card3.getCardIdentity());

    assertEquals(initNumOfCard1 + 2, numOfCard1AfterDiscard);
    assertEquals(initNumOfCard3 + 1, numOfCard3AfterDiscard);

    var levelOneDiscardPile = gameBoardStateAfterDiscard.levelOneResource().discardPile();
    assertEquals(List.of(card4, card5, card6), levelOneDiscardPile);

    var levelTwoDiscardPile = gameBoardStateAfterDiscard.levelTwoResource().discardPile();
    assertEquals(List.of(card7), levelTwoDiscardPile);
  }
}