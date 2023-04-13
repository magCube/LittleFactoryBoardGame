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
  void initializeDisplayingPileTest(DisplayingPile<Card> pile) {
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(0, pile.discardPileSize());
  }

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideFirstTierResourcesPiles")
  void takeCardTest(DisplayingPile<Card> pile) {
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
  void insertCardTest(DisplayingPile<Card> pile) {
    var originalDeckSize = pile.deckSize();
    var cardToInsert = ResourceCard.builder()
        .value(1)
        .typeId(1)
        .name("test")
        .build();
    var cardToInsert2 = ResourceCard.builder()
        .value((int) (Math.random() * 10))
        .typeId((int) (Math.random() * 100))
        .name("test random")
        .build();
    pile.discardCard(List.of(cardToInsert, cardToInsert2));
    assertEquals(originalDeckSize, pile.deckSize());
    assertEquals(2, pile.discardPileSize());
  }

  @Test
  void refillCardStopWhenDeckUsesUp() throws DisplayPileException {
    var firstTierResources = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new DisplayingPile<>(firstTierResources);
    assertEquals(0, pile.deckSize());
    assertEquals(1, pile.getDisplaying().size());
  }

  @Test
  void refillCardFromDiscardPileWhenDeckUsedUp() throws DisplayPileException {
    var firstTierResources = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new DisplayingPile<>(firstTierResources);
    assertEquals(0, pile.deckSize());
    var card4Discard = ResourceCard.builder()
        .value(1)
        .name("unique card for testing")
        .typeId(91276)
        .build();
    pile.discardCard(card4Discard);
    assertEquals(1, pile.discardPileSize());
    pile.refillCards();
    assertEquals(0, pile.discardPileSize());
    assertEquals(2, pile.getDisplaying().size());
  }

  private ArrayList<ResourceCard> buildMockDeckWithOnlyOneTypeOfCard() {
    var firstTierResources = new ArrayList<ResourceCard>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          ResourceCard.builder().value(1).name(String.valueOf(i))
              .typeId(1)
              .build()
      );
    }
    return firstTierResources;
  }
}
