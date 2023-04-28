package org.magcube.game;

import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.gameboard.GameBoard;
import org.magcube.gameboard.GameBoards;
import org.mockito.Mockito;

public class GameTestUtils {

  @FunctionalInterface
  interface ThrowableConsumer<T, E extends Exception> {

    void accept(T t) throws E;
  }

  public static HashMap<CardType, List<? extends Card>> cardsInDisplayReturnDummyCards(List<CardIdentity> cardIdentities) {
    UnaryOperator<CardIdentity> cloneCardIdentity = (x) -> new CardIdentity(x.cardType(), x.typeId());

    ArrayList<Card> cards = new ArrayList<>();
    for (var cardIdentity : cardIdentities) {
      CardIdentity clonedCardIdentity = cloneCardIdentity.apply(cardIdentity);
      Card card = cardIdentity.cardType() == CardType.BUILDING
          ? BuildingCard.builder().cardIdentity(clonedCardIdentity).build()
          : ResourceCard.builder().cardIdentity(clonedCardIdentity).build();
      cards.add(card);
    }
    return GameBoards.categorizeCards(cards);
  }

  public static void mockTakeCardsDoNothing(GameBoard gameBoardMock) {
    Mockito.doNothing().when(gameBoardMock).takeCards(any());
  }

  public static void mockCardsInDisplayReturnDummyCards(GameBoard gameBoardMock) {
    Mockito.when(gameBoardMock.cardsInDisplay(any())).thenAnswer((args) -> Optional.of(cardsInDisplayReturnDummyCards(args.getArgument(0))));
  }

  public static void mockCardsInDisplayReturnOptionalEmpty(GameBoard gameBoard) {
    Mockito.when(gameBoard.cardsInDisplay(any())).thenReturn(Optional.empty());
  }

  public static void injectMockGameBoard(Game game, ThrowableConsumer<GameBoard, Exception> MockingFn) {
    try {
      GameBoard mockedGameBoard = Mockito.mock(GameBoard.class);
      MockingFn.accept(mockedGameBoard);
      Field gameBoardField = GameImpl.class.getDeclaredField("gameBoard");
      gameBoardField.setAccessible(true);
      gameBoardField.set(game, mockedGameBoard);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setIsTradedOrPlayerProduced(Game game, boolean isTradedOrPlayerProduced) {
    game.getCurrentPlayer().setTradedOrPlayerProduced(isTradedOrPlayerProduced);
  }

  public static void spyCurrentPlayer(Game game) {
    try {
      var field = GameImpl.class.getDeclaredField("currentPlayer");
      field.setAccessible(true);
      field.set(game, Mockito.spy(game.getCurrentPlayer()));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setWinner(Game game) {
    try {
      var field = GameImpl.class.getDeclaredField("winner");
      field.setAccessible(true);
      field.set(game, game.getCurrentPlayer());
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}