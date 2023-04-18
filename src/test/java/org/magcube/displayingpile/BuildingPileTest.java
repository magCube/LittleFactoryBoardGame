package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

public class BuildingPileTest {

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new BuildingPile(CardDeck.get(numOfPlayers).building));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorShouldThrowTest(NumOfPlayers numOfPlayers) {
    ArrayList<BuildingCard> mockDeck = new ArrayList<>();
    mockDeck.add(BuildingCard.builder()
        .cardIdentity(new CardIdentity(CardType.BUILDING, 0))
        .name("test" + 0)
        .build()
    );
    for (int i = 0; i < 8; i++) {
      var card = BuildingCard.builder()
          .cardIdentity(new CardIdentity(CardType.BUILDING, i))
          .name("test" + (i + 1))
          .build();
      mockDeck.add(card);
    }
    assertThrows(DisplayPileException.class, () -> new BuildingPile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(IDisplayingPile<Card> pile) {
    assertEquals(CardType.BUILDING, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(IDisplayingPile<BuildingCard> pile) {
    List<List<BuildingCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().allMatch(x -> x.size() == 1 && x.get(0) != null));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getMaxDisplayingSizeTest(IDisplayingPile<BuildingCard> pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var pile = new BuildingPile(CardDeck.get(numOfPlayers).building);
    assertEquals(pile.getDeck().size(), CardDeck.get(numOfPlayers).building.size() - pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardSizeTest(IDisplayingPile<BuildingCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
    assertEquals(0, pile.discardPileSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(IDisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = displaying.get(i).get(0);
      var cards = pile.takeCards(List.of(card.getCardIdentity()));
      sizes.set(i, sizes.get(i) - 1);
      displaying = pile.getDisplaying();
      assertEquals(sizes, displaying.stream().map(List::size).toList());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void takeCardsSingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new BuildingPile(CardDeck.get(NumOfPlayers.FOUR).building);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardEmptyCaseTest(IDisplayingPile<BuildingCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();

    assertDoesNotThrow(() -> pile.takeCards(List.of(cardIdentity1)));

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsTest(IDisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(1, initNumOfCardWithCardIdentity1);
    assertEquals(1, initNumOfCardWithCardIdentity2);

    var list = List.of(cardIdentity1, cardIdentity2);
    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(2, cards.size());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsShouldThrowTest(IDisplayingPile<BuildingCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity3 = new CardIdentity(CardType.LEVEL_1_RESOURCE, 99999);
    var cardIdentity4 = new CardIdentity(CardType.LEVEL_2_RESOURCE, 99999);

    var initNumOfCards = pile.getDisplaying().stream().flatMap(List::stream).toList().size();

    assertEquals(pile.getMaxDisplayingSize(), initNumOfCards);

    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1)));
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1, cardIdentity2)));
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity3)));
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity2, cardIdentity3)));
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity4)));
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity2, cardIdentity4)));

    var curNumOfCards = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    assertEquals(initNumOfCards, curNumOfCards);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsEmptyCaseTest(IDisplayingPile<BuildingCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var initNumOfCardWithCardIdentity1 = pile.getDisplaying().get(0).size();

    assertEquals(1, initNumOfCardWithCardIdentity1);

    List<CardIdentity> list = List.of(cardIdentity1, cardIdentity1);

    assertThrows(DisplayPileException.class, () -> pile.takeCards(list));

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(1, curNumOfCardWithCardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      assertDoesNotThrow(() -> pile.takeCards(List.of(cardIdentity1)));
    }
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1, cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(IDisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);

    assertNotNull(card1);
    assertNotNull(card2);

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of()));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2, card2)));
  }

  @Test
  void refillCardStopWhenDeckUsesUp() throws DisplayPileException {
    var mockDeck = new ArrayList<BuildingCard>();
    for (var i = 0; i < 3; i++) {
      mockDeck.add(BuildingCard.builder()
          .cardIdentity(new CardIdentity(CardType.BUILDING, i))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }

    var pile = new BuildingPile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    pile.refillCards();

    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void refillCardsTest(IDisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    assertEquals(0, pile.getDisplaying().get(0).size());
    assertEquals(0, pile.getDisplaying().get(1).size());
    assertEquals(2, pile.getDisplaying().stream().filter(x -> x.size() == 0).count());

    pile.refillCards();
    assertTrue(pile.getDisplaying().get(0).size() > 0);
    assertTrue(pile.getDisplaying().get(1).size() > 0);
    assertEquals(0, pile.getDisplaying().stream().filter(x -> x.size() == 0).count());
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_1_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_2_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_1_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_2_RESOURCE, 99999),
        new CardIdentity(CardType.BUILDING, 99999)
    );
  }

  private static Stream<BuildingPile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new BuildingPile(CardDeck.get(NumOfPlayers.TWO).building),
        new BuildingPile(CardDeck.get(NumOfPlayers.THREE).building),
        new BuildingPile(CardDeck.get(NumOfPlayers.FOUR).building)
    );
  }

  private <T extends Card> long numOfCardsWithCardIdentityInDisplaying(List<List<T>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
  }
}
