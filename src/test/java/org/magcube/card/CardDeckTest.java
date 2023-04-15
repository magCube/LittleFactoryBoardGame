package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.exception.NumOfPlayersException;

class CardDeckTest {

  @ParameterizedTest
  @MethodSource
  void deckSizeShouldMatchTest(int numOfPlayers, Method m) throws NumOfPlayersException {
    assertEquals(sumHelper(CardQuantity.basicResource, m),
        CardDeck.get(numOfPlayers).basicResource.size());
    assertEquals(sumHelper(CardQuantity.level1Resource, m),
        CardDeck.get(numOfPlayers).level1Resource.size());
    assertEquals(sumHelper(CardQuantity.level2Resource, m),
        CardDeck.get(numOfPlayers).level2Resource.size());
    assertEquals(sumHelper(CardQuantity.building, m), CardDeck.get(numOfPlayers).building.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {2, 3, 4})
  void shouldHaveDataTest(int numOfPlayers) throws NumOfPlayersException {
    assertNotNull(CardDeck.get(numOfPlayers));
  }

  @ParameterizedTest
  @ValueSource(ints = {-5, -1, 0, 1, 5})
  void shouldThrowErrTest(int numOfPlayers) {
    assertThrows(NumOfPlayersException.class, () -> CardDeck.get(numOfPlayers));
  }

  // note: this test only check if the card is shallow clone
  @Test
  void isCloneTest() throws NumOfPlayersException {
    List<ResourceCard> resourceCards = new ArrayList<>();
    resourceCards.addAll(CardDeck.get(2).basicResource);
    resourceCards.addAll(CardDeck.get(2).level1Resource);
    resourceCards.addAll(CardDeck.get(2).level2Resource);
    resourceCards.addAll(CardDeck.get(3).basicResource);
    resourceCards.addAll(CardDeck.get(3).level1Resource);
    resourceCards.addAll(CardDeck.get(3).level2Resource);
    resourceCards.addAll(CardDeck.get(4).basicResource);
    resourceCards.addAll(CardDeck.get(4).level1Resource);
    resourceCards.addAll(CardDeck.get(4).level2Resource);

    assertTrue(resourceCards.size() > 0);
    assertTrue(allDistinctElements(resourceCards));

    List<BuildingCard> buildingCards = new ArrayList<>();
    buildingCards.addAll(CardDeck.get(2).building);
    buildingCards.addAll(CardDeck.get(3).building);
    buildingCards.addAll(CardDeck.get(4).building);

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
        Arguments.of(2, CardQuantity.class.getDeclaredMethod("getTwoPlayers")),
        Arguments.of(3, CardQuantity.class.getDeclaredMethod("getThreePlayers")),
        Arguments.of(4, CardQuantity.class.getDeclaredMethod("getFourPlayers"))
    );
  }
}