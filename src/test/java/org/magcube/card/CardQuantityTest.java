package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

  // no duplicate data identifier (same cardType and typeId)
  @ParameterizedTest
  @MethodSource
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

  static private Stream<Arguments> noDuplicateDataIdentifierTest() {
    return Stream.of(
        Arguments.of(CardQuantity.basicResource),
        Arguments.of(CardQuantity.level1Resource),
        Arguments.of(CardQuantity.level2Resource),
        Arguments.of(CardQuantity.building)
    );
  }

  static private Stream<Arguments> quantityDataCoverAllCardDataTest() {
    return Stream.of(
        Arguments.of(CardQuantity.basicResource, CardData.basicResource),
        Arguments.of(CardQuantity.level1Resource, CardData.level1Resource),
        Arguments.of(CardQuantity.level2Resource, CardData.level2Resource),
        Arguments.of(CardQuantity.building, CardData.building)
    );
  }
}
