package org.magcube.gameinstance;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.GameInstance;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.AlreadyTradedOrProducedException;
import org.magcube.exception.CardIdentitiesException;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.ExceededMaxNumOfHandException;
import org.magcube.exception.GameStartupException;
import org.magcube.exception.InvalidTradingException;
import org.magcube.exception.NotAvailableInGameBoardException;
import org.magcube.exception.PlayerDoesNotOwnCardsException;
import org.magcube.player.NumOfPlayers;
import org.magcube.player.Player;
import org.mockito.Mockito;

public class GameInstanceTest {

  GameInstance game;

  @BeforeEach
  void initGame() throws DisplayPileException, GameStartupException {
    game = Mockito.spy(new GameInstance());
    game.setPlayers(NumOfPlayers.TWO);
    game.startGame();
  }

  @ParameterizedTest
  @EnumSource
  void setPlayersTest(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(numOfPlayers);
    assertEquals(numOfPlayers.getValue(), gameInstance.getPlayers().size());
    assertNotNull(gameInstance.getGameBoard());
  }

  @ParameterizedTest
  @EnumSource
  void startGameNormally(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var gameInstance = new GameInstance();
    gameInstance.setPlayers(numOfPlayers);
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

  @Nested
  class tradeCardsByCoins {

    @Test
    void alreadyTradedTest() {
      GameTestUtils.setIsTradedOrPlayerProduced(game, true);
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      assertThrows(AlreadyTradedOrProducedException.class, () -> game.tradeCardsByCoins(cardIdentities));
    }

    @Test
    void invalidCardIdentitiesTest() {
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 99999));
      assertThrows(CardIdentitiesException.class, () -> game.tradeCardsByCoins(cardIdentities));
    }

    @Test
    void exceedMaxHandTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 6)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 7)).build()
          )
      );
      assertEquals(7, currentPlayer.getResources().size());
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      assertThrows(ExceededMaxNumOfHandException.class, () -> game.tradeCardsByCoins(cardIdentities));
    }

    @Test
    void noAvailableInGameBoardTest() {
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.BUILDING, 1));
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(cardIdentities)).thenReturn(null);
      });
      assertThrows(NotAvailableInGameBoardException.class, () -> game.tradeCardsByCoins(cardIdentities));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 998})
    void noEnoughCoinsTest(int coin) {
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1));
      var mockedCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(999).build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(cardIdentities)).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(mockedCard));
        }});
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.receiveCoin(coin);

      Exception exception = assertThrows(InvalidTradingException.class, () -> game.tradeCardsByCoins(cardIdentities));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.paymentNoEnough, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {999, 1000})
    void enoughCoinsTest(int coin) {
      List<CardIdentity> cardIdentities = List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1));
      var mockedCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(999).build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(cardIdentities)).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(mockedCard));
        }});
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.receiveCoin(coin);

      assertDoesNotThrow(() -> game.tradeCardsByCoins(cardIdentities));
    }

    @Test
    void shouldSuccessTest() throws DisplayPileException {
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });

      List<CardIdentity> cardIdentities = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3),
          new CardIdentity(CardType.BUILDING, 4)
      );

      assertDoesNotThrow(() -> game.tradeCardsByCoins(cardIdentities));

      var currentPlayer = game.getCurrentPlayer();
      assertEquals(0, currentPlayer.getCoin());
      assertEquals(3, currentPlayer.getResources().size());
      assertEquals(1, currentPlayer.getBuildings().size());

      List<ResourceCard> expectedResourceCards = List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)).build()
      );
      List<BuildingCard> expectedBuildingCards = List.of(
          BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 4)).build()
      );
      HashMap<CardType, List<? extends Card>> expectedCategorizedCard = new HashMap<>() {{
        put(CardType.BASIC_RESOURCE, List.of(expectedResourceCards.get(0)));
        put(CardType.LEVEL_ONE_RESOURCE, List.of(expectedResourceCards.get(1)));
        put(CardType.LEVEL_TWO_RESOURCE, List.of(expectedResourceCards.get(2)));
        put(CardType.BUILDING, List.of(expectedBuildingCards.get(0)));
      }};

      assertTrue(currentPlayer.getResources().containsAll(expectedResourceCards));
      assertTrue(currentPlayer.getBuildings().containsAll(expectedBuildingCards));
      assertEquals(0, currentPlayer.getCoin());
      assertTrue(game.isTradedOrPlayerProduced());
      verify(game.getGameBoard()).takeCards(expectedCategorizedCard);
    }
  }

  @Nested
  class tradeCardsByCards {

    @Test
    void alreadyTradedTest() {
      GameTestUtils.setIsTradedOrPlayerProduced(game, true);
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 2));
      assertThrows(AlreadyTradedOrProducedException.class, () -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void oneToOneTest() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 2));
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build()));
      assertDoesNotThrow(() -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void oneToNTest() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 3),
          new CardIdentity(CardType.BASIC_RESOURCE, 4)
      );
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build()));
      assertDoesNotThrow(() -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void nToOneTest() {
      List<CardIdentity> payment = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 3)
      );
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 4));
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build()
      ));
      assertDoesNotThrow(() -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void ntoNTest() {
      List<CardIdentity> payment = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 3)
      );
      List<CardIdentity> targets = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 4),
          new CardIdentity(CardType.BASIC_RESOURCE, 5),
          new CardIdentity(CardType.BASIC_RESOURCE, 6)
      );
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build()
      ));
      Exception exception = assertThrows(InvalidTradingException.class, () -> game.tradeCardsByCards(payment, targets));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.notOneToNOrNToOne, exception.getMessage());
    }

    @Test
    void invalidCostCardIdentitiesTest() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 99999));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 2));
      assertThrows(CardIdentitiesException.class, () -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void invalidProductCardIdentitiesTest() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 99999));
      assertThrows(CardIdentitiesException.class, () -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void exceedMaxHandShouldNotThrowTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 6)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 7)).build()
          )
      );
      assertEquals(7, currentPlayer.getResources().size());

      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });

      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 2));
      assertDoesNotThrow(() -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void exceedMaxHandShouldThrowTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 6)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 7)).build()
          )
      );
      assertEquals(7, currentPlayer.getResources().size());

      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        GameTestUtils.mockCardsInDisplayReturnDummyCards(mockedGameBoard);
      });

      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 3)
      );
      assertThrows(ExceededMaxNumOfHandException.class, () -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void noAvailableInGameBoardTest() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BUILDING, 1));
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(null);
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build()));
      assertThrows(NotAvailableInGameBoardException.class, () -> game.tradeCardsByCards(payment, targets));
    }

    @Test
    void paymentNoEnoughTest1() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1));
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BUILDING, 1));
      var mockedPaymentCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(1).build();
      var mockedTargetCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(999).build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.BUILDING, List.of(mockedTargetCard));
        }});
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(mockedPaymentCard));

      Exception exception = assertThrows(InvalidTradingException.class, () -> game.tradeCardsByCards(payment, targets));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.paymentNoEnough, exception.getMessage());
    }

    @Test
    void paymentNoEnoughTest2() {
      List<CardIdentity> payment = List.of(
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)
      );
      List<CardIdentity> targets = List.of(new CardIdentity(CardType.BUILDING, 1));
      var mockedPaymentCard1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(1).build();
      var mockedPaymentCard2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).value(10).build();
      var mockedTargetCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(999).build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.BUILDING, List.of(mockedTargetCard));
        }});
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(mockedPaymentCard1, mockedPaymentCard2));

      Exception exception = assertThrows(InvalidTradingException.class, () -> game.tradeCardsByCards(payment, targets));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.paymentNoEnough, exception.getMessage());
    }

    @Test
    void paymentNoEnoughTest3() {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1));
      List<CardIdentity> targets = List.of(
          new CardIdentity(CardType.BUILDING, 1),
          new CardIdentity(CardType.BUILDING, 2)
      );
      var mockedPaymentCard = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(999).build();
      var mockedTargetCard1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(1).build();
      var mockedTargetCard2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 2)).value(999).build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.BUILDING, List.of(mockedTargetCard1, mockedTargetCard2));
        }});
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(mockedPaymentCard));

      Exception exception = assertThrows(InvalidTradingException.class, () -> game.tradeCardsByCards(payment, targets));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.paymentNoEnough, exception.getMessage());
    }

    @Test
    void shouldSuccessTest1() throws DisplayPileException {
      List<CardIdentity> payment = List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1));
      var paymentCard = ResourceCard.builder().cardIdentity(payment.get(0)).value(999).build();
      var targets = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 2),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
          new CardIdentity(CardType.BUILDING, 1)
      );
      var targetCards = List.of(
          ResourceCard.builder().cardIdentity(targets.get(0)).value(1).build(),
          ResourceCard.builder().cardIdentity(targets.get(1)).value(1).build(),
          ResourceCard.builder().cardIdentity(targets.get(2)).value(1).build(),
          BuildingCard.builder().cardIdentity(targets.get(3)).value(1).build()
      );

      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.BASIC_RESOURCE, List.of(targetCards.get(0), targetCards.get(1)));
          put(CardType.LEVEL_ONE_RESOURCE, List.of(targetCards.get(2)));
          put(CardType.BUILDING, List.of(targetCards.get(3)));
        }});
      });

      GameTestUtils.spyCurrentPlayer(game);
      var ownResourceCard2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)).value(1).build();
      var ownBuildingCard = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 2)).value(1).build();
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
          paymentCard,
          ownResourceCard2
      ));
      currentPlayer.takeBuildingCards(List.of(ownBuildingCard));

      var expectedCategorizedPaymentCards = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.LEVEL_TWO_RESOURCE, List.of(paymentCard));
      }};

      var expectedCategorizedTargetCards = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.BASIC_RESOURCE, List.of(targetCards.get(0), targetCards.get(1)));
        put(CardType.LEVEL_ONE_RESOURCE, List.of(targetCards.get(2)));
        put(CardType.BUILDING, List.of(targetCards.get(3)));
      }};

      assertDoesNotThrow(() -> game.tradeCardsByCards(payment, targets));
      assertEquals(4, currentPlayer.getResources().size());
      assertTrue(currentPlayer.getResources().containsAll(List.of(
          ownResourceCard2,
          targetCards.get(0),
          targetCards.get(1),
          targetCards.get(2)
      )));
      assertEquals(2, currentPlayer.getBuildings().size());
      assertTrue(currentPlayer.getBuildings().containsAll(List.of(
          ownBuildingCard,
          targetCards.get(3)
      )));
      assertTrue(game.isTradedOrPlayerProduced());
      verify(game.getGameBoard()).takeCards(expectedCategorizedTargetCards);
      verify(game.getGameBoard()).discardCards(expectedCategorizedPaymentCards);
      verify(currentPlayer).discardCards(List.of(paymentCard));
    }
  }

  @Nested
  class playerProduceBySpentCost {

    @Test
    void alreadyProducedTest() {
      GameTestUtils.setIsTradedOrPlayerProduced(game, true);
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      CardIdentity productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      assertThrows(AlreadyTradedOrProducedException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void invalidCostCardIdentitiesTest() {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 99999));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      assertThrows(CardIdentitiesException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void invalidProductCardIdentityTest() {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);
      assertThrows(CardIdentitiesException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void exceedMaxHandCardShouldNotThrowTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 6)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 7)).build()
          )
      );

      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .cost(new CardIdentity[][]{{new CardIdentity(CardType.BASIC_RESOURCE, 1)}})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_ONE_RESOURCE, List.of(productCard));
        }});
      });

      assertDoesNotThrow(() -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void notOwningCostCardTest() {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .cost(new CardIdentity[][]{{new CardIdentity(CardType.BASIC_RESOURCE, 1)}})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_ONE_RESOURCE, List.of(productCard));
        }});
      });

      assertThrows(PlayerDoesNotOwnCardsException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void noAvailableInGameBoardTest() {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(null);
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(costCardIdentities.get(0)).build()));

      assertThrows(NotAvailableInGameBoardException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
    }

    @Test
    void costNoMatchTest() {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .cost(new CardIdentity[][]{{new CardIdentity(CardType.BASIC_RESOURCE, 2)}})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_ONE_RESOURCE, List.of(productCard));
        }});
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build()));

      Exception exception = assertThrows(InvalidTradingException.class, () -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.costNotMatch, exception.getMessage());
    }

    @Test
    void shouldSuccessTest1() throws DisplayPileException {
      List<CardIdentity> costCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var costCard = ResourceCard.builder().cardIdentity(costCardIdentities.get(0)).build();
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .cost(new CardIdentity[][]{{new CardIdentity(CardType.BASIC_RESOURCE, 1)}})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_ONE_RESOURCE, List.of(productCard));
        }});
      });

      GameTestUtils.spyCurrentPlayer(game);
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(costCard));

      var expectedCategorizedCostCard = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.BASIC_RESOURCE, List.of(costCard));
      }};

      var expectedCategorizedProductCard = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.LEVEL_ONE_RESOURCE, List.of(productCard));
      }};

      assertDoesNotThrow(() -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
      assertEquals(1, currentPlayer.getResources().size());
      assertTrue(currentPlayer.getResources().contains(productCard));
      assertTrue(game.isTradedOrPlayerProduced());
      verify(game.getGameBoard()).takeCards(expectedCategorizedProductCard);
      verify(game.getGameBoard()).discardCards(expectedCategorizedCostCard);
      verify(currentPlayer).discardCards(List.of(costCard));
    }

    @Test
    void shouldSuccessTest2() throws DisplayPileException {
      List<CardIdentity> costCardIdentities = List.of(
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
      );
      var costCards = List.of(
          ResourceCard.builder().cardIdentity(costCardIdentities.get(0)).build(),
          ResourceCard.builder().cardIdentity(costCardIdentities.get(1)).build(),
          ResourceCard.builder().cardIdentity(costCardIdentities.get(2)).build()
      );

      var productCardIdentity = new CardIdentity(CardType.BUILDING, 1);
      var productCard = BuildingCard.builder()
          .cardIdentity(productCardIdentity)
          .cost(new CardIdentity[][]{{
              new CardIdentity(CardType.BASIC_RESOURCE, 1),
              new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
              new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
          }})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.BUILDING, List.of(productCard));
        }});
      });

      GameTestUtils.spyCurrentPlayer(game);
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(costCards);

      var expectedCategorizedCostCard = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.BASIC_RESOURCE, List.of(costCards.get(0)));
        put(CardType.LEVEL_ONE_RESOURCE, List.of(costCards.get(1)));
        put(CardType.LEVEL_TWO_RESOURCE, List.of(costCards.get(2)));
      }};

      var expectedCategorizedProductCard = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.BUILDING, List.of(productCard));
      }};

      assertDoesNotThrow(() -> game.playerProduceBySpentCost(costCardIdentities, productCardIdentity));
      assertEquals(0, currentPlayer.getResources().size());
      assertEquals(1, currentPlayer.getBuildings().size());
      assertTrue(currentPlayer.getBuildings().contains(productCard));
      assertTrue(game.isTradedOrPlayerProduced());
      verify(game.getGameBoard()).takeCards(expectedCategorizedProductCard);
      verify(game.getGameBoard()).discardCards(expectedCategorizedCostCard);
      verify(currentPlayer).discardCards(costCards);
    }
  }

  @Nested
  class playerProduceByOwningCapital {

    @Test
    void alreadyProducedTest() {
      GameTestUtils.setIsTradedOrPlayerProduced(game, true);
      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      CardIdentity productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2);
      assertThrows(AlreadyTradedOrProducedException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void invalidCapitalCardIdentitiesTest() {
      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
      assertThrows(CardIdentitiesException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void invalidProductCardIdentityTest() {
      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);
      assertThrows(CardIdentitiesException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void exceedMaxHandCardShouldNotThrowTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 6)).build()
          )
      );

      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .capital(new CardIdentity[]{new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
        }});
      });

      assertDoesNotThrow(() -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void exceedMaxHandCardShouldThrowTest() {
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 6)).build(),
              ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 7)).build()
          )
      );

      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .capital(new CardIdentity[]{new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
        }});
      });

      assertThrows(ExceededMaxNumOfHandException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void notOwningCapitalCardTest() {
      List<CardIdentity> capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .capital(new CardIdentity[]{capitalCardIdentities.get(0)})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        GameTestUtils.mockTakeCards(mockedGameBoard);
        Mockito.when(mockedGameBoard.cardsInDisplay(List.of(productCardIdentity))).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
        }});
      });

      assertThrows(PlayerDoesNotOwnCardsException.class, () -> game.playerProduceBySpentCost(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void noAvailableInGameBoardTest() {
      var capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);

      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(null);
      });

      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(capitalCardIdentities.get(0)).build()));

      assertThrows(NotAvailableInGameBoardException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
    }

    @Test
    void capitalNoMatchTest() {
      var capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .capital(new CardIdentity[]{new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
        }});
      });
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build()));

      Exception exception = assertThrows(
          InvalidTradingException.class, () -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
      assertEquals(InvalidTradingException.class, exception.getClass());
      assertEquals(InvalidTradingException.capitalNotMatch, exception.getMessage());
    }

    @Test
    void shouldSuccessTest1() throws DisplayPileException {
      var capitalCardIdentities = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
      var capitalCard = ResourceCard.builder().cardIdentity(capitalCardIdentities.get(0)).build();
      var productCardIdentity = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
      var productCard = ResourceCard.builder()
          .cardIdentity(productCardIdentity)
          .capital(new CardIdentity[]{capitalCardIdentities.get(0)})
          .build();
      GameTestUtils.injectMockGameBoard(game, (mockedGameBoard) -> {
        Mockito.when(mockedGameBoard.cardsInDisplay(any())).thenReturn(new HashMap<>() {{
          put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
        }});
      });

      GameTestUtils.spyCurrentPlayer(game);
      var currentPlayer = game.getCurrentPlayer();
      currentPlayer.takeResourceCards(List.of(capitalCard));

      var expectedCategorizedProductCard = new HashMap<CardType, List<? extends Card>>() {{
        put(CardType.LEVEL_TWO_RESOURCE, List.of(productCard));
      }};

      assertDoesNotThrow(() -> game.playerProduceByOwningCapital(capitalCardIdentities, productCardIdentity));
      assertEquals(2, currentPlayer.getResources().size());
      assertTrue(currentPlayer.getResources().containsAll(List.of(
          capitalCard,
          productCard
      )));
      verify(game.getGameBoard()).takeCards(expectedCategorizedProductCard);
    }
  }
}
