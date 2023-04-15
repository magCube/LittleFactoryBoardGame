package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.exception.CardQuantityException;
import org.magcube.exception.NumOfPlayersException;

class CardQuantityTest {

  @Test
  void allDataIsNonZeroTest() {
    assertFalse(CardQuantity.basicResource.stream().anyMatch(x ->
        x.getTwoPlayers() == 0 ||
            x.getThreePlayers() == 0 ||
            x.getFourPlayers() == 0
    ));
    assertFalse(CardQuantity.level1Resource.stream().anyMatch(x ->
        x.getTwoPlayers() == 0 ||
            x.getThreePlayers() == 0 ||
            x.getFourPlayers() == 0
    ));

    // for level 2 resource and building, they should be always 1
    assertTrue(CardQuantity.level2Resource.stream().allMatch(x ->
        x.getTwoPlayers() == 1 &&
            x.getThreePlayers() == 1 &&
            x.getFourPlayers() == 1
    ));
    assertTrue(CardQuantity.building.stream().allMatch(x ->
        x.getTwoPlayers() == 1 &&
            x.getThreePlayers() == 1 &&
            x.getFourPlayers() == 1
    ));
  }

  @ParameterizedTest
  @MethodSource("cardQuantityDataProvider")
  void allCardTypeIsSameTest(List<CardQuantity> cardQuantities) {
    assertTrue(cardQuantities.stream()
        .allMatch(x -> x.getCardType() == cardQuantities.get(0).getCardType()));
  }

  // no duplicate data identifier (same cardType and typeId)
  @ParameterizedTest
  @MethodSource("cardQuantityDataProvider")
  void noDuplicateDataIdentifierTest(List<CardQuantity> data) {
    long uniqueCount = data.stream()
        .map(x -> x.getCardType().toString() + "-" + x.getTypeId())
        .distinct()
        .count();
    assertEquals(data.size(), uniqueCount);
  }

  // for each card data, there should be a corresponding quantity data (same cardType and typeId)
  @ParameterizedTest
  @MethodSource
  void quantityDataCoverAllCardDataTest(List<CardQuantity> data, List<? extends Card> cardData) {
    assertTrue(cardData.stream().allMatch(card ->
        data.stream().anyMatch(cardQuantity ->
            cardQuantity.getCardType() == card.getCardType() &&
                cardQuantity.getTypeId() == card.getTypeId()
        )
    ));
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 3, 4})
  void getQuantityTest(int numOfPlayers) throws CardQuantityException, NumOfPlayersException {
    assertTrue(CardQuantity.getQuantity(CardType.BASIC_RESOURCE, 1, numOfPlayers) > 0);
    assertTrue(CardQuantity.getQuantity(CardType.LEVEL_1_RESOURCE, 1, numOfPlayers) > 0);
    assertTrue(CardQuantity.getQuantity(CardType.LEVEL_2_RESOURCE, 1, numOfPlayers) > 0);
    assertTrue(CardQuantity.getQuantity(CardType.BUILDING, 1, numOfPlayers) > 0);
  }

  @Test
  void getQuantityExceptionTest() {
    assertThrows(CardQuantityException.class,
        () -> CardQuantity.getQuantity(CardType.BASIC_RESOURCE, 99999, 4));
  }

  @Test
  void getQuantityForPlayerCountTest() throws CardQuantityException, NumOfPlayersException {
    var cardQuantity = new CardQuantity(CardType.BASIC_RESOURCE, 1, 5, 7, 9);
    assertEquals(5, cardQuantity.getQuantityForNumOfPlayers(2));
    assertEquals(7, cardQuantity.getQuantityForNumOfPlayers(3));
    assertEquals(9, cardQuantity.getQuantityForNumOfPlayers(4));
  }

  @ParameterizedTest
  @ValueSource(ints = {-5, -1, 0, 1, 5})
  void getQuantityForPlayerCountExceptionTest(int numOfPlayers) {
    var cardQuantity = new CardQuantity(CardType.BASIC_RESOURCE, 1, 5, 7, 9);
    assertThrows(NumOfPlayersException.class,
        () -> cardQuantity.getQuantityForNumOfPlayers(numOfPlayers));
  }

  private static Stream<Arguments> cardQuantityDataProvider() {
    return Stream.of(
        Arguments.of(CardQuantity.basicResource),
        Arguments.of(CardQuantity.level1Resource),
        Arguments.of(CardQuantity.level2Resource),
        Arguments.of(CardQuantity.building)
    );
  }

  private static Stream<Arguments> quantityDataCoverAllCardDataTest() {
    return Stream.of(
        Arguments.of(CardQuantity.basicResource, CardData.basicResource),
        Arguments.of(CardQuantity.level1Resource, CardData.level1Resource),
        Arguments.of(CardQuantity.level2Resource, CardData.level2Resource),
        Arguments.of(CardQuantity.building, CardData.building)
    );
  }
}
