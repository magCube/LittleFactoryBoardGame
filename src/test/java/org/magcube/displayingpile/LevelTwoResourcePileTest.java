package org.magcube.displayingpile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.magcube.displayingpile.DisplayingPileTestUtil.numOfCardsInDisplaying;
import static org.magcube.displayingpile.DisplayingPileTestUtil.takeCardHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.player.NumOfPlayers;

public class LevelTwoResourcePileTest {

  private static Stream<LevelTwoResourcePile> pileProvider() {
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
      mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i)).build());
    }
    assertDoesNotThrow(() -> new LevelTwoResourcePile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.LEVEL_TWO_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest1(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().allMatch(x -> x.size() == 1 && x.get(0) != null));
  }

  @Test
  void getDisplayingTest2() throws NoSuchFieldException, IllegalAccessException {
    List<ResourceCard> mockDisplaying = new ArrayList<>();
    mockDisplaying.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 1)).build());
    mockDisplaying.add(null);
    mockDisplaying.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 3)).build());
    mockDisplaying.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 4)).build());
    mockDisplaying.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 5)).build());

    var pile = new LevelTwoResourcePile(List.of());

    assertEquals(Collections.nCopies(5, List.of()), pile.getDisplaying());

    Field field = UniqueCardPile.class.getDeclaredField("availableCards");
    field.setAccessible(true);
    field.set(pile, mockDisplaying);

    assertEquals(List.of(
        List.of(mockDisplaying.get(0)),
        List.of(),
        List.of(mockDisplaying.get(2)),
        List.of(mockDisplaying.get(3)),
        List.of(mockDisplaying.get(4))
    ), pile.getDisplaying());
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) {
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
  void getMaxDisplayingSizeTest2(int size) {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, i)).build());
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
  void cardsInDisplaySingleCardShouldReturnOptionalEmptyTest(DisplayingPile<ResourceCard> pile) {
    var card = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999)).build();
    assertTrue(pile.cardsInDisplay(List.of(card.getCardIdentity())).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();

    var optCardsInDisplay = pile.cardsInDisplay(List.of(cardIdentity1));
    assertTrue(optCardsInDisplay.isPresent());
    pile.takeCards(optCardsInDisplay.get());

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(1, initNumOfCardWithCardIdentity1);
    assertEquals(1, initNumOfCardWithCardIdentity2);

    var list = List.of(cardIdentity1, cardIdentity2);
    var optCards = pile.cardsInDisplay(list);
    assertTrue(optCards.isPresent());
    var cards = optCards.get();
    assertEquals(2, cards.size());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsShouldReturnOptionalEmptyTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity2 = new CardIdentity(CardType.LEVEL_TWO_RESOURCE, 99999);
    var cardIdentity3 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);

    assertFalse(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity3)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity3)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity3, cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity3)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2, cardIdentity3)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsEmptyCaseTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity2 = pile.getDisplaying().get(1).get(0).getCardIdentity();

    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity2)).isEmpty());

    var optCardsInDisplay = pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2));
    assertTrue(optCardsInDisplay.isPresent());
    var cardsInDisplay = optCardsInDisplay.get();
    assertEquals(2, cardsInDisplay.size());
    assertEquals(1, cardsInDisplay.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(1, cardsInDisplay.stream().filter(x -> x.isIdentical(cardIdentity2)).count());

    pile.takeCards(cardsInDisplay);

    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity1, cardIdentity2)).isEmpty());
    assertTrue(pile.cardsInDisplay(List.of(cardIdentity2, cardIdentity1)).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsSingleCardTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    List<Integer> sizes = new ArrayList<>(displaying.stream().map(List::size).toList());

    assertTrue(displaying.size() > 0);
    assertEquals(pile.getMaxDisplayingSize(), displaying.size());

    for (int i = 0; i < displaying.size(); i++) {
      var card = displaying.get(i).get(0);
      var optCardsInDisplay = pile.cardsInDisplay(List.of(card.getCardIdentity()));
      assertTrue(optCardsInDisplay.isPresent());
      pile.takeCards(optCardsInDisplay.get());
      sizes.set(i, sizes.get(i) - 1);
      assertEquals(sizes, pile.getDisplaying().stream().map(List::size).toList());
    }
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void takeCardsMultipleCardsTest(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(1, initNumOfCardWithCardIdentity1);
    assertEquals(1, initNumOfCardWithCardIdentity2);

    var list = List.of(cardIdentity1, cardIdentity2);
    var optCardsInDisplay = pile.cardsInDisplay(list);
    assertTrue(optCardsInDisplay.isPresent());
    pile.takeCards(optCardsInDisplay.get());
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsSingleCardTest(DisplayingPile<ResourceCard> pile) {
    var card1 = pile.getDisplaying().get(0).get(0);
    var cardIdentity1 = card1.getCardIdentity();

    var cards = takeCardHelper(pile, List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertFalse(pile.getDisplaying().stream().flatMap(List::stream).toList().containsAll(cards));

    pile.discardCards(cards);
    assertEquals(1, pile.getDiscardPile().size());
    assertEquals(1, pile.discardPileSize());
    assertEquals(card1, pile.getDiscardPile().get(0));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void discardCardsMultipleCardsTest(DisplayingPile<ResourceCard> pile) {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    var cards = takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    assertEquals(2, pile.getDiscardPile().size());
    assertEquals(2, pile.discardPileSize());
    assertTrue(pile.getDiscardPile().containsAll(cards));
  }

  @Test
  void refillCardStopWhenDeckUsesUp() {
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
  void refillCardFromDiscardPileWhenDeckUsedUp() {
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
  void refillCardsTest(DisplayingPile<ResourceCard> pile) {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var list = List.of(cardIdentity1, cardIdentity2);

    takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

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
