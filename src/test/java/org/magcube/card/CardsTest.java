package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.magcube.enums.CardType;

class CardsTest {

  @Nested
  class testIsCardIdentitiesSame {

    @Test
    void withEmptyActualArrays() {
      CardIdentity[] actual = {};
      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{}));
      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{new CardIdentity(CardType.BASIC_RESOURCE, 1)}));
    }

    @Test
    void withSingleCardIdentity() {
      CardIdentity[] actual = {new CardIdentity(CardType.BASIC_RESOURCE, 1)};

      assertTrue(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{new CardIdentity(CardType.BASIC_RESOURCE, 1)}));
      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{new CardIdentity(CardType.BASIC_RESOURCE, 2)}));
      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)}));
    }

    @Test
    void withMultipleCardIdentities() {
      CardIdentity[] actual = {
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)
      };

      assertTrue(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2)
      }));

      assertTrue(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 2)
      }));
    }

    @Test
    void withDuplicateCardIdentities() {
      CardIdentity[] actual = {
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      };

      assertTrue(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 2)
      }));

      assertFalse(Cards.isCardIdentitiesSame(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.BASIC_RESOURCE, 2)
      }));
    }
  }

  @Nested
  class isOneDimCardIdentitiesMatch {

    @Test
    void testEmpty() {
      CardIdentity[] actual = {};
      CardIdentity[] testing = {};
      assertFalse(Cards.isOneDimCardIdentitiesMatch(actual, testing));

      testing = new CardIdentity[]{new CardIdentity(CardType.BASIC_RESOURCE, 1)};
      assertFalse(Cards.isOneDimCardIdentitiesMatch(actual, testing));
    }

    @Test
    void testDifferentCase() {
      CardIdentity[] actual = {
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
      };

      assertTrue(Cards.isOneDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
      }));

      assertTrue(Cards.isOneDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isOneDimCardIdentitiesMatch(actual, new CardIdentity[]{}));

      assertFalse(Cards.isOneDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
      }));

      assertFalse(Cards.isOneDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
      }));
    }
  }

  @Nested
  class isTwoDimCardIdentitiesMatch {

    @Test
    void testEmpty() {
      CardIdentity[][] actual = {};
      CardIdentity[] testing = {};
      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, testing));

      testing = new CardIdentity[]{new CardIdentity(CardType.BASIC_RESOURCE, 1)};
      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, testing));
    }

    @Test
    void testSingleOption() {
      CardIdentity[][] actual = {{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
      }};

      assertTrue(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
      }));

      assertTrue(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{}));

      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1)
      }));

      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 1),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
      }));
    }

    @Test
    void testCostMatchTwoOptions() {
      CardIdentity[][] actual = {
          {new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 4)},
          {new CardIdentity(CardType.BASIC_RESOURCE, 3), new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)}
      };

      assertTrue(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 4)
      }));

      assertTrue(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 3),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)
      }));

      assertTrue(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5),
          new CardIdentity(CardType.BASIC_RESOURCE, 3)
      }));

      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{}));

      assertFalse(Cards.isTwoDimCardIdentitiesMatch(actual, new CardIdentity[]{
          new CardIdentity(CardType.BASIC_RESOURCE, 3)
      }));
    }
  }
}