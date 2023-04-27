package org.magcube.gameboard;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardData;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class GameBoards {

  private GameBoards() {
  }

  public static HashMap<CardType, List<CardIdentity>> categorizeCardIdentities(List<CardIdentity> cardIdentities) {
    HashMap<CardType, List<CardIdentity>> data = new HashMap<>();
    for (var cardIdentity : cardIdentities) {
      data.computeIfAbsent(cardIdentity.cardType(), k -> new ArrayList<>()).add(cardIdentity);
    }
    return data;
  }

  public static HashMap<CardType, List<? extends Card>> categorizeCards(List<? extends Card> cards) {
    HashMap<CardType, List<? extends Card>> data = new HashMap<>();

    BiConsumer<CardType, ResourceCard> putResourceCard = (cardType, card) -> {
      @SuppressWarnings("unchecked")
      ArrayList<ResourceCard> list = (ArrayList<ResourceCard>) data.computeIfAbsent(cardType, k -> new ArrayList<ResourceCard>());
      list.add(card);
    };

    Consumer<BuildingCard> putBuildingCard = (card) -> {
      @SuppressWarnings("unchecked")
      ArrayList<BuildingCard> list = (ArrayList<BuildingCard>) data.computeIfAbsent(CardType.BUILDING, k -> new ArrayList<BuildingCard>());
      list.add(card);
    };

    for (var card : cards) {
      switch (card.cardType()) {
        case BASIC_RESOURCE -> putResourceCard.accept(CardType.BASIC_RESOURCE, (ResourceCard) card);
        case LEVEL_ONE_RESOURCE -> putResourceCard.accept(CardType.LEVEL_ONE_RESOURCE, (ResourceCard) card);
        case LEVEL_TWO_RESOURCE -> putResourceCard.accept(CardType.LEVEL_TWO_RESOURCE, (ResourceCard) card);
        case BUILDING -> putBuildingCard.accept((BuildingCard) card);
      }
    }

    return data;
  }

  public static boolean isCardIdentitiesValid(List<CardIdentity> cardIdentities) {
    for (var cardIdentity : cardIdentities) {
      var cardType = cardIdentity.cardType();
      if (cardType == null) {
        return false;
      } else {
        int typeId = cardIdentity.typeId();
        if (typeId > CardData.maxTypeId.get(cardType) || typeId < 1) {
          return false;
        }
      }
    }
    return true;
  }

  public static boolean isClassValid(List<? extends Card> cards) {
    for (var card : cards) {
      var isValid = switch (card.cardType()) {
        case BASIC_RESOURCE, LEVEL_ONE_RESOURCE, LEVEL_TWO_RESOURCE -> card instanceof ResourceCard;
        case BUILDING -> card instanceof BuildingCard;
      };
      if (!isValid) {
        return false;
      }
    }
    return true;
  }

  public static boolean isCardsValidate(List<? extends Card> cards) {
    return isCardIdentitiesValid(cards.stream().map(Card::getCardIdentity).collect(Collectors.toList())) &&
        isClassValid(cards);
  }

  public static boolean isNoBuildingCards(List<? extends Card> cards) {
    return cards.stream().noneMatch(card -> card.cardType() == CardType.BUILDING);
  }

  public static int sumOfCardsValue(List<? extends Card> cards) {
    return cards.stream().mapToInt(Card::getValue).sum();
  }

  public static int sumOfCardsValue(HashMap<CardType, List<? extends Card>> categorizedCards) {
    return categorizedCards.values().stream()
        .flatMap(Collection::stream)
        .mapToInt(Card::getValue)
        .sum();
  }

  public static List<ResourceCard> flattenResourceCardsFromCategorizedCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    var resourceCards = new ArrayList<ResourceCard>();
    for (var cardType : categorizedCards.keySet()) {
      if (cardType != CardType.BUILDING) {
        resourceCards.addAll(categorizedCards.get(cardType).stream()
            .map(card -> (ResourceCard) card)
            .toList());
      }
    }
    return resourceCards;
  }
}
