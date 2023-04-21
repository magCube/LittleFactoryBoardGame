package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class CardIdentityTest {

  @ParameterizedTest
  @EnumSource(CardType.class)
  void testEquals(CardType cardType) {
    CardIdentity card1 = new CardIdentity(cardType, 1);
    CardIdentity card2 = new CardIdentity(cardType, 1);
    CardIdentity card3 = new CardIdentity(cardType, 2);

    assertEquals(card1, card1); // same object
    assertEquals(card1, card2); // same values
    assertNotEquals(null, card1); // null check
    assertNotEquals(card1, new Object()); // different class check
    assertNotEquals(card1, card3); // different type id

    // test inequality with different card types
    for (CardType otherType : CardType.values()) {
      if (otherType != cardType) {
        assertNotEquals(card1, new CardIdentity(otherType, 1));
      }
    }
  }

  @Test
  void compareToTest1() {
    CardIdentity card1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    CardIdentity card2 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    CardIdentity card3 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
    CardIdentity card4 = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    CardIdentity card5 = new CardIdentity(CardType.BUILDING, 3);

    // Same cardType and typeId
    assertEquals(0, card1.compareTo(card2));

    // Different cardType, same typeId
    assertTrue(card1.compareTo(card3) < 0);
    assertTrue(card3.compareTo(card1) > 0);

    // Same cardType, different typeId
    assertTrue(card1.compareTo(card4) < 0);
    assertTrue(card4.compareTo(card1) > 0);

    // Different cardType and typeId
    assertTrue(card3.compareTo(card5) < 0);
    assertTrue(card5.compareTo(card1) > 0);
  }

  @Test
  void compareToTest2() {
    List<CardIdentity> cards = Arrays.asList(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BUILDING, 3),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 2)
    );

    List<CardIdentity> expectedSortedCards = Arrays.asList(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 2),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
        new CardIdentity(CardType.BUILDING, 3)
    );

    Collections.sort(cards);
    assertEquals(expectedSortedCards, cards);
  }
}
