package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.Card;
import org.magcube.card.CardData;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

class BasicResourceDisplayingPileTest {

  private static List<ResourceCard> takeCardHelper(DisplayingPile<ResourceCard> pile, List<CardIdentity> cardIdentities) throws DisplayPileException {
    var cardsInDisplay = pile.cardsInDisplay(cardIdentities);
    if (cardsInDisplay == null) {
      throw new DisplayPileException("Not all cards are in display");
    }
    return pile.takeCards(cardsInDisplay);
  }

  private static long numOfCardsWithCardIdentityInDisplaying(List<List<ResourceCard>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
  }

  private static Stream<BasicResourceDisplayingPile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.TWO).basicResource),
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.THREE).basicResource),
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource)
    );
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest1(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).basicResource));
  }

  @Test
  void constructorTest2() {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, i), "test" + i + j, 1, null, null));
      }
    }
    assertDoesNotThrow(() -> new BasicResourceDisplayingPile(mockDeck));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorShouldThrowTest1(NumOfPlayers numOfPlayers) {
    assertThrows(DisplayPileException.class, () -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).levelOneResource));
    assertThrows(DisplayPileException.class, () -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).levelTwoResource));
  }

  @ParameterizedTest
  @EnumSource(value = CardType.class, mode = Mode.INCLUDE, names = {"LEVEL_ONE_RESOURCE", "LEVEL_TWO_RESOURCE"})
  void constructorShouldThrowTest2(CardType inconsistantCardType) {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, i), "test" + i + j, 1, null, null));
      }
    }
    mockDeck.add(new ResourceCard(new CardIdentity(inconsistantCardType, 1), "test", 1, null, null));
    assertThrows(DisplayPileException.class, () -> new BasicResourceDisplayingPile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.BASIC_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(CardData.basicResource.size(), displaying.size());
    assertEquals(CardData.basicResource.size(), pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().noneMatch(List::isEmpty));
    displaying.forEach(innerList -> {
      var first = innerList.get(0).getCardIdentity();
      assertTrue(innerList.stream().allMatch(c -> c.getCardIdentity().equals(first)));
    });
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDeckTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    assertEquals(Collections.emptyList(), pile.getDeck());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getMaxDisplayingSizeTest1(BasicResourceDisplayingPile pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
    assertEquals(pile.getMaxDisplayingSize(), CardData.basicResource.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
  void getMaxDisplayingSizeTest2(int size) throws DisplayPileException {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, i), "test" + i + j, 1, null, null));
      }
    }
    var pile = new BasicResourceDisplayingPile(mockDeck);
    assertEquals(pile.getMaxDisplayingSize(), size);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void deckSizeTest(DisplayingPile<ResourceCard> pile) {
    assertEquals(0, pile.deckSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardPileSizeTest(DisplayingPile<ResourceCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
    assertEquals(0, pile.discardPileSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertTrue(displaying.size() > 0);
    for (List<ResourceCard> resourceCards : displaying) {
      var card = resourceCards.get(0);
      var cards = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      assertNotNull(cards);
      assertEquals(1, cards.size());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardShouldReturnNullTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 99999), "test", 1, null, null);
    assertNull(pile.cardsInDisplay(List.of(card.getCardIdentity())));
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1),
        new CardIdentity(CardType.BUILDING, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999),
        new CardIdentity(CardType.BUILDING, 99999)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void cardsInDisplaySingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      var cards = pile.cardsInDisplay(List.of(cardIdentity1));
      assertNotNull(cards);
      var cardToken = pile.takeCards(cards);
      assertEquals(1, cardToken.size());
      assertEquals(cards, cardToken);
    }

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertNull(pile.cardsInDisplay(List.of(cardIdentity1)));
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void cardsInDisplayMultipleCardsTest(int quantity1, int quantity2) throws DisplayPileException {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));
    var cards = pile.cardsInDisplay(list);
    assertNotNull(cards);
    assertEquals(quantity1 + quantity2, cards.size());
    assertEquals(quantity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(quantity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2);
    var cardIdentity3 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3);

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
  void cardsInDisplayMultipleCardsEmptyCaseTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertNull(pile.cardsInDisplay(Collections.nCopies((int) (initNumOfCardWithCardIdentity1 + 1), cardIdentity1)));

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      var cards = pile.cardsInDisplay(List.of(cardIdentity1));
      assertNotNull(cards);
      var cardsToken = pile.takeCards(cards);
      assertEquals(1, cardsToken.size());
      assertEquals(cards, cardsToken);
    }
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertNull(pile.cardsInDisplay(List.of(cardIdentity1)));
    assertNull(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertTrue(displaying.size() > 0);
    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = pile.getDisplaying().get(i).get(0);
      var cardsInDisplay = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      var cards = pile.takeCards(cardsInDisplay);
      sizes.set(i, sizes.get(i) - 1);
      assertEquals(sizes, pile.getDisplaying().stream().map(List::size).toList());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void takeCardsMultipleCardsTest(int quantity1, int quantity2) throws DisplayPileException {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));

    var cardsInDisplay = pile.cardsInDisplay(list);
    assertNotNull(cardsInDisplay);
    var cardsToken = pile.takeCards(cardsInDisplay);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(quantity1 + quantity2, cardsToken.size());
    assertEquals(quantity1, cardsToken.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(quantity2, cardsToken.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);
    assertEquals(cardsInDisplay, cardsToken);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    ResourceCard validCard1 = pile.getDisplaying().get(0).get(0);
    ResourceCard validCard2 = pile.getDisplaying().get(1).get(0);
    ResourceCard invalidCard = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 1), "invalid", 0, null, null);

    var initDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();

    var cards = List.of(validCard1, validCard2, invalidCard);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(cards));

    var displayFlatten = pile.getDisplaying().stream().flatMap(List::stream).toList();
    var curDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    // should not remove any cards if one is invalid
    assertTrue(displayFlatten.containsAll(List.of(validCard1, validCard2)));
    assertEquals(initDisplayingCard, curDisplayingCard);

    List<ResourceCard> validCards = List.of(validCard1, validCard2);
    pile.takeCards(validCards);
    displayFlatten = pile.getDisplaying().stream().flatMap(List::stream).toList();
    curDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    assertFalse(displayFlatten.contains(validCard1));
    assertFalse(displayFlatten.contains(validCard2));
    assertEquals(initDisplayingCard - 2, curDisplayingCard);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsSingleCardTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var cards = takeCardHelper(pile, List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);
    assertFalse(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
    assertTrue(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void discardCardsMultipleCardsTest(int quantity1, int quantity2) throws DisplayPileException {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));

    var cards = takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);
    assertFalse(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2, curNumOfCardWithCardIdentity2);
    assertTrue(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = new ResourceCard(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1), "test", 1, null, null);
    var card2 = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 99999), "test", 1, null, null);
    var card3 = takeCardHelper(pile, List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1))).get(0);

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2)));

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2, card3)));

    assertDoesNotThrow(() -> pile.discardCards(List.of(card3)));
  }
}
