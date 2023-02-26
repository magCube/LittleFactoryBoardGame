package org.magcube.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.magcube.card.Card;
import org.magcube.card.Building;
import org.magcube.card.BasicResource;

public class PlayerTest {

  @Test
  void userNameOperationsTest() {
    var user = new Player();
    user.setName("test12312412312");
    assertEquals("test12312412312", user.getName());
  }

  @Test
  void userGiveAndTakeCardAndPointTest() {
    var user = new Player();
    assertEquals(0, user.getPoints());
    var card1 = BasicResource.BasicResourceBuilder()
        .value(1)
        .cost(new Card[1])
        .typeId(1)
        .build();
    var card3 = BasicResource.BasicResourceBuilder()
        .value(2)
        .cost(new Card[1])
        .typeId(2)
        .build();
    var card2 = Building.factoryBuilder()
        .typeId(100)
        .build();
    var card4 = Building.factoryBuilder()
        .typeId(101)
        .build();
    user.takeCards(List.of(card1, card2, card3, card4));
    assertEquals(2, user.getCards().size());
    assertEquals(2, user.getBuildings().size());
    assertEquals(2, user.getPoints());
    user.addPoints(1);
    assertEquals(3, user.getPoints());
    user.addPoints(10);
    assertEquals(13, user.getPoints());
    user.giveCards(List.of(card1));
    assertEquals(1, user.getCards().size());
    assertEquals(card3, user.getCards().get(0));
    user.giveCards(List.of(card2));
    assertEquals(2, user.getBuildings().size());
  }

  @Test
  void userOwnCardTest() {
    var user = new Player();
    assertEquals(0, user.getPoints());
    var card1 = BasicResource.BasicResourceBuilder()
        .value(1)
        .cost(new Card[1])
        .typeId(1)
        .build();
    assertFalse(user.ownCard(card1));
    user.takeCards(List.of(card1));
    Assertions.assertTrue(user.ownCard(card1));
  }

  @Test
  void userOwnFactoryCardTest() {
    var user = new Player();
    assertEquals(0, user.getPoints());
    var card1 = Building.factoryBuilder().build();
    assertFalse(user.ownCard(card1));
    user.takeCards(List.of(card1));
    Assertions.assertTrue(user.ownCard(card1));
  }

}
