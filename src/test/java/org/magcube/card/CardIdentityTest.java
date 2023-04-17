package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}
