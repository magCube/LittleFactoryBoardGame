package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.magcube.displayingpile.DisplayingPileTestUtil.numOfCardsInDisplaying;
import static org.magcube.displayingpile.DisplayingPileTestUtil.takeCardHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.Card;
import org.magcube.card.CardData;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.player.NumOfPlayers;

class BasicResourceDisplayingPileTest {

  private static Stream<BasicResourceDisplayingPile> pileProvider() {
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
        mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, i)).build());
      }
    }
    assertDoesNotThrow(() -> new BasicResourceDisplayingPile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.BASIC_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest1(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(CardData.basicResource.size(), displaying.size());
    assertEquals(CardData.basicResource.size(), pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().noneMatch(List::isEmpty));
    displaying.forEach(innerList -> {
      var first = innerList.get(0).getCardIdentity();
      assertTrue(innerList.stream().allMatch(c -> c.getCardIdentity().equals(first)));
    });
  }

  @Test
  void getDisplayingTest2() {
    List<ResourceCard> mockedDeck = new ArrayList<>();
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build());
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build());
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 1)).build());
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build());
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 2)).build());
    mockedDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 3)).build());
    var pile = new BasicResourceDisplayingPile(mockedDeck);
    List<List<ResourceCard>> displaying = pile.getDisplaying();

    List<List<ResourceCard>> expected = new ArrayList<>();
    expected.add(List.of(mockedDeck.get(0), mockedDeck.get(1), mockedDeck.get(2)));
    expected.add(List.of(mockedDeck.get(3), mockedDeck.get(4)));
    expected.add(List.of(mockedDeck.get(5)));

    assertEquals(expected, displaying);
    assertEquals(3, displaying.size());
    assertEquals(3, pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDeckTest(DisplayingPile<ResourceCard> pile) {
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
  void getMaxDisplayingSizeTest2(int size) {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, i)).build());
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
  void cardsInDisplaySingleCardTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertTrue(displaying.size() > 0);
    for (List<ResourceCard> resourceCards : displaying) {
      var card = resourceCards.get(0);
      var optCards = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      assertTrue(optCards.isPresent());
      var cards = optCards.get();
      assertEquals(1, cards.size());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardShouldOptionalEmptyTest(DisplayingPile<ResourceCard> pile) {
    var card = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 99999)).build();
    assertTrue(pile.cardsInDisplay(List.of(card.getCardIdentity())).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      var cards = pile.cardsInDisplay(List.of(cardIdentity1));
      assertTrue(cards.isPresent());
      pile.takeCards(cards.get());
    }

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
    assertEquals(pile.getMaxDisplayingSize(), pile.getDisplaying().size());
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void cardsInDisplayMultipleCardsTest(int quantity1, int quantity2) {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));
    var OptCards = pile.cardsInDisplay(list);
    assertTrue(OptCards.isPresent());
    var cards = OptCards.get();
    assertEquals(quantity1 + quantity2, cards.size());
    assertEquals(quantity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(quantity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsShouldReturnOptionalEmtpyTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);
    var cardIdentity3 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);

    assertFalse(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity3)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity3, cardIdentity2)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2, cardIdentity3)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsEmptyCaseTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertTrue(pile.cardsInDisplay(Collections.nCopies(initNumOfCardWithCardIdentity1 + 1, cardIdentity1)).isEmpty());

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      var cards = pile.cardsInDisplay(List.of(cardIdentity1));
      assertTrue(cards.isPresent());
      pile.takeCards(cards.get());
    }
    curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity1)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertTrue(displaying.size() > 0);
    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = pile.getDisplaying().get(i).get(0);
      var optCardsInDisplay = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      assertTrue(optCardsInDisplay.isPresent());
      pile.takeCards(optCardsInDisplay.get());
      sizes.set(i, sizes.get(i) - 1);
      assertEquals(sizes, pile.getDisplaying().stream().map(List::size).toList());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void takeCardsMultipleCardsTest(int quantity1, int quantity2) {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));

    var optCardsInDisplay = pile.cardsInDisplay(list);
    assertTrue(optCardsInDisplay.isPresent());
    pile.takeCards(optCardsInDisplay.get());
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsSingleCardTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var cards = takeCardHelper(pile, List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
  }

  @ParameterizedTest
  @CsvSource({
      "1, 1",
      "1, 2",
      "2, 1",
      "2, 2"
  })
  void discardCardsMultipleCardsTest(int quantity1, int quantity2) {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(quantity1, cardIdentity1));
    list.addAll(Collections.nCopies(quantity2, cardIdentity2));

    var cards = takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) {
    var card1 = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, 99999)).build();
    assertThrows(NullPointerException.class, () -> pile.discardCards(List.of(card1)));
  }
}
