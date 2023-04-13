package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CardTest {

  @Test
  void sameCardTest() {
    var card1 = new ResourceCard(CardType.BASIC_RESOURCE, 1, "", 1, null, null);
    var card2 = new ResourceCard(CardType.BASIC_RESOURCE, 1, "", 1, null, null);
    var card3 = new ResourceCard(CardType.BASIC_RESOURCE, 2, "", 1, null, null);
    var card4 = new ResourceCard(CardType.LEVEL_1_RESOURCE, 1, "", 1, null, null);
    var card5 = new ResourceCard(CardType.LEVEL_1_RESOURCE, 1, "", 1, null, null);

    assertTrue(card1.sameCard(card1));
    assertTrue(card1.sameCard(card2));
    assertTrue(card2.sameCard(card1));
    assertTrue(card4.sameCard(card5));
    assertTrue(card5.sameCard(card4));

    assertFalse(card1.sameCard(card3));
    assertFalse(card3.sameCard(card1));
    assertFalse(card1.sameCard(card5));
    assertFalse(card5.sameCard(card1));
    assertFalse(card1.sameCard(card4));
    assertFalse(card4.sameCard(card1));
  }
}
