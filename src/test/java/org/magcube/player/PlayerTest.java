package org.magcube.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    assertEquals(0, user.points());
    var card1 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1))
        .value(1)
        .build();
    var card2 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 100))
        .points(1)
        .build();
    var card3 = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2))
        .value(2)
        .build();
    var card4 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 101))
        .points(1)
        .build();

    var resources = List.of(card1, card3);
    var buildings = List.of(card2, card4);

    user.takeResourceCards(resources);
    user.takeBuildingCards(buildings);

    assertEquals(2, user.getResources().size());
    assertTrue(user.getResources().containsAll(resources));
    assertEquals(2, user.getBuildings().size());
    assertTrue(user.getBuildings().containsAll(buildings));

    assertEquals(2, user.points());
    user.addPoints(1);
    assertEquals(3, user.points());
    user.addPoints(10);
    assertEquals(13, user.points());

    user.discardCards(List.of(card1));
    assertEquals(1, user.getResources().size());
    assertEquals(card3, user.getResources().get(0));
  }

  @Test
  void equivalentResourcesTest() {
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
    var player = new Player("1", "player1");
    player.takeResourceCards(List.of(ownCard1a, ownCard1b, ownCard2));

    var testCardIdentity1a = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var testCardIdentity1b = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var testCardIdentity2a = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    var testCardIdentity2b = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    var testCardIdentity3 = new CardIdentity(CardType.BASIC_RESOURCE, 3);

    assertEquals(List.of(ownCard1a), player.equivalentResources(List.of(testCardIdentity1a)));
    assertEquals(List.of(ownCard1b), player.equivalentResources(List.of(testCardIdentity1b)));
    assertEquals(List.of(ownCard1a, ownCard1b), player.equivalentResources(List.of(testCardIdentity1a, testCardIdentity1b)));
    assertEquals(List.of(ownCard1a, ownCard2), player.equivalentResources(List.of(testCardIdentity1a, testCardIdentity2a)));
    assertEquals(List.of(ownCard1a, ownCard1b, ownCard2),
        player.equivalentResources(List.of(testCardIdentity1a, testCardIdentity1b, testCardIdentity2a)));
    assertEquals(List.of(ownCard2, ownCard1a, ownCard1b),
        player.equivalentResources(List.of(testCardIdentity2b, testCardIdentity1a, testCardIdentity1b)));

    assertNull(player.equivalentResources(List.of(testCardIdentity3)));
    assertNull(player.equivalentResources(List.of(testCardIdentity2a, testCardIdentity2b)));
    assertNull(player.equivalentResources(List.of(testCardIdentity1a, testCardIdentity1b, testCardIdentity2a, testCardIdentity2b)));
  }

  @Test
  void equivalentBuildingTest() {
    var building1 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 1))
        .build();
    var building2 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 2))
        .build();
    var building3 = BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 3))
        .build();

    var player = new Player("1", "player1");
    player.takeBuildingCards(List.of(building1, building2, building3));

    var testCardIdentity1 = new CardIdentity(CardType.BUILDING, 1);
    var testCardIdentity2 = new CardIdentity(CardType.BUILDING, 2);
    var testCardIdentity3 = new CardIdentity(CardType.BUILDING, 3);
    var testCardIdentity4 = new CardIdentity(CardType.BUILDING, 4);
    var testCardIdentity5 = new CardIdentity(CardType.BUILDING, 5);

    assertEquals(building1, player.equivalentBuilding(testCardIdentity1));
    assertEquals(building2, player.equivalentBuilding(testCardIdentity2));
    assertEquals(building3, player.equivalentBuilding(testCardIdentity3));
    assertNull(player.equivalentBuilding(testCardIdentity4));
    assertNull(player.equivalentBuilding(testCardIdentity5));
  }
}
