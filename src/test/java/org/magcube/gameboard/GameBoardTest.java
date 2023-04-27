package org.magcube.gameboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.magcube.displayingpile.DisplayingPileTestUtil.numOfCardsInDisplaying;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.CardIdentitiesException;
import org.magcube.player.NumOfPlayers;


class GameBoardTest {

  private static Stream<GameBoard> gameBoardProvider() {
    return Stream.of(
        new GameBoard(NumOfPlayers.TWO),
        new GameBoard(NumOfPlayers.THREE),
        new GameBoard(NumOfPlayers.FOUR)
    );
  }

  private static Stream<Arguments> getPileStateTest() {
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
    var pileState = gameBoard.pileState(cardType);
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
    assertEquals(gameBoard.pileState(CardType.BASIC_RESOURCE), gameBoardState.basicResource());
    assertEquals(gameBoard.pileState(CardType.LEVEL_ONE_RESOURCE), gameBoardState.levelOneResource());
    assertEquals(gameBoard.pileState(CardType.LEVEL_TWO_RESOURCE), gameBoardState.levelTwoResource());
    assertEquals(gameBoard.pileState(CardType.BUILDING), gameBoardState.building());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayTest1(GameBoard gameBoard) {
    var valid1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var valid2 = new CardIdentity(CardType.BASIC_RESOURCE, 3);
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid1)));
    assertNotNull(gameBoard.cardsInDisplay(List.of(valid2)));

    var optCategorizedCards1 = gameBoard.cardsInDisplay(List.of(valid1));
    assertTrue(optCategorizedCards1.isPresent());
    var categorizedCards1 = optCategorizedCards1.get();
    assertEquals(1, categorizedCards1.size());
    assertTrue(categorizedCards1.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(1, categorizedCards1.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards1.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());

    var optCategorizedCards2 = gameBoard.cardsInDisplay(List.of(valid1, valid2));
    assertTrue(optCategorizedCards2.isPresent());
    var categorizedCards2 = optCategorizedCards2.get();
    assertEquals(1, categorizedCards2.size());
    assertTrue(categorizedCards2.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(2, categorizedCards2.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());

    var optCategorizedCards3 = gameBoard.cardsInDisplay(List.of(valid2, valid1));
    assertTrue(optCategorizedCards3.isPresent());
    var categorizedCards3 = optCategorizedCards3.get();
    assertEquals(1, categorizedCards3.size());
    assertTrue(categorizedCards3.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(2, categorizedCards3.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(1, categorizedCards2.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());

    var optCategorizedCards4 = gameBoard.cardsInDisplay(List.of(valid2, valid1, valid2));
    assertTrue(optCategorizedCards4.isPresent());
    var categorizedCards4 = optCategorizedCards4.get();
    assertEquals(1, categorizedCards4.size());
    assertTrue(categorizedCards4.containsKey(CardType.BASIC_RESOURCE));
    assertEquals(3, categorizedCards4.get(CardType.BASIC_RESOURCE).size());
    assertEquals(1, categorizedCards4.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid1)).count());
    assertEquals(2, categorizedCards4.get(CardType.BASIC_RESOURCE).stream().filter(card -> card.getCardIdentity().equals(valid2)).count());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayTest2(GameBoard gameBoard) {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.levelOneResource().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid3 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1)).isPresent());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid2)).isPresent());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid3)).isPresent());

    var optCategorizedCards = gameBoard.cardsInDisplay(List.of(valid1, valid2, valid3));
    assertTrue(optCategorizedCards.isPresent());
    var categorizedCards = optCategorizedCards.get();
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
  void cardsInDisplayShouldReturnOptionalEmptyTest1(GameBoard gameBoard) {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();

    var invalid1 = gameBoardState.levelTwoResource().deck().get(0).getCardIdentity();
    var invalid2 = gameBoardState.building().deck().get(0).getCardIdentity();

    assertFalse(gameBoard.cardsInDisplay(List.of(valid1, valid2)).isEmpty());

    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1, invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1, valid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid2, invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2, valid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2, valid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2, valid1, valid2)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void cardsInDisplayShouldReturnOptionalEmptyTest2(GameBoard gameBoard) {
    var gameBoardState = gameBoard.gameBoardState();
    var valid1 = gameBoardState.building().displaying().get(0).get(0).getCardIdentity();
    var valid2 = gameBoardState.building().displaying().get(1).get(0).getCardIdentity();

    var invalid1 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);
    var invalid2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);
    var invalid3 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999);
    var invalid4 = new CardIdentity(CardType.BUILDING, 99999);

    assertFalse(gameBoard.cardsInDisplay(List.of(valid1, valid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid3)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid4)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, invalid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid1, valid1)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid2, invalid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(invalid2, valid2)).isEmpty());
    assertTrue(gameBoard.cardsInDisplay(List.of(valid1, valid2, invalid1)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void takeCardsTest1(GameBoard gameBoard) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    var cardIdentity3 = new CardIdentity(CardType.BASIC_RESOURCE, 5);

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(gameBoard.gameBoardState().basicResource().displaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(gameBoard.gameBoardState().basicResource().displaying(), cardIdentity2);

    var optCards = gameBoard.cardsInDisplay(List.of(cardIdentity1, cardIdentity2, cardIdentity3));
    assertTrue(optCards.isPresent());
    var cards = optCards.get();
    gameBoard.takeCards(cards);

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(gameBoard.gameBoardState().basicResource().displaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(gameBoard.gameBoardState().basicResource().displaying(), cardIdentity2);

    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - 1, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void takeCardTest2(GameBoard gameBoard) {
    var card1 = gameBoard.gameBoardState().levelOneResource().displaying().get(0).get(0);
    var card2 = gameBoard.gameBoardState().levelTwoResource().displaying().get(1).get(0);
    var card3 = gameBoard.gameBoardState().building().displaying().get(2).get(0);

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(gameBoard.gameBoardState().levelOneResource().displaying(), card1.getCardIdentity());
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(gameBoard.gameBoardState().levelTwoResource().displaying(), card2.getCardIdentity());
    var initNumOfCardWithCardIdentity3 = numOfCardsInDisplaying(gameBoard.gameBoardState().building().displaying(), card3.getCardIdentity());

    var cardIdentities = List.of(card1.getCardIdentity(), card2.getCardIdentity(), card3.getCardIdentity());
    var optCards = gameBoard.cardsInDisplay(cardIdentities);
    assertTrue(optCards.isPresent());
    var cards = optCards.get();
    gameBoard.takeCards(cards);

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(gameBoard.gameBoardState().levelOneResource().displaying(), card1.getCardIdentity());
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(gameBoard.gameBoardState().levelTwoResource().displaying(), card2.getCardIdentity());
    var curNumOfCardWithCardIdentity3 = numOfCardsInDisplaying(gameBoard.gameBoardState().building().displaying(), card3.getCardIdentity());

    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - 1, curNumOfCardWithCardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity3 - 1, curNumOfCardWithCardIdentity3);
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsTest1() throws CardIdentitiesException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card5 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card6 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build();
    var card7 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    HashMap<CardType, List<? extends Card>> expected = new HashMap<>();
    expected.put(CardType.BASIC_RESOURCE, List.of(card1, card2, card3));
    expected.put(CardType.LEVEL_ONE_RESOURCE, List.of(card4, card5, card6));
    expected.put(CardType.LEVEL_TWO_RESOURCE, List.of(card7));

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4, card5, card6, card7));
    assertEquals(expected, categorizedCards);
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void validateAndCategorizeDiscardCardsTest2() throws CardIdentitiesException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();

    HashMap<CardType, List<? extends Card>> expected = new HashMap<>();
    expected.put(CardType.BASIC_RESOURCE, List.of(card1, card2, card3));
    expected.put(CardType.LEVEL_ONE_RESOURCE, List.of(card4));

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4));
    assertEquals(expected, categorizedCards);
  }

  @Test
  void validateAndCategorizeDiscardCardsShouldThrowTest1() {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var invalidCard1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 99999)).build();
    var invalidCard2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999)).build();
    var invalidCard3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999)).build();

    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard2)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard3)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard2)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard3)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard2)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard3)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard2)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard3)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1, invalidCard2)));
  }

  @Test
  void validateAndCategorizeDiscardCardsShouldThrowTest2() {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var invalidCard1 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();

    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card2, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card3, invalidCard1)));
    assertThrows(CardIdentitiesException.class, () -> GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, invalidCard1)));
  }

  @ParameterizedTest
  @MethodSource("gameBoardProvider")
  void discardCardsTest(GameBoard gameBoard) throws CardIdentitiesException {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var card3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build();
    var card4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card5 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build();
    var card6 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build();
    var card7 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).build();

    var gameBoardState = gameBoard.gameBoardState();
    var basicResourceDisplay = gameBoardState.basicResource().displaying();
    var initNumOfCard1 = numOfCardsInDisplaying(basicResourceDisplay, card1.getCardIdentity());
    var initNumOfCard3 = numOfCardsInDisplaying(basicResourceDisplay, card3.getCardIdentity());

    var categorizedCards = GameBoard.validateAndCategorizeDiscardCards(List.of(card1, card2, card3, card4, card5, card6, card7));
    gameBoard.discardCards(categorizedCards);

    var gameBoardStateAfterDiscard = gameBoard.gameBoardState();
    var basicResourceDisplayAfterDiscard = gameBoardStateAfterDiscard.basicResource().displaying();
    var numOfCard1AfterDiscard = numOfCardsInDisplaying(basicResourceDisplayAfterDiscard, card1.getCardIdentity());
    var numOfCard3AfterDiscard = numOfCardsInDisplaying(basicResourceDisplayAfterDiscard, card3.getCardIdentity());

    assertEquals(initNumOfCard1 + 2, numOfCard1AfterDiscard);
    assertEquals(initNumOfCard3 + 1, numOfCard3AfterDiscard);

    var levelOneDiscardPile = gameBoardStateAfterDiscard.levelOneResource().discardPile();
    assertEquals(List.of(card4, card5, card6), levelOneDiscardPile);

    var levelTwoDiscardPile = gameBoardStateAfterDiscard.levelTwoResource().discardPile();
    assertEquals(List.of(card7), levelTwoDiscardPile);
  }
}