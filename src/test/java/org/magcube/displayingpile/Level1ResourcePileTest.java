package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

class Level1ResourcePileTest {

  private static Stream<Level1ResourcePile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new Level1ResourcePile(CardDeck.get(NumOfPlayers.TWO).level1Resource),
        new Level1ResourcePile(CardDeck.get(NumOfPlayers.THREE).level1Resource),
        new Level1ResourcePile(CardDeck.get(NumOfPlayers.FOUR).level1Resource)
    );
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
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
    assertDoesNotThrow(() -> new Level1ResourcePile(CardDeck.get(numOfPlayers).level1Resource));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorShouldThrowTest(NumOfPlayers numOfPlayers) {
    assertThrows(DisplayPileException.class, () -> new Level1ResourcePile(CardDeck.get(numOfPlayers).basicResource));
    assertThrows(DisplayPileException.class, () -> new Level1ResourcePile(CardDeck.get(numOfPlayers).level2Resource));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(IDisplayingPile<Card> pile) {
    assertEquals(CardType.LEVEL_1_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(IDisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().noneMatch(List::isEmpty));
    displaying.forEach(innerList -> {
      var first = innerList.get(0).getCardIdentity();
      assertTrue(innerList.stream().allMatch(c -> c.getCardIdentity().equals(first)));
    });
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getMaxDisplayingSizeTest(IDisplayingPile<ResourceCard> pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var pile = new Level1ResourcePile(CardDeck.get(numOfPlayers).level1Resource);
    assertTrue(pile.getDeck().size() <= CardDeck.get(numOfPlayers).level1Resource.size() - pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardSizeTest(IDisplayingPile<ResourceCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
    assertEquals(0, pile.discardPileSize());
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
      displaying = pile.getDisplaying();
      assertEquals(sizes, displaying.stream().map(List::size).toList());
      assertEquals(List.of(card), cards);
    }
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void takeCardsSingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new Level1ResourcePile(CardDeck.get(NumOfPlayers.FOUR).level1Resource);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardEmptyCaseTest(IDisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var initNumOfCardWithCardIdentity1 = pile.getDisplaying().get(0).size();

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      assertDoesNotThrow(() -> pile.takeCards(List.of(cardIdentity1)));
    }

    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(List.of(cardIdentity1)));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity2, cardIdentity2));

    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, cards.size());
    assertEquals(initNumOfCardWithCardIdentity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(initNumOfCardWithCardIdentity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsShouldThrowTest(IDisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = new CardIdentity(CardType.BASIC_RESOURCE, 1);
    var cardIdentity2 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity3 = new CardIdentity(CardType.LEVEL_1_RESOURCE, 99999);
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
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var initNumOfCardWithCardIdentity1 = pile.getDisplaying().get(0).size();

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
    var card1 = pile.getDisplaying().get(0).get(0);
    var cardIdentity1 = card1.getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var cards = pile.takeCards(List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);

    pile.discardCards(cards);
    assertEquals(1, pile.getDiscardPile().size());
    assertEquals(1, pile.discardPileSize());
    assertEquals(card1, pile.getDiscardPile().get(0));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsMultipleCardsTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity2, cardIdentity2));

    var cards = pile.takeCards(list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, pile.getDiscardPile().size());
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, pile.discardPileSize());
    assertTrue(pile.getDiscardPile().containsAll(cards));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 1), "test", 1, null, null);
    var card2 = new ResourceCard(new CardIdentity(CardType.LEVEL_2_RESOURCE, 1), "test", 1, null, null);
    var card3 = pile.takeCards(List.of(pile.getDisplaying().get(0).get(0).getCardIdentity())).get(0);

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2)));

    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card2, card3)));
    assertThrows(DisplayPileException.class, () -> pile.discardCards(List.of(card1, card2, card3)));

    assertDoesNotThrow(() -> pile.discardCards(List.of(card3)));
  }

  @Test
  void refillCardStopWhenDeckUsesUp() throws DisplayPileException {
    var mockDeck = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new Level1ResourcePile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().get(0).size());
    assertEquals(1, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    pile.refillCards();

    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().get(0).size());
    assertEquals(1, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());
  }

  @Test
  void refillCardFromDiscardPileWhenDeckUsedUp() throws DisplayPileException {
    var mockDeck = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new Level1ResourcePile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().get(0).size());
    assertEquals(1, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    var card4Discard = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.LEVEL_1_RESOURCE, 99999))
        .name("unique card for testing")
        .value(1)
        .build();
    pile.discardCards(List.of(card4Discard));
    assertEquals(1, pile.discardPileSize());

    pile.refillCards();

    assertEquals(0, pile.discardPileSize());

    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().get(0).size());
    assertEquals(1, pile.getDisplaying().get(1).size());
    assertEquals(2, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());
  }

  private ArrayList<ResourceCard> buildMockDeckWithOnlyOneTypeOfCard() {
    var level1Resource = new ArrayList<ResourceCard>();
    for (var i = 0; i < 3; i++) {
      level1Resource.add(ResourceCard.builder()
          .cardIdentity(new CardIdentity(CardType.LEVEL_1_RESOURCE, 1))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }
    return level1Resource;
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void refillCardsTest(IDisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies((int) initNumOfCardWithCardIdentity2, cardIdentity2));

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

  private long numOfCardsWithCardIdentityInDisplaying(List<List<ResourceCard>> displaying, CardIdentity cardIdentity) {
    return displaying.stream()
        .map(x -> x.stream().filter(y -> y.isIdentical(cardIdentity)).count())
        .reduce(0L, Long::sum);
  }
}