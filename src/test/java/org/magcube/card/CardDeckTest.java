package org.magcube.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.magcube.enums.CardType;
import org.magcube.enums.NumOfPlayers;

class CardDeckTest {

  @ParameterizedTest
  @EnumSource
  void deckSizeShouldMatchTest(NumOfPlayers numOfPlayers) {
    BiFunction<List<CardQuantity>, NumOfPlayers, Integer> sumFn = (cardQuantities, n) -> cardQuantities.stream()
        .mapToInt(x -> x.getQuantityForNumOfPlayers(n))
        .sum();

    assertEquals(sumFn.apply(CardQuantity.basicResource, numOfPlayers), CardDeck.get(numOfPlayers).basicResource.size());
    assertEquals(sumFn.apply(CardQuantity.levelOneResource, numOfPlayers), CardDeck.get(numOfPlayers).levelOneResource.size());
    assertEquals(sumFn.apply(CardQuantity.levelTwoResource, numOfPlayers), CardDeck.get(numOfPlayers).levelTwoResource.size());
    assertEquals(sumFn.apply(CardQuantity.building, numOfPlayers), CardDeck.get(numOfPlayers).building.size());
  }

  @ParameterizedTest
  @EnumSource
  void shouldHaveDataTest(NumOfPlayers numOfPlayers) {
    assertNotNull(CardDeck.get(numOfPlayers));
  }

  @ParameterizedTest
  @EnumSource
  void cardTypeShouldConsistentTest(NumOfPlayers numOfPlayers) {
    var deck = CardDeck.get(numOfPlayers);
    assertTrue(deck.basicResource.stream().allMatch(x -> x.cardType() == CardType.BASIC_RESOURCE));
    assertTrue(deck.levelOneResource.stream().allMatch(x -> x.cardType() == CardType.LEVEL_ONE_RESOURCE));
    assertTrue(deck.levelTwoResource.stream().allMatch(x -> x.cardType() == CardType.LEVEL_TWO_RESOURCE));
    assertTrue(deck.building.stream().allMatch(x -> x.cardType() == CardType.BUILDING));
  }

  @ParameterizedTest
  @EnumSource
  void levelTwoResourceAndBuildingShouldNotHaveDuplicateTest(NumOfPlayers numOfPlayers) {
    var deck = CardDeck.get(numOfPlayers);
    assertEquals(deck.levelTwoResource.size(), (new HashSet<>(deck.levelTwoResource.stream().mapToInt(Card::typeId).boxed().toList())).size());
    assertEquals(deck.building.size(), (new HashSet<>(deck.building.stream().mapToInt(Card::typeId).boxed().toList())).size());
  }

  // note: this test only check if the card is shallow clone
  @Test
  void isCloneTest() {
    Function<List<? extends Card>, Boolean> allRefNoSameFn = (list) -> {
      for (int i = 0; i < list.size(); i++) {
        for (int j = i + 1; j < list.size(); j++) {
          if (list.get(i) == list.get(j)) {
            return false;
          }
        }
      }
      return true;
    };

    List<ResourceCard> resourceCards = new ArrayList<>();
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.TWO).levelTwoResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.THREE).levelTwoResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).levelOneResource);
    resourceCards.addAll(CardDeck.get(NumOfPlayers.FOUR).levelTwoResource);

    assertTrue(resourceCards.size() > 0);
    assertTrue(allRefNoSameFn.apply(resourceCards));

    List<BuildingCard> buildingCards = new ArrayList<>();
    buildingCards.addAll(CardDeck.get(NumOfPlayers.TWO).building);
    buildingCards.addAll(CardDeck.get(NumOfPlayers.THREE).building);
    buildingCards.addAll(CardDeck.get(NumOfPlayers.FOUR).building);

    assertTrue(buildingCards.size() > 0);
    assertTrue(allRefNoSameFn.apply(buildingCards));
  }
}