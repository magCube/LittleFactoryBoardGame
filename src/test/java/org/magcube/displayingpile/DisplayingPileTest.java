package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

public class DisplayingPileTest {

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideBasicResourcesPiles")
  void initializeDisplayingPileTest(DisplayingPile<Card> pile) {
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(0, pile.discardPileSize());
  }

  @Test
  void initializeDisplayingPileShouldThrowTest() {
    var basicResources = buildMockDeckWithOnlyOneTypeOfCard();
    basicResources.add(ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.LEVEL_1_RESOURCE, 1))
        .value(1)
        .name("test")
        .build());
    assertThrows(DisplayPileException.class, () -> new DisplayingPile<>(basicResources));
  }

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideBasicResourcesPiles")
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
  @MethodSource("org.magcube.TestUtils#provideBasicResourcesPiles")
  void discardCardTest(DisplayingPile<Card> pile) throws DisplayPileException {
    var originalDeckSize = pile.deckSize();
    var card1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1)
        .name("test")
        .build();
    var card2 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, (int) (Math.random() * 100)))
        .value((int) (Math.random() * 10))
        .name("test random")
        .build();
    pile.discardCard(List.of(card1, card2));
    assertEquals(originalDeckSize, pile.deckSize());
    assertEquals(2, pile.discardPileSize());
    assertTrue(pile.getDiscardPile().containsAll(List.of(card1, card2)));
  }

  @ParameterizedTest
  @MethodSource("org.magcube.TestUtils#provideBasicResourcesPiles")
  void discardCardShouldThrowTest(DisplayingPile<Card> pile) {
    var originalDeckSize = pile.deckSize();
    var card1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.LEVEL_1_RESOURCE, 1))
        .value(1)
        .name("test")
        .build();
    assertThrows(DisplayPileException.class, () -> pile.discardCard(card1));
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
    var basicResources = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new DisplayingPile<>(basicResources);
    assertEquals(0, pile.deckSize());
    var card4Discard = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 91276))
        .name("unique card for testing")
        .value(1)
        .build();
    pile.discardCard(card4Discard);
    assertEquals(1, pile.discardPileSize());
    pile.refillCards();
    assertEquals(0, pile.discardPileSize());
    assertEquals(2, pile.getDisplaying().size());
  }

  private ArrayList<ResourceCard> buildMockDeckWithOnlyOneTypeOfCard() {
    var basicResources = new ArrayList<ResourceCard>();
    for (var i = 0; i < 10; i++) {
      basicResources.add(ResourceCard.builder()
          .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }
    return basicResources;
  }
}
