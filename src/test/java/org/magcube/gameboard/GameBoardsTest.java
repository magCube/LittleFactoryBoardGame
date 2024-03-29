package org.magcube.gameboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.ResourceCard;
import org.magcube.enums.CardType;
import org.magcube.enums.NumOfPlayers;

class GameBoardsTest {

  @Test
  void categorizeCardIdentitiesTest1() {
    var cardIdentities = Arrays.asList(
        new CardIdentity(CardType.BASIC_RESOURCE, 1234),
        new CardIdentity(CardType.BASIC_RESOURCE, 1234),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
        new CardIdentity(CardType.BASIC_RESOURCE, 9012),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3456),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4682),
        new CardIdentity(CardType.BUILDING, 7890)
    );

    var expectedData = new HashMap<CardType, List<CardIdentity>>() {{
      put(CardType.BASIC_RESOURCE, Arrays.asList(
          new CardIdentity(CardType.BASIC_RESOURCE, 1234),
          new CardIdentity(CardType.BASIC_RESOURCE, 1234),
          new CardIdentity(CardType.BASIC_RESOURCE, 9012)));
      put(CardType.LEVEL_ONE_RESOURCE, List.of(
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4682)));
      put(CardType.LEVEL_TWO_RESOURCE, List.of(
          new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3456)));
      put(CardType.BUILDING, List.of(
          new CardIdentity(CardType.BUILDING, 7890)));
    }};

    HashMap<CardType, List<CardIdentity>> actualData = GameBoards.categorizeCardIdentities(cardIdentities);

    assertEquals(expectedData.size(), actualData.size());
    for (CardType cardType : expectedData.keySet()) {
      assertNotNull(actualData.get(cardType));
      assertIterableEquals(expectedData.get(cardType), actualData.get(cardType));
    }
  }

  @Test
  void categorizeCardIdentitiesTest2() {
    var cardIdentities = Arrays.asList(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
        new CardIdentity(CardType.BUILDING, 7890)
    );

    var expectedData = new HashMap<CardType, List<CardIdentity>>() {{
      put(CardType.LEVEL_ONE_RESOURCE, List.of(
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678),
          new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)));
      put(CardType.BUILDING, List.of(
          new CardIdentity(CardType.BUILDING, 7890)));
    }};

    HashMap<CardType, List<CardIdentity>> actualData = GameBoards.categorizeCardIdentities(cardIdentities);

    assertFalse(actualData.containsKey(CardType.BASIC_RESOURCE));
    assertFalse(actualData.containsKey(CardType.LEVEL_TWO_RESOURCE));
    assertEquals(expectedData.size(), actualData.size());
    for (CardType cardType : expectedData.keySet()) {
      assertNotNull(actualData.get(cardType));
      assertIterableEquals(expectedData.get(cardType), actualData.get(cardType));
    }
  }

  @Test
  void categorizeCardsTest1() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1234)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1234)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 9012)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3456)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4682)).build(),
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 7890)).build()
    );

    var expectedData = new HashMap<CardType, List<? extends Card>>() {{
      put(CardType.BASIC_RESOURCE, List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1234)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1234)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 9012)).build()));
      put(CardType.LEVEL_ONE_RESOURCE, List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4682)).build()));
      put(CardType.LEVEL_TWO_RESOURCE, List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3456)).build()));
      put(CardType.BUILDING, List.of(
          BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 7890)).build()));
    }};

    HashMap<CardType, List<? extends Card>> actualData = GameBoards.categorizeCards(cards);

    assertEquals(expectedData.size(), actualData.size());
    for (CardType cardType : expectedData.keySet()) {
      assertNotNull(actualData.get(cardType));
      assertIterableEquals(expectedData.get(cardType), actualData.get(cardType));
      actualData.get(cardType).forEach(card -> assertSame(cardType == CardType.BUILDING ? BuildingCard.class : ResourceCard.class, card.getClass()));
    }
  }

  @Test
  void categorizeCardsTest2() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 7890)).build()
    );

    var expectedData = new HashMap<CardType, List<? extends Card>>() {{
      put(CardType.LEVEL_ONE_RESOURCE, List.of(
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build(),
          ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5678)).build()));
      put(CardType.BUILDING, List.of(
          BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 7890)).build()));
    }};

    HashMap<CardType, List<? extends Card>> actualData = GameBoards.categorizeCards(cards);

    assertFalse(actualData.containsKey(CardType.BASIC_RESOURCE));
    assertFalse(actualData.containsKey(CardType.LEVEL_TWO_RESOURCE));
    assertEquals(expectedData.size(), actualData.size());
    for (CardType cardType : expectedData.keySet()) {
      assertNotNull(actualData.get(cardType));
      assertIterableEquals(expectedData.get(cardType), actualData.get(cardType));
      actualData.get(cardType).forEach(card -> assertSame(cardType == CardType.BUILDING ? BuildingCard.class : ResourceCard.class, card.getClass()));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "BASIC_RESOURCE, 1, true",
      "LEVEL_ONE_RESOURCE, 1, true",
      "LEVEL_TWO_RESOURCE, 1, true",
      "BUILDING, 1, true",
      "BASIC_RESOURCE, 99999, false",
      "LEVEL_ONE_RESOURCE, 99999, false",
      "LEVEL_TWO_RESOURCE, 99999, false",
      "BUILDING, 99999, false",
      "BASIC_RESOURCE, 0, false",
      "LEVEL_ONE_RESOURCE, 0, false",
      "LEVEL_TWO_RESOURCE, 0, false",
      "BUILDING, 0, false",
      ", 1, false",
  })
  void isCardIdentitiesValidTest1(CardType cardType, int typeId, boolean expected) {
    assertEquals(expected, GameBoards.isCardIdentitiesValid(List.of(new CardIdentity(cardType, typeId))));
  }

  @Test
  void isCardIdentitiesValidTest2() {
    var valid1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var valid2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);
    var valid3 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1);
    var valid4 = new CardIdentity(CardType.BUILDING, 1);

    var invalid1 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);
    var invalid2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);

    assertTrue(GameBoards.isCardIdentitiesValid(List.of(valid1, valid2)));
    assertTrue(GameBoards.isCardIdentitiesValid(List.of(valid3, valid4)));
    assertTrue(GameBoards.isCardIdentitiesValid(List.of(valid2, valid3, valid4)));
    assertTrue(GameBoards.isCardIdentitiesValid(List.of(valid1, valid2, valid3, valid4)));

    assertFalse(GameBoards.isCardIdentitiesValid(List.of(invalid1, valid2)));
    assertFalse(GameBoards.isCardIdentitiesValid(List.of(valid1, invalid2)));
    assertFalse(GameBoards.isCardIdentitiesValid(List.of(invalid1, invalid2)));
    assertFalse(GameBoards.isCardIdentitiesValid(List.of(valid1, valid2, invalid1)));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void isClassValidTest1(NumOfPlayers numOfPlayers) {
    var deck = CardDeck.get(numOfPlayers);
    assertTrue(GameBoards.isClassValid(deck.basicResource));
    assertTrue(GameBoards.isClassValid(deck.levelOneResource));
    assertTrue(GameBoards.isClassValid(deck.levelTwoResource));
    assertTrue(GameBoards.isClassValid(deck.building));
  }

  @Test
  void isClassValidTest2() {
    var valid1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var valid2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build();
    var valid3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).build();
    var valid4 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).build();

    var invalid1 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var invalid2 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build();
    var invalid3 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).build();
    var invalid4 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).build();

    assertTrue(GameBoards.isClassValid(List.of(valid1)));
    assertTrue(GameBoards.isClassValid(List.of(valid2)));
    assertTrue(GameBoards.isClassValid(List.of(valid3)));
    assertTrue(GameBoards.isClassValid(List.of(valid4)));
    assertTrue(GameBoards.isClassValid(List.of(valid1, valid2)));
    assertTrue(GameBoards.isClassValid(List.of(valid3, valid4)));
    assertTrue(GameBoards.isClassValid(List.of(valid1, valid2, valid3, valid4)));

    assertFalse(GameBoards.isClassValid(List.of(invalid1)));
    assertFalse(GameBoards.isClassValid(List.of(invalid2)));
    assertFalse(GameBoards.isClassValid(List.of(invalid3)));
    assertFalse(GameBoards.isClassValid(List.of(invalid4)));
    assertFalse(GameBoards.isClassValid(List.of(invalid1, invalid2)));
    assertFalse(GameBoards.isClassValid(List.of(invalid3, invalid4)));
    assertFalse(GameBoards.isClassValid(List.of(invalid1, invalid2, invalid3, invalid4)));

    assertFalse(GameBoards.isClassValid(List.of(valid1, invalid1)));
    assertFalse(GameBoards.isClassValid(List.of(valid2, invalid2)));
    assertFalse(GameBoards.isClassValid(List.of(valid3, invalid3)));
    assertFalse(GameBoards.isClassValid(List.of(valid4, invalid4)));
    assertFalse(GameBoards.isClassValid(List.of(valid1, valid2, invalid1)));
    assertFalse(GameBoards.isClassValid(List.of(valid3, valid4, invalid3)));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void isNoBuildingCardsTest1(NumOfPlayers numOfPlayers) {
    var deck = CardDeck.get(numOfPlayers);
    assertTrue(GameBoards.isNoBuildingCards(deck.basicResource));
    assertTrue(GameBoards.isNoBuildingCards(deck.levelOneResource));
    assertTrue(GameBoards.isNoBuildingCards(deck.levelTwoResource));
    assertFalse(GameBoards.isNoBuildingCards(deck.building));
  }

  @Test
  void isNoBuildingCardsTest2() {
    var resource1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build();
    var resource2 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build();
    var resource3 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).build();

    var building1 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).build();
    var building2 = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 2)).build();

    assertTrue(GameBoards.isNoBuildingCards(List.of(resource1)));
    assertTrue(GameBoards.isNoBuildingCards(List.of(resource2)));
    assertTrue(GameBoards.isNoBuildingCards(List.of(resource3)));
    assertTrue(GameBoards.isNoBuildingCards(List.of(resource1, resource2, resource3)));

    assertFalse(GameBoards.isNoBuildingCards(List.of(building1)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(building2)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(building1, building2)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(resource1, building1)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(resource2, building2)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(building1, resource3)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(resource1, resource2, resource3, building1)));
    assertFalse(GameBoards.isNoBuildingCards(List.of(building1, resource1, resource2, resource3)));
  }

  @Test
  void sumOfCardsValueTest1() {
    var cards = List.of(
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(6).build()
    );
    assertEquals(6, GameBoards.sumOfCardsValue(cards));

    HashMap<CardType, List<? extends Card>> categorizedCards = new HashMap<>();
    categorizedCards.put(CardType.BUILDING, cards);
    assertEquals(6, GameBoards.sumOfCardsValue(categorizedCards));
  }

  @Test
  void sumOfCardsValueTest2() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(6).build()
    );
    assertEquals(7, GameBoards.sumOfCardsValue(cards));

    HashMap<CardType, List<? extends Card>> categorizedCards = new HashMap<>();
    categorizedCards.put(CardType.BASIC_RESOURCE, List.of(cards.get(0)));
    categorizedCards.put(CardType.BUILDING, List.of(cards.get(1)));
    assertEquals(7, GameBoards.sumOfCardsValue(categorizedCards));
  }

  @Test
  void sumOfCardsValueTest3() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).value(3).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).value(3).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).value(5).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(8).build(),
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(6).build()
    );
    assertEquals(28, GameBoards.sumOfCardsValue(cards));

    HashMap<CardType, List<? extends Card>> categorizedCards = new HashMap<>();
    categorizedCards.put(CardType.BASIC_RESOURCE, List.of(cards.get(0), cards.get(1), cards.get(2)));
    categorizedCards.put(CardType.LEVEL_ONE_RESOURCE, List.of(cards.get(3), cards.get(4), cards.get(5)));
    categorizedCards.put(CardType.LEVEL_TWO_RESOURCE, List.of(cards.get(6)));
    categorizedCards.put(CardType.BUILDING, List.of(cards.get(7)));
    assertEquals(28, GameBoards.sumOfCardsValue(categorizedCards));
  }

  @Test
  void flattenResourceCardsFromCategorizedCardsTest1() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(8).build()
    );

    var categorizedCards = new HashMap<CardType, List<? extends Card>>();
    categorizedCards.put(CardType.BASIC_RESOURCE, List.of(cards.get(0), cards.get(1), cards.get(2)));
    categorizedCards.put(CardType.LEVEL_TWO_RESOURCE, List.of(cards.get(3)));

    var flattenedCards = GameBoards.flattenResourceCardsFromCategorizedCards(categorizedCards);
    assertEquals(4, flattenedCards.size());
    assertTrue(flattenedCards.containsAll(cards));
    assertTrue(GameBoards.isNoBuildingCards(flattenedCards));
  }

  @Test
  void flattenResourceCardsFromCategorizedCardsTest2() {
    var cards = List.of(
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).value(1).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).value(3).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).value(3).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).value(5).build(),
        ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).value(8).build(),
        BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 1)).value(6).build()
    );

    HashMap<CardType, List<? extends Card>> categorizedCards = new HashMap<>();
    categorizedCards.put(CardType.BASIC_RESOURCE, List.of(cards.get(0), cards.get(1), cards.get(2)));
    categorizedCards.put(CardType.LEVEL_ONE_RESOURCE, List.of(cards.get(3), cards.get(4), cards.get(5)));
    categorizedCards.put(CardType.LEVEL_TWO_RESOURCE, List.of(cards.get(6)));
    categorizedCards.put(CardType.BUILDING, List.of(cards.get(7)));

    var expected = cards.subList(0, 7);
    var resourceCards = GameBoards.flattenResourceCardsFromCategorizedCards(categorizedCards);
    resourceCards.sort(Comparator.comparing(Card::getCardIdentity));
    assertEquals(expected, resourceCards);
    assertTrue(GameBoards.isNoBuildingCards(resourceCards));
  }
}