package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.player.NumOfPlayers;

class CardDeckTest {

  @ParameterizedTest
  @MethodSource
  void deckSizeShouldMatchTest(NumOfPlayers numOfPlayers, Method m) {
    assertEquals(sumHelper(CardQuantity.basicResource, m),
        CardDeck.get(numOfPlayers).basicResource.size());
    assertEquals(sumHelper(CardQuantity.levelOneResource, m),
        CardDeck.get(numOfPlayers).levelOneResource.size());
    assertEquals(sumHelper(CardQuantity.levelTwoResource, m),
        CardDeck.get(numOfPlayers).levelTwoResource.size());
    assertEquals(sumHelper(CardQuantity.building, m), CardDeck.get(numOfPlayers).building.size());
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void shouldHaveDataTest(NumOfPlayers numOfPlayers) {
    assertNotNull(CardDeck.get(numOfPlayers));
  }

  // note: this test only check if the card is shallow clone
  @Test
  void isCloneTest() {
    List<ResourceCard> resourceCards = new ArrayList<>();
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).levelTwoResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).levelTwoResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).levelTwoResource);

    assertTrue(resourceCards.size() > 0);
    assertTrue(allDistinctElements(resourceCards));

    List<BuildingCard> buildingCards = new ArrayList<>();
    buildingCards.addAll(CardDeck.get(NumOfPlayers.TWO).building);
    buildingCards.addAll(CardDeck.get(NumOfPlayers.THREE).building);
    buildingCards.addAll(CardDeck.get(NumOfPlayers.FOUR).building);

    assertTrue(buildingCards.size() > 0);
    assertTrue(allDistinctElements(buildingCards));
  }

  private <T> boolean allDistinctElements(List<T> list) {
    for (int i = 0; i < list.size(); i++) {
      for (int j = i + 1; j < list.size(); j++) {
        if (list.get(i) == list.get(j)) {
          return false;
        }
      }
    }
    return true;
  }

  private int sumHelper(List<CardQuantity> cardQuantities, Method method) {
    return cardQuantities.stream()
        .mapToInt(card -> {
          try {
            return (int) method.invoke(card);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .sum();
  }

  static private Stream<Arguments> deckSizeShouldMatchTest() throws NoSuchMethodException {
    return Stream.of(
        Arguments.of(NumOfPlayers.TWO, CardQuantity.class.getDeclaredMethod("twoPlayers")),
        Arguments.of(NumOfPlayers.THREE, CardQuantity.class.getDeclaredMethod("threePlayers")),
        Arguments.of(NumOfPlayers.FOUR, CardQuantity.class.getDeclaredMethod("fourPlayers"))
    );
  }
}