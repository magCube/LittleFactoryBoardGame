package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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

  @Test
  void testCostMatchEmpty() {
    ResourceCard card = ResourceCard.builder()
        .cost(new CardIdentity[][]{})
        .build();
    assertFalse(card.costMatch(List.of()));
  }

  @Test
  void testCostMatchSingleOption() {
    // case 2: this.cost is one option i.e. outer array length is 1
    ResourceCard card = ResourceCard.builder()
        .cost(new CardIdentity[][]{{
            new CardIdentity(CardType.BASIC_RESOURCE, 1),
            new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
        }})
        .build();

    assertTrue(card.costMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
    )));

    assertTrue(card.costMatch(List.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
        new CardIdentity(CardType.BASIC_RESOURCE, 1)
    )));

    assertFalse(card.costMatch(List.of()));

    assertFalse(card.costMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1)
    )));

    assertFalse(card.costMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
    )));
  }

  @Test
  void testCostMatchTwoOptions() {
    // case 3: this.cost is two options i.e. outer array length is 2
    ResourceCard card = ResourceCard.builder()
        .cost(new CardIdentity[][]{
            {new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 4)},
            {new CardIdentity(CardType.BASIC_RESOURCE, 3), new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)}
        })
        .build();

    assertTrue(card.costMatch(List.of(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 4))));

    assertTrue(card.costMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 3),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)
    )));

    assertTrue(card.costMatch(List.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5),
        new CardIdentity(CardType.BASIC_RESOURCE, 3)
    )));

    assertFalse(card.costMatch(List.of()));

    assertFalse(card.costMatch(List.of(new CardIdentity(CardType.BASIC_RESOURCE, 3))));
  }

  @Test
  void testCapitalMatchEmpty() {
    ResourceCard card = ResourceCard.builder()
        .capital(new CardIdentity[]{})
        .build();
    assertFalse(card.capitalMatch(List.of()));
  }

  @Test
  void testCapitalMatchSingleOption() {
    ResourceCard card = ResourceCard.builder()
        .capital(new CardIdentity[]{
            new CardIdentity(CardType.BASIC_RESOURCE, 1),
            new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
        })
        .build();

    assertTrue(card.capitalMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)
    )));

    assertTrue(card.capitalMatch(List.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
        new CardIdentity(CardType.BASIC_RESOURCE, 1)
    )));

    assertFalse(card.capitalMatch(List.of()));

    assertFalse(card.capitalMatch(List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1))));

    assertFalse(card.capitalMatch(List.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)
    )));
  }
}
