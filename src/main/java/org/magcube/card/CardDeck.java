package org.magcube.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.magcube.enums.NumOfPlayers;
import org.magcube.exception.CardQuantityException;

public class CardDeck {

  private static final HashMap<NumOfPlayers, CardDeck> data = new HashMap<>();

  public final List<ResourceCard> basicResource;
  public final List<ResourceCard> levelOneResource;
  public final List<ResourceCard> levelTwoResource;
  public final List<BuildingCard> building;

  static {
    try {
      data.put(NumOfPlayers.TWO, new CardDeck(NumOfPlayers.TWO));
      data.put(NumOfPlayers.THREE, new CardDeck(NumOfPlayers.THREE));
      data.put(NumOfPlayers.FOUR, new CardDeck(NumOfPlayers.FOUR));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static CardDeck get(NumOfPlayers numOfPlayers) {
    return data.get(numOfPlayers);
  }

  private static <T extends Card> List<T> deepCloneCard(T card, int quantity, Class<T> clazz) throws JsonProcessingException {
    List<T> cards = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(card);
    for (int i = 0; i < quantity; i++) {
      cards.add(objectMapper.readValue(json, clazz));
    }
    return cards;
  }

  private static <T extends Card> List<T> getCards(List<T> uniqueCards, NumOfPlayers numOfPlayers, Class<T> clazz)
      throws CardQuantityException, JsonProcessingException {
    List<T> cards = new ArrayList<>();
    for (T card : uniqueCards) {
      int quantity = CardQuantity.getQuantity(card.cardType(), card.typeId(), numOfPlayers);
      cards.addAll(deepCloneCard(card, quantity, clazz));
    }
    return Collections.unmodifiableList(cards);
  }

  private CardDeck(NumOfPlayers numOfPlayers) throws JsonProcessingException, CardQuantityException {
    this.basicResource = getCards(CardData.basicResource, numOfPlayers, ResourceCard.class);
    this.levelOneResource = getCards(CardData.levelOneResource, numOfPlayers, ResourceCard.class);
    this.levelTwoResource = getCards(CardData.levelTwoResource, numOfPlayers, ResourceCard.class);
    this.building = getCards(CardData.building, numOfPlayers, BuildingCard.class);
  }
}
