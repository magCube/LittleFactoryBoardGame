package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CardsTest {

  @Test
  void isCardIdentitiesTest1() {
    List<CardIdentity> actual = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));
    List<CardIdentity> testing = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1));

    assertTrue(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(new CardIdentity(CardType.BASIC_RESOURCE, 2));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));

    actual = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2));

    testing = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2));
    assertTrue(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
        new CardIdentity(CardType.BASIC_RESOURCE, 1));
    assertTrue(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
        new CardIdentity(CardType.BASIC_RESOURCE, 2));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));

    actual = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1));

    testing = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1));
    assertTrue(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 2));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));

    testing = List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 2));
    assertFalse(Cards.isCardIdentitiesSame(actual, testing));
  }
}