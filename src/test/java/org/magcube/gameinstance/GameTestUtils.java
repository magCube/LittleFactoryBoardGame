package org.magcube.gameinstance;

import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;
import org.magcube.GameInstance;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.gameboard.GameBoard;
import org.magcube.gameboard.GameBoards;
import org.mockito.Mockito;

public class GameTestUtils {

  @FunctionalInterface
  interface ThrowingConsumer<T, E extends Exception> {

    void accept(T t) throws E;
  }

  public static HashMap<CardType, List<? extends Card>> cardsInDisplayReturnDummyCards(List<CardIdentity> cardIdentities) {
    UnaryOperator<CardIdentity> cloneCardIdentity = (x) -> new CardIdentity(x.getCardType(), x.getTypeId());

    ArrayList<Card> cards = new ArrayList<>();
    for (var cardIdentity : cardIdentities) {
      CardIdentity clonedCardIdentity = cloneCardIdentity.apply(cardIdentity);
      Card card = cardIdentity.getCardType() == CardType.BUILDING
          ? BuildingCard.builder().cardIdentity(clonedCardIdentity).build()
          : ResourceCard.builder().cardIdentity(clonedCardIdentity).build();
      cards.add(card);
    }
    return GameBoards.categorizeCards(cards);
  }

  public static void mockTakeCards(GameBoard gameBoardMock) {
    try {
      Mockito.when(gameBoardMock.takeCards(any())).thenAnswer((args) -> args.getArgument(0));
    } catch (DisplayPileException e) {
      throw new RuntimeException(e);
    }
  }

  public static void mockCardsInDisplayReturnDummyCards(GameBoard gameBoardMock) {
    try {
      Mockito.when(gameBoardMock.cardsInDisplay(any())).thenAnswer((args) -> cardsInDisplayReturnDummyCards(args.getArgument(0)));
    } catch (DisplayPileException e) {
      throw new RuntimeException(e);
    }
  }

  public static void injectMockGameBoard(GameInstance game, ThrowingConsumer<GameBoard, Exception> MockingFn) {
    try {
      GameBoard mockedGameBoard = Mockito.mock(GameBoard.class);
      MockingFn.accept(mockedGameBoard);
      Field gameBoardField = GameInstance.class.getDeclaredField("gameBoard");
      gameBoardField.setAccessible(true);
      gameBoardField.set(game, mockedGameBoard);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setIsTradedOrPlayerProduced(GameInstance game, boolean isTradedOrPlayerProduced) {
    try {
      Field field = GameInstance.class.getDeclaredField("isTradedOrPlayerProduced");
      field.setAccessible(true);
      field.set(game, isTradedOrPlayerProduced);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void spyCurrentPlayer(GameInstance game) {
    try {
      var field = GameInstance.class.getDeclaredField("currentPlayer");
      field.setAccessible(true);
      field.set(game, Mockito.spy(game.getCurrentPlayer()));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}