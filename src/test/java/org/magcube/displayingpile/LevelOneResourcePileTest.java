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

class LevelOneResourcePileTest {

  private static Stream<LevelOneResourcePile> pileProvider() {
    return Stream.of(
        new LevelOneResourcePile(CardDeck.get(NumOfPlayers.TWO).levelOneResource),
        new LevelOneResourcePile(CardDeck.get(NumOfPlayers.THREE).levelOneResource),
        new LevelOneResourcePile(CardDeck.get(NumOfPlayers.FOUR).levelOneResource)
    );
  }

  private static ArrayList<ResourceCard> buildMockDeckWithOnlyOneTypeOfCard() {
    var levelOneResource = new ArrayList<ResourceCard>();
    for (var i = 0; i < 3; i++) {
      levelOneResource.add(ResourceCard.builder()
          .cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1))
          .name(String.valueOf(i))
          .value(1)
          .build()
      );
    }
    return levelOneResource;
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void constructorTest1(NumOfPlayers numOfPlayers) {
    assertDoesNotThrow(() -> new LevelOneResourcePile(CardDeck.get(numOfPlayers).levelOneResource));
  }

  @Test
  void constructorTest2() {
    ArrayList<ResourceCard> mockDeck = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, i)).name("test" + i + j).value(1).build());
      }
    }
    assertDoesNotThrow(() -> new LevelOneResourcePile(mockDeck));
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getCardTypeTest(DisplayingPile<Card> pile) {
    assertEquals(CardType.LEVEL_ONE_RESOURCE, pile.getCardType());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void getDisplayingTest1(DisplayingPile<ResourceCard> pile) {
    List<List<ResourceCard>> displaying = pile.getDisplaying();
    assertEquals(5, displaying.size());
    assertEquals(5, pile.getMaxDisplayingSize());
    assertTrue(displaying.stream().noneMatch(List::isEmpty));
    displaying.forEach(innerList -> {
      var first = innerList.get(0).getCardIdentity();
      assertTrue(innerList.stream().allMatch(c -> c.getCardIdentity().equals(first)));
    });
  }

  @Test
  void getDisplayTest2() throws NoSuchFieldException, IllegalAccessException {
    List<List<ResourceCard>> mockDisplaying = List.of(
        List.of(
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build(),
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 1)).build()
        ),
        List.of(
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build(),
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 2)).build()
        ),
        List.of(
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build(),
            ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 3)).build()
        ),
        List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 4)).build()),
        List.of(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 5)).build())
    );

    var pile = new LevelOneResourcePile(Collections.emptyList());
    assertEquals(Collections.nCopies(5, Collections.emptyList()), pile.getDisplaying());

    Field field = pile.getClass().getDeclaredField("displaying");
    field.setAccessible(true);
    field.set(pile, mockDisplaying);
    assertEquals(mockDisplaying, pile.getDisplaying());
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckTest(NumOfPlayers numOfPlayers) {
    var pile = new LevelOneResourcePile(CardDeck.get(numOfPlayers).levelOneResource);
    var numOfCardsInDisplaying = pile.getDisplaying().stream().mapToInt(List::size).sum();
    assertTrue(numOfCardsInDisplaying >= pile.getMaxDisplayingSize());
    assertTrue(pile.getDeck().size() > 0);
    assertEquals(pile.getDeck().size(), CardDeck.get(numOfPlayers).levelOneResource.size() - numOfCardsInDisplaying);
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
      for (int j = 0; j < 3; j++) {
        mockDeck.add(ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, i)).name("test" + i + j).value(1).build());
      }
    }
    var pile = new LevelOneResourcePile(mockDeck);
    assertEquals(5, pile.getMaxDisplayingSize());
  }

  @ParameterizedTest
  @EnumSource(NumOfPlayers.class)
  void getDeckSizeTest(NumOfPlayers numOfPlayers) {
    var pile = new LevelOneResourcePile(CardDeck.get(numOfPlayers).levelOneResource);
    var numOfCardsInDisplaying = pile.getDisplaying().stream().mapToInt(List::size).sum();
    assertEquals(pile.deckSize(), pile.getDeck().size());
    assertTrue(pile.deckSize() > 0);
    assertTrue(pile.deckSize() <= CardDeck.get(numOfPlayers).levelOneResource.size() - numOfCardsInDisplaying);
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
    var card = ResourceCard.builder().cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999)).name("test").value(1).build();
    assertTrue(pile.cardsInDisplay(List.of(card.getCardIdentity())).isEmpty());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplaySingleCardEmptyCaseTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var initNumOfCardWithCardIdentity1 = pile.getDisplaying().get(0).size();

    for (int i = 0; i < initNumOfCardWithCardIdentity1; i++) {
      var optCards = pile.cardsInDisplay(List.of(cardIdentity1));
      assertTrue(optCards.isPresent());
      pile.takeCards(optCards.get());
    }

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

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity2, cardIdentity2));
    var optCards = pile.cardsInDisplay(list);
    assertTrue(optCards.isPresent());
    var cards = optCards.get();
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, cards.size());
    assertEquals(initNumOfCardWithCardIdentity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(initNumOfCardWithCardIdentity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());
  }

  @ParameterizedTest
  @MethodSource("pileProvider")
  void cardsInDisplayMultipleCardsShouldReturnOptionalEmptyTest(DisplayingPile<ResourceCard> pile) {
    var cardIdentity1 = pile.getDisplaying().get(0).get(0).getCardIdentity();
    var cardIdentity2 = new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999);
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
    List<List<ResourceCard>> displaying = pile.getDisplaying();

    var cardIdentity1 = displaying.get(0).get(0).getCardIdentity();
    var cardIdentity2 = displaying.get(1).get(0).getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    assertTrue(pile.cardsInDisplay(Collections.nCopies(initNumOfCardWithCardIdentity1 + 1, cardIdentity1)).isEmpty());
    assertTrue(pile.cardsInDisplay(Collections.nCopies(initNumOfCardWithCardIdentity2 + 1, cardIdentity2)).isEmpty());

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity2, cardIdentity2));

    var optCards = pile.cardsInDisplay(list);
    assertTrue(optCards.isPresent());
    var cards = optCards.get();
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, cards.size());
    assertEquals(initNumOfCardWithCardIdentity1, cards.stream().filter(x -> x.isIdentical(cardIdentity1)).count());
    assertEquals(initNumOfCardWithCardIdentity2, cards.stream().filter(x -> x.isIdentical(cardIdentity2)).count());

    pile.takeCards(cards);

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
      var card = pile.getDisplaying().get(i).get(0);
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

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity2, cardIdentity2));

    var optCards = pile.cardsInDisplay(list);
    assertTrue(optCards.isPresent());
    pile.takeCards(optCards.get());

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

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var cards = takeCardHelper(pile, List.of(cardIdentity1));
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    assertEquals(initNumOfCardWithCardIdentity1 - 1, curNumOfCardWithCardIdentity1);

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

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity2, cardIdentity2));

    var cards = takeCardHelper(pile, list);
    var curNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var curNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);
    assertEquals(0, curNumOfCardWithCardIdentity1);
    assertEquals(0, curNumOfCardWithCardIdentity2);

    pile.discardCards(cards);
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, pile.getDiscardPile().size());
    assertEquals(initNumOfCardWithCardIdentity1 + initNumOfCardWithCardIdentity2, pile.discardPileSize());
    assertTrue(pile.getDiscardPile().containsAll(cards));
  }

  @Test
  void refillCardStopWhenDeckUsesUp() {
    var mockDeck = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new LevelOneResourcePile(mockDeck);
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
  void refillCardFromDiscardPileWhenDeckUsedUp() {
    var mockDeck = buildMockDeckWithOnlyOneTypeOfCard();
    var pile = new LevelOneResourcePile(mockDeck);
    assertEquals(0, pile.deckSize());
    assertEquals(5, pile.getDisplaying().size());
    assertEquals(mockDeck.size(), pile.getDisplaying().get(0).size());
    assertEquals(1, pile.getDisplaying().stream().filter(innerList -> innerList.size() > 0).count());

    var card4Discard = ResourceCard.builder()
        .cardIdentity(new CardIdentity(CardType.LEVEL_ONE_RESOURCE, 99999))
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

  @ParameterizedTest
  @MethodSource("pileProvider")
  void refillCardsTest(DisplayingPile<ResourceCard> pile) {
    var card1 = pile.getDisplaying().get(0).get(0);
    var card2 = pile.getDisplaying().get(1).get(0);
    var cardIdentity1 = card1.getCardIdentity();
    var cardIdentity2 = card2.getCardIdentity();

    var initNumOfCardWithCardIdentity1 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity1);
    var initNumOfCardWithCardIdentity2 = numOfCardsInDisplaying(pile.getDisplaying(), cardIdentity2);

    var list = new ArrayList<CardIdentity>();
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity1, cardIdentity1));
    list.addAll(Collections.nCopies(initNumOfCardWithCardIdentity2, cardIdentity2));

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