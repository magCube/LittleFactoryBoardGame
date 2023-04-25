package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CardTest {

  @Test
  void resourceCardIsIdenticalTest() {
    CardIdentity identity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    CardIdentity identity2 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    CardIdentity identity3 = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    CardIdentity identity4 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);

    ResourceCard card1 = ResourceCard.builder()
        .cardIdentity(identity1)
        .name("Card 1")
        .value(3)
        .build();
    ResourceCard card2 = ResourceCard.builder()
        .cardIdentity(identity2)
        .name("Card 2")
        .value(3)
        .build();
    ResourceCard card3 = ResourceCard.builder()
        .cardIdentity(identity3)
        .name("Card 2")
        .value(5)
        .build();
    ResourceCard card4 = ResourceCard.builder()
        .cardIdentity(identity4)
        .name("Card 4")
        .value(5)
        .build();

    assertTrue(card1.isIdentical(card1)); // same object
    assertTrue(card1.isIdentical(card2)); // same identity
    assertFalse(card1.isIdentical(card3)); // different identity
    assertFalse(card1.isIdentical(card4)); // different identity
  }


  @Test
  void buildingCardIsIdenticalTest() {
    CardIdentity identity1 = new CardIdentity(CardType.BUILDING, 1);
    CardIdentity identity2 = new CardIdentity(CardType.BUILDING, 1);
    CardIdentity identity3 = new CardIdentity(CardType.BUILDING, 2);

    BuildingCard card1 = BuildingCard.builder()
        .cardIdentity(identity1)
        .name("Card 1")
        .value(3)
        .build();
    BuildingCard card2 = BuildingCard.builder()
        .cardIdentity(identity2)
        .name("Card 2")
        .value(3)
        .build();
    BuildingCard card3 = BuildingCard.builder()
        .cardIdentity(identity3)
        .name("Card 2")
        .value(5)
        .build();

    assertTrue(card1.isIdentical(card1)); // same object
    assertTrue(card1.isIdentical(card2)); // same identity
    assertFalse(card1.isIdentical(card3)); // different identity
  }
}
