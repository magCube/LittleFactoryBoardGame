package org.magcube.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.magcube.exception.CardQuantityException;
import org.magcube.exception.NumOfPlayersException;

public class CardDeck {

  private static final HashMap<Integer, CardDeck> data = new HashMap<>();

  public final List<ResourceCard> basicResource;
  public final List<ResourceCard> level1Resource;
  public final List<ResourceCard> level2Resource;
  public final List<BuildingCard> building;

  static {
    try {
      data.put(2, new CardDeck(2));
      data.put(3, new CardDeck(3));
      data.put(4, new CardDeck(4));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static CardDeck get(int numOfPlayers) throws NumOfPlayersException {
    var deck = data.get(numOfPlayers);
    if (deck == null) {
      throw new NumOfPlayersException(numOfPlayers);
    } else {
      return deck;
    }
  }

  private static <T extends Card> List<T> deepCloneCard(T card, int quantity, Class<T> clazz)
      throws JsonProcessingException {
    List<T> cards = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(card);
    for (int i = 0; i < quantity; i++) {
      cards.add(objectMapper.readValue(json, clazz));
    }
    return cards;
  }

  private static <T extends Card> List<T> getCards(List<T> uniqueCards, int numOfPlayers,
      Class<T> clazz) throws CardQuantityException, JsonProcessingException, NumOfPlayersException {
    List<T> cards = new ArrayList<>();
    for (T card : uniqueCards) {
      int quantity = CardQuantity.getQuantity(card.getCardType(), card.getTypeId(), numOfPlayers);
      cards.addAll(deepCloneCard(card, quantity, clazz));
    }
    return cards;
  }

  private CardDeck(int numOfPlayers)
      throws JsonProcessingException, CardQuantityException, NumOfPlayersException {
    this.basicResource = getCards(CardData.basicResource, numOfPlayers, ResourceCard.class);
    this.level1Resource = getCards(CardData.level1Resource, numOfPlayers, ResourceCard.class);
    this.level2Resource = getCards(CardData.level2Resource, numOfPlayers, ResourceCard.class);
    this.building = getCards(CardData.building, numOfPlayers, BuildingCard.class);
  }
}
