package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.Card;
import org.magcube.card.CardData;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

class BasicResourceDisplayingPileTest {

  private static Stream<BasicResourceDisplayingPile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.TWO).basicResource),
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.THREE).basicResource),
        new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource)
    );
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.LEVEL_1_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_2_RESOURCE, 1),
        new CardIdentity(CardType.BUILDING, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_1_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_2_RESOURCE, 99999),
        new CardIdentity(CardType.BUILDING, 99999)
    );
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).basicResource));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorShouldThrowTest(NumOfPlayers numOfPlayers) {
    assertThrows(DisplayPileException.class, () -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).level1Resource));
    assertThrows(DisplayPileException.class, () -> new BasicResourceDisplayingPile(CardDeck.get(numOfPlayers).level2Resource));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(IDisplayingPile<Card> pile) {
    assertEquals(CardType.BASIC_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(IDisplayingPile<ResourceCard> pile) {
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
  void getMaxDisplayingSizeTest(BasicResourceDisplayingPile pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
    assertEquals(pile.getMaxDisplayingSize(), CardData.basicResource.size());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = displaying.get(i).get(0);
      var cards = pile.takeCards(List.of(card.getCardIdentity()));
      sizes.set(i, sizes.get(i) - 1);
      assertEquals(sizes, displaying.stream().map(List::size).toList());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void takeCardsSingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new BasicResourceDisplayingPile(CardDeck.get(NumOfPlayers.FOUR).basicResource);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardEmptyCaseTest(IDisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      assertDoesNotThrow(() -> pile.takeCards(List.of(cardIdentity1)));
    }

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1)));
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

    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(quantity1 + quantity2, cards.size());
    assertEquals(quantity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(quantity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsShouldThrowTest(IDisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.LEVEL_1_RESOURCE, 1);
    var cardIdentity2 = new CardIdentity(CardType.BASIC_RESOURCE, 2);
    var cardIdentity3 = new CardIdentity(CardType.BASIC_RESOURCE, 99999);
    var cardIdentity4 = new CardIdentity(CardType.LEVEL_2_RESOURCE, 99999);

    var initNumOfCards = pile.getDisplaying().stream().flatMap(List::stream).toList().size();

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
  void takeCardsMultipleCardsEmptyCaseTest(IDisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    List<CardIdentity> list = Collections.nCopies((int) (initNumOfCardWithCardIdentity1 + 1), cardIdentity1);

    assertThrows(DisplayPileException.class, () -> pile.takeCards(list));

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      assertDoesNotThrow(() -> pile.takeCards(List.of(cardIdentity1)));
    }
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1, cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsSingleCardTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var cards = pile.takeCards(List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
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

    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1 - quantity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2 - quantity2, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(initNumOfCardWithCardIdentity1, curNumOfCardWithCardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity2, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(IDisplayingPile<Card> pile) throws DisplayPileException {
    var card1 = new ResourceCard(new CardIdentity(CardType.LEVEL_1_RESOURCE, 1), "test", 1, null, null);
    var card2 = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 99999), "test", 1, null, null);
    var card3 = pile.takeCards(List.of(new CardIdentity(CardType.BASIC_RESOURCE, 1))).get(0);

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2)));

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2, card3)));

    assertDoesNotThrow(() -> pile.discardCards(List.of(card3)));
  }

  private long numOfCardsWithCardIdentityInDisplaying(List<List<ResourceCard>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
  }
}
