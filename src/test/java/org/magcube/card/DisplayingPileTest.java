package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.Main;
import org.magcube.exception.DisplayPileException;

public class DisplayingPileTest {

  private static Stream<DisplayingPile<FirstTierResource>> provideFirstTierResourcesPiles()
      throws DisplayPileException {
    return Stream.of(
        getFirstTierResourcesPile(),
        getTestingFirstTierResourcesPile()
    );
  }

  private static DisplayingPile<FirstTierResource> getFirstTierResourcesPile()
      throws DisplayPileException {
    return new DisplayingPile<>(Main.firstTierResources);
  }

  private static DisplayingPile<FirstTierResource> getTestingFirstTierResourcesPile()
      throws DisplayPileException {
    var firstTierResources = new ArrayList<FirstTierResource>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          FirstTierResource.firstTierBuilder().value(1).cost(new Card[1]).name(String.valueOf(i))
              .typeId(i % 5)
              .build()
      );
    }
    return new DisplayingPile<>(firstTierResources);
  }

  @ParameterizedTest
  @MethodSource("provideFirstTierResourcesPiles")
  void takeCardTest(DisplayingPile<FirstTierResource> pile) throws DisplayPileException {
    var oldDisplaying = List.copyOf(pile.getDisplaying());
    assertEquals(5, oldDisplaying.size());
    assertTrue(oldDisplaying.stream().noneMatch(ArrayList::isEmpty));
    var oldFirstCards = oldDisplaying.get(0);
    var oldFirstCardsSize = oldFirstCards.size();
    assertFalse(oldFirstCards.isEmpty());
    var result = pile.takeCard(oldFirstCards.get(0));
    assertTrue(result);
    if (oldFirstCardsSize == 1) {
      assertNotSame(oldDisplaying.get(0), pile.getDisplaying().get(0));
    } else {
      assertEquals(oldFirstCardsSize - 1, pile.getDisplaying().get(0).size());
    }
  }
}
