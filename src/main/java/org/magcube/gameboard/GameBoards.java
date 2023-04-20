package org.magcube.gameboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
      data.computeIfAbsent(cardIdentity.getCardType(), k -> new ArrayList<>()).add(cardIdentity);
    }
    return data;
  }

  public static HashMap<CardType, List<? extends Card>> categorizeCards(List<? extends Card> cards) {
    HashMap<CardType, List<? extends Card>> data = new HashMap<>();

    ArrayList<ResourceCard> basicResourceCards = new ArrayList<>();
    ArrayList<ResourceCard> levelOneResourceCards = new ArrayList<>();
    ArrayList<ResourceCard> levelTwoResourceCards = new ArrayList<>();
    ArrayList<BuildingCard> buildingCards = new ArrayList<>();

    for (var card : cards) {
      switch (card.cardType()) {
        case BASIC_RESOURCE -> basicResourceCards.add((ResourceCard) card);
        case LEVEL_ONE_RESOURCE -> levelOneResourceCards.add((ResourceCard) card);
        case LEVEL_TWO_RESOURCE -> levelTwoResourceCards.add((ResourceCard) card);
        case BUILDING -> buildingCards.add((BuildingCard) card);
      }
    }

    if (!basicResourceCards.isEmpty()) {
      data.put(CardType.BASIC_RESOURCE, basicResourceCards);
    }
    if (!levelOneResourceCards.isEmpty()) {
      data.put(CardType.LEVEL_ONE_RESOURCE, levelOneResourceCards);
    }
    if (!levelTwoResourceCards.isEmpty()) {
      data.put(CardType.LEVEL_TWO_RESOURCE, levelTwoResourceCards);
    }
    if (!buildingCards.isEmpty()) {
      data.put(CardType.BUILDING, buildingCards);
    }

    return data;
  }

  public static boolean isCardIdentitiesValid(List<CardIdentity> cardIdentities) {
    for (var cardIdentity : cardIdentities) {
      var cardType = cardIdentity.getCardType();
      if (cardType == null) {
        return false;
      } else {
        int typeId = cardIdentity.getTypeId();
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
}
