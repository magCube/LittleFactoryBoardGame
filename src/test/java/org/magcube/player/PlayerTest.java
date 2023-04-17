package org.magcube.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.magcube.card.BuildingCard;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class PlayerTest {

  @Test
  void userNameOperationsTest() {
    String name = "test12312412312";
    var user = new Player("1", name);
    assertEquals(name, user.getName());
  }

  @Test
  void userGiveAndTakeCardAndPointTest() {
    var user = new Player("1", "test12312412312");
    assertEquals(0, user.getPoints());
    var card1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1)
        .build();
    var card2 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 100))
        .build();
    var card3 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2))
        .value(2)
        .build();
    var card4 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 101))
        .build();

    var resources = List.of(card1, card3);
    var buildings = List.of(card2, card4);

    user.takeResourceCards(resources);
    user.takeBuildingCards(buildings);

    assertEquals(2, user.getResources().size());
    assertTrue(user.getResources().containsAll(resources));
    assertEquals(2, user.getBuildings().size());
    assertTrue(user.getBuildings().containsAll(buildings));

    assertEquals(2, user.getPoints());
    user.addPoints(1);
    assertEquals(3, user.getPoints());
    user.addPoints(10);
    assertEquals(13, user.getPoints());

    user.discardCards(List.of(card1));
    assertEquals(1, user.getResources().size());
    assertEquals(card3, user.getResources().get(0));
  }

  @Test
  void userOwnCardTest() {
    var user = new Player("1", "player1");
    assertEquals(0, user.getPoints());
    var card1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1)
        .build();
    var anotherCard1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1)
        .build();
    var card2 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2))
        .value(1)
        .build();

    assertFalse(user.ownCard(card1));
    user.takeResourceCards(List.of(card1));
    assertTrue(user.ownCard(card1));
    assertTrue(user.ownCard(anotherCard1));
    assertFalse(user.ownCard(card2));
  }

  @Test
  void userOwnFactoryCardTest() {
    var user = new Player("1", "player1");
    assertEquals(0, user.getPoints());
    var card1 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 1))
        .value(6)
        .build();
    var anotherCard1 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 1))
        .value(6)
        .build();
    var card2 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 2))
        .value(6)
        .build();

    assertFalse(user.ownCard(card1));
    user.takeBuildingCards(List.of(card1));
    assertTrue(user.ownCard(card1));
    assertTrue(user.ownCard(anotherCard1));
    assertFalse(user.ownCard(card2));
  }

  @Test
  void isOwnAllResourcesTest() {
    var card1Builder = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1);
    var card2Builder = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2))
        .value(1);
    var card3Builder = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3))
        .value(1);

    var ownCard1a = card1Builder.build();
    var ownCard1b = card1Builder.build();
    var ownCard2 = card2Builder.build();
    var user = new Player("1", "player1");
    user.takeResourceCards(List.of(ownCard1a, ownCard1b, ownCard2));

    var testCard1a = card1Builder.build();
    var testCard1b = card1Builder.build();
    var testCard2a = card2Builder.build();
    var testCard2b = card2Builder.build();
    var testCard3 = card3Builder.build();

    assertTrue(user.isOwnAllResources(List.of(testCard1a)));
    assertTrue(user.isOwnAllResources(List.of(testCard1b)));
    assertTrue(user.isOwnAllResources(List.of(testCard1a, testCard1b)));
    assertTrue(user.isOwnAllResources(List.of(testCard1a, testCard2a)));
    assertTrue(user.isOwnAllResources(List.of(testCard1a, testCard1b, testCard2a)));
    assertTrue(user.isOwnAllResources(List.of(testCard2b, testCard1a, testCard1b)));

    assertFalse(user.isOwnAllResources(List.of(testCard3)));
    assertFalse(user.isOwnAllResources(List.of(testCard2a, testCard2b)));
    assertFalse(user.isOwnAllResources(List.of(testCard1a, testCard1b, testCard2a, testCard2b)));
  }
}
