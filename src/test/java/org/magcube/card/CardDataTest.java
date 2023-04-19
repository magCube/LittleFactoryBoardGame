package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CardDataTest {

  @Test
  void cardDataTest() {
    assertEquals(5, CardData.basicResource.size());
    assertEquals(9, CardData.level1Resource.size());
    assertEquals(9, CardData.level2Resource.size());
    assertEquals(38, CardData.building.size());
  }

  @ParameterizedTest
  @MethodSource
  <T extends Card> void cardTypeCheckingTest(Class<T> clazz, List<T> data, CardType cardType) {
    assertTrue(data.stream().allMatch(x -> clazz.isInstance(x) && x.cardType() == cardType));
  }

  static private Stream<Arguments> cardTypeCheckingTest() {
    return Stream.of(
        Arguments.of(ResourceCard.class, CardData.basicResource, CardType.BASIC_RESOURCE),
        Arguments.of(ResourceCard.class, CardData.level1Resource, CardType.LEVEL_ONE_RESOURCE),
        Arguments.of(ResourceCard.class, CardData.level2Resource, CardType.LEVEL_TWO_RESOURCE),
        Arguments.of(BuildingCard.class, CardData.building, CardType.BUILDING)
    );
  }
}
