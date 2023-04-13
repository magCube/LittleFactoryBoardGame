package org.magcube.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.magcube.card.BuildingCard;
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
        .cardType(CardType.BASIC_RESOURCE)
        .typeId(1)
        .value(1)
        .build();
    var card2 = BuildingCard.builder()
        .cardType(CardType.BUILDING)
        .typeId(100)
        .build();
    var card3 = ResourceCard.builder()
        .cardType(CardType.BASIC_RESOURCE)
        .typeId(2)
        .value(2)
        .build();
    var card4 = BuildingCard.builder()
        .cardType(CardType.BUILDING)
        .typeId(101)
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
        .cardType(CardType.BASIC_RESOURCE)
        .typeId(1)
        .value(1)
        .build();
    var anotherCard1 = ResourceCard.builder()
        .cardType(CardType.BASIC_RESOURCE)
        .typeId(1)
        .value(1)
        .build();
    var card2 = ResourceCard.builder()
        .cardType(CardType.BASIC_RESOURCE)
        .typeId(2)
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
        .cardType(CardType.BUILDING)
        .typeId(1)
        .value(6)
        .build();
    var anotherCard1 = BuildingCard.builder()
        .cardType(CardType.BUILDING)
        .typeId(1)
        .value(6)
        .build();
    var card2 = BuildingCard.builder()
        .cardType(CardType.BUILDING)
        .typeId(2)
        .value(6)
        .build();

    assertFalse(user.ownCard(card1));
    user.takeBuildingCards(List.of(card1));
    assertTrue(user.ownCard(card1));
    assertTrue(user.ownCard(anotherCard1));
    assertFalse(user.ownCard(card2));
  }

}
