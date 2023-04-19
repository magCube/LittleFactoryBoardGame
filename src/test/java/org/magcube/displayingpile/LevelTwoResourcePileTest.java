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
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.player.NumOfPlayers;

public class LevelTwoResourcePileTest {

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

  private static Stream<LevelTwoResourcePile> pileProvider() throws DisplayPileException {
    return Stream.of(
        new LevelTwoResourcePile(CardDeck.get(NumOfPlayers.TWO).levelTwoResource),
        new LevelTwoResourcePile(CardDeck.get(NumOfPlayers.THREE).levelTwoResource),
        new LevelTwoResourcePile(CardDeck.get(NumOfPlayers.FOUR).levelTwoResource)
    );
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest1(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new LevelTwoResourcePile(CardDeck.get(numOfPlayers).levelTwoResource));
  }

  @Test
  void constructorTest2() {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      mockDeck.add(new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i), "test" + i, 1, null, null));
    }
    assertDoesNotThrow(() -> new LevelTwoResourcePile(mockDeck));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorShouldThrowTest1(NumOfPlayers numOfPlayers) {
    assertThrows(DisplayPileException.class, () -> new LevelTwoResourcePile(CardDeck.get(numOfPlayers).basicResource));
    assertThrows(DisplayPileException.class, () -> new LevelTwoResourcePile(CardDeck.get(numOfPlayers).levelOneResource));
  }

  @ParameterizedTest
  @EnumSource(value = CardType.class, mode = Mode.INCLUDE, names = {"BASIC_RESOURCE", "LEVEL_ONE_RESOURCE"})
  void constructorShouldThrowTest2(CardType inconsistantCardType) {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i), "test" + i + j, 1, null, null));
      }
    }
    mockDeck.add(new ResourceCard(new CardIdentity(inconsistantCardType, 1), "test", 1, null, null));
    assertThrows(DisplayPileException.class, () -> new LevelTwoResourcePile(mockDeck));
  }

  @Test
  void constructorShouldThrowTest3() {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    mockDeck.add(new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 0), "test" + 0, 5, null, null));
    for (int i = 0; i < 8; i++) {
      var card = new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i), "test" + i, 5, null, null);
      mockDeck.add(card);
    }
    assertThrows(DisplayPileException.class, () -> new LevelTwoResourcePile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.LEVEL_TWO_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().allMatch(x -> x.size() == 1 && x.get(0) != null));
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var pile = new LevelTwoResourcePile(CardDeck.get(numOfPlayers).levelTwoResource);
    assertEquals(pile.getDeck().size(), CardDeck.get(numOfPlayers).levelTwoResource.size() - pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardPileTest(DisplayingPile<ResourceCard> pile) {
    assertEquals(0, pile.getDiscardPile().size());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getMaxDisplayingSizeTest1(DisplayingPile<ResourceCard> pile) {
    assertTrue(pile.getMaxDisplayingSize() > 0);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
  void getMaxDisplayingSizeTest2(int size) throws DisplayPileException {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      mockDeck.add(new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i), "test" + i, 1, null, null));
    }
    var pile = new LevelTwoResourcePile(mockDeck);
    assertEquals(5, pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDiscardSizeTest(DisplayingPile<ResourceCard> pile) {
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
    var card = new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999), "test", 1, null, null);
    assertNull(pile.cardsInDisplay(List.of(card.getCardIdentity())));
  }

  private static Stream<CardIdentity> invalidCardIdentitiesProvider() {
    return Stream.of(
        new CardIdentity(CardType.BASIC_RESOURCE, 1),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1),
        new CardIdentity(CardType.BUILDING, 1),
        new CardIdentity(CardType.BASIC_RESOURCE, 99999),
        new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999),
        new CardIdentity(CardType.BUILDING, 99999)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidCardIdentitiesProvider")
  void cardsInDisplaySingleCardShouldThrowTest(CardIdentity cardIdentity) throws DisplayPileException {
    var pile = new LevelTwoResourcePile(CardDeck.get(NumOfPlayers.FOUR).levelTwoResource);
    assertThrows(DisplayPileException.class, () -> pile.cardsInDisplay(List.of(cardIdentity)));
  }


  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
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
  void cardsInDisplayMultipleCardsTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();

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
  void cardsInDisplayMultipleCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
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
  void cardsInDisplayMultipleCardsEmptyCaseTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
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
  void takeCardsSingleCardTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
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
  void takeCardsMultipleCardsTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    List<List<ResourceCard>> displaying = pile.getDisplaying();

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
    assertEquals(cardsInDisplay, cardsToken);
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
  void takeCardsMultipleCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    ResourceCard validCard1 = pile.getDisplaying().get(0).get(0);
    ResourceCard validCard2 = pile.getDisplaying().get(1).get(0);
    ResourceCard invalidCard = new ResourceCard(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1), "invalid", 0, null, null);

    var initDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();

    var cards = List.of(validCard1, validCard2, invalidCard);
    assertThrows(DisplayPileException.class, () -> pile.takeCards(cards));

    var displayFlatten = pile.getDisplaying().stream().flatMap(List::stream).toList();
    var curDisplayingCard = pile.getDisplaying().stream().flatMap(List::stream).toList().size();
    // should not remove any cards if one is invalid
    assertTrue(displayFlatten.contains(validCard1));
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
    var card1 = pile.getDisplaying().get(0).get(0);
    var cardIdentity1 = card1.getCardIdentity();

    var cards = takeCardHelper(pile, List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertFalse(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));

    pile.discardCards(cards);
    assertEquals(1, pile.getDiscardPile().size());
    assertEquals(1, pile.discardPileSize());
    assertEquals(card1, pile.getDiscardPile().get(0));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsMultipleCardsTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    var cards = takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsWithCardIdentityInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    assertEquals(2, pile.getDiscardPile().size());
    assertEquals(2, pile.discardPileSize());
    assertTrue(pile.getDiscardPile().containsAll(cards));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsShouldThrowTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = new ResourceCard(new CardIdentity(CardType.BASIC_RESOURCE, 1), "test", 1, null, null);
    var card2 = new ResourceCard(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1), "test", 1, null, null);
    var card3 = takeCardHelper(pile, List.of(pile.getDisplaying().get(0).get(0).getCardIdentity())).get(0);

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
    var mockDeck = new ArrayList<ResourceCard>();
    for (var i = 0; i < 3; i++) {
      mockDeck.add(ResourceCard.builder()
          .cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }

    var pile = new LevelTwoResourcePile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    pile.refillCards();

    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());
  }


  @Test
  void refillCardFromDiscardPileWhenDeckUsedUp() throws DisplayPileException {
    var mockDeck = new ArrayList<ResourceCard>();
    for (var i = 0; i < 3; i++) {
      mockDeck.add(ResourceCard.builder()
          .cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }
    var pile = new LevelTwoResourcePile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    var card4Discard = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999))
        .name("unique card for testing")
        .value(1)
        .build();
    pile.discardCards(List.of(card4Discard));
    assertEquals(1, pile.discardPileSize());

    pile.refillCards();

    assertEquals(0, pile.discardPileSize());

    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size() + 1, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());
  }


  @ParameterizedTest
  @MethodSource("pileProvider")
  void refillCardsTest(DisplayingPile<ResourceCard> pile) throws DisplayPileException {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    var cards = takeCardHelper(pile, list);
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
