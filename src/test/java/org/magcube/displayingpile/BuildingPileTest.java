package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

public class BuildingPileTest {

  private static List<BuildingCard> takeCardHelper(DisplayingPile<BuildingCard> pile, List<CardIdentity> cardIdentities) throws DisplayPileException {
    var cardsInDisplay = pile.cardsInDisplay(cardIdentities);
    if (cardsInDisplay == null) {
      throw new DisplayPileException("Not all cards are in display");
    }
    return pile.takeCards(cardsInDisplay);
  }

  private static <T extends Card> long numOfCardsWithCardIdentityInDisplaying(List<List<T>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
  }

  private static Stream<BuildingPile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new BuildingPile(CardDeck.get(NumOfPlayers.TWO).building),
        new BuildingPile(CardDeck.get(NumOfPlayers.THREE).building),
        new BuildingPile(CardDeck.get(NumOfPlayers.FOUR).building)
    );
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest1(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new BuildingPile(CardDeck.get(numOfPlayers).building));
  }

  @Test
  void constructorTest2() {
    ArrayList<BuildingCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      mockDeck.add(BuildingCard.builder()
          .cardIdentity(new CardIdentity(CardType.BUILDING, i))
          .name("test" + i)
          .build());
    }
    assertDoesNotThrow(() -> new BuildingPile(mockDeck));
  }

  @Test
  void constructorShouldThrowTest() {
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
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.BUILDING, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(DisplayingPile<BuildingCard> pile) {
    List<List<BuildingCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().allMatch(x -> x.size() == 1 && x.get(0) != null));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var pile = new BuildingPile(CardDeck.get(numOfPlayers).building);
    assertEquals(pile.getDeck().size(), CardDeck.get(numOfPlayers).building.size() - pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardPileTest(DisplayingPile<ResourceCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getMaxDisplayingSizeTest1(DisplayingPile<BuildingCard> pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
  void getMaxDisplayingSizeTest2(int size) throws DisplayPileException {
    ArrayList<BuildingCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      mockDeck.add(BuildingCard.builder()
          .cardIdentity(new CardIdentity(CardType.BUILDING, i))
          .name("test" + i)
          .build());
    }
    var pile = new BuildingPile(mockDeck);
    assertEquals(5, pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardSizeTest(DisplayingPile<BuildingCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
    assertEquals(0, pile.discardPileSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();
    assertTrue(displaying.size() > 0);

    for (List<BuildingCard> buildingCard : displaying) {
      var card = buildingCard.get(0);
      var cards = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      assertNotNull(cards);
      assertEquals(1, cards.size());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardShouldReturnNullTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var card = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 99999)).name("test").build();
    assertNull(pile.cardsInDisplay(List.of(card.getCardIdentity())));
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void cardsInDisplaySingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new BuildingPile(CardDeck.get(NumOfPlayers.FOUR).building);
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();

    var cardsInDisplay = pile.cardsInDisplay(List.of(cardIdentity1));
    assertNotNull(cardsInDisplay);
    var cardToken = pile.takeCards(cardsInDisplay);

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(cardsInDisplay, cardToken);
    assertNull(pile.cardsInDisplay(List.of(cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(1, initNumOfCardWithCardIdentity1);
    assertEquals(1, initNumOfCardWithCardIdentity2);

    var list = List.of(cardIdentity1, cardIdentity2);
    var cards = pile.cardsInDisplay(list);
    assertNotNull(cards);
    assertEquals(2, cards.size());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
  }


  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsShouldThrowTest(DisplayingPile<BuildingCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity3 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1);

    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity2)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity3)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity1)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity3)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity3, cardIdentity1)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity3)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsEmptyCaseTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity2 = pile.getDisplaying().get(1).get(0).getCardIdentity();

    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity1)));
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity2)));

    var cardsInDisplay = pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2));
    assertNotNull(cardsInDisplay);
    assertEquals(2, cardsInDisplay.size());
    assertEquals(1, cardsInDisplay.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cardsInDisplay.stream().filter(x -> x.isIdentical(cardIdentity2)).count());

    var cardToken = pile.takeCards(cardsInDisplay);
    assertEquals(cardsInDisplay, cardToken);

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    assertNull(pile.cardsInDisplay(List.of(cardIdentity1)));
    assertNull(pile.cardsInDisplay(List.of(cardIdentity2)));
    assertNull(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2)));
    assertNull(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity1)));
  }


  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertTrue(displaying.size() > 0);
    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = displaying.get(i).get(0);
      var cardsInDisplay = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      var cards = pile.takeCards(cardsInDisplay);
      sizes.set(i, sizes.get(i) - 1);
      assertEquals(sizes, pile.getDisplaying().stream().map(List::size).toList());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    List<List<BuildingCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(1, initNumOfCardWithCardIdentity1);
    assertEquals(1, initNumOfCardWithCardIdentity2);

    var list = List.of(cardIdentity1, cardIdentity2);
    var cardsInDisplay = pile.cardsInDisplay(list);
    assertNotNull(cardsInDisplay);
    var cardsToken = pile.takeCards(cardsInDisplay);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(2, cardsToken.size());
    assertEquals(1, cardsToken.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cardsToken.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsShouldThrowTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var validCard1 = pile.getDisplaying().get(0).get(0);
    var validCard2 = pile.getDisplaying().get(1).get(0);
    var invalidCard = BuildingCard.builder().cardIdentity(new CardIdentity(CardType.BUILDING, 0)).name("invalid").build();

    var initDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();

    var cards = List.of(validCard1, validCard2, invalidCard);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(cards));

    var displayFlatten = pile.getDisplaying().stream().flatMap(List::stream).toList();
    var curDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    // should not remove any cards if one is invalid
    assertTrue(displayFlatten.contains(validCard1));
    assertEquals(initDisplayingCard, curDisplayingCard);

    List<BuildingCard> validCards = List.of(validCard1, validCard2);
    pile.takeCards(validCards);
    displayFlatten = pile.getDisplaying().stream().flatMap(List::stream).toList();
    curDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    assertFalse(displayFlatten.contains(validCard1));
    assertFalse(displayFlatten.contains(validCard2));
    assertEquals(initDisplayingCard - 2, curDisplayingCard);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(DisplayingPile<BuildingCard> pile) {
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
  void refillCardsTest(DisplayingPile<BuildingCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    takeCardHelper(pile, list);
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
}
