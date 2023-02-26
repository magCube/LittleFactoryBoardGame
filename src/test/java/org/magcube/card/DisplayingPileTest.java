package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

public class DisplayingPileTest {

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideFirstTierResourcesPiles")
  void takeCardTest(DisplayingPile<BasicResource> pile) {
    var oldDisplaying = List.copyOf(pile.getDisplaying());
    assertEquals(5, oldDisplaying.size());
    assertTrue(oldDisplaying.stream().noneMatch(ArrayList::isEmpty));
    var oldFirstCards = oldDisplaying.get(0);
    var oldFirstCardsSize = oldFirstCards.size();
    assertFalse(oldFirstCards.isEmpty());
    pile.takeCard(oldFirstCards.get(0));
    if (oldFirstCardsSize == 1) {
      assertNotSame(oldDisplaying.get(0), pile.getDisplaying().get(0));
    } else {
      assertEquals(oldFirstCardsSize - 1, pile.getDisplaying().get(0).size());
    }
  }

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideFirstTierResourcesPiles")
  void insertCardTest(DisplayingPile<BasicResource> pile) {
    var originalDeckSize = pile.deckSize();
    var cardToInsert = BasicResource.BasicResourceBuilder()
        .value(1)
        .typeId(1)
        .name("test")
        .cost(new Card[1])
        .build();
    var cardToInsert2 = BasicResource.BasicResourceBuilder()
        .value((int) (Math.random() * 10))
        .typeId((int) (Math.random() * 100))
        .name("test random")
        .cost(new Card[1])
        .build();
    pile.insertCard(List.of(cardToInsert, cardToInsert2));
    assertEquals(originalDeckSize + 2, pile.deckSize());
  }

  @Test
  void refillCardStopWhenDeckUsesUp() throws DisplayPileException {
    var firstTierResources = new ArrayList<BasicResource>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          BasicResource.BasicResourceBuilder().value(1).cost(new Card[1]).name(String.valueOf(i))
              .typeId(1)
              .build()
      );
    }
    var pile = new DisplayingPile<>(firstTierResources);
    assertEquals(0, pile.deckSize());
    assertEquals(1, pile.getDisplaying().size());
  }
}
