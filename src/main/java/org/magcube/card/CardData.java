package org.magcube.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.magcube.enums.CardType;

public class CardData {

  public static final List<ResourceCard> basicResource;
  public static final List<ResourceCard> levelOneResource;
  public static final List<ResourceCard> levelTwoResource;
  public static final List<BuildingCard> building;
  public static final HashMap<CardType, Integer> maxTypeId = new HashMap<>();

  static {
    try {
      basicResource = loadCard(ResourceCard.class, "/type0.json");
      levelOneResource = loadCard(ResourceCard.class, "/type1.json");
      levelTwoResource = loadCard(ResourceCard.class, "/type2.json");
      building = loadCard(BuildingCard.class, "/type3.json");

      maxTypeId.put(CardType.BASIC_RESOURCE, maxTypeIdInList(basicResource));
      maxTypeId.put(CardType.LEVEL_ONE_RESOURCE, maxTypeIdInList(levelOneResource));
      maxTypeId.put(CardType.LEVEL_TWO_RESOURCE, maxTypeIdInList(levelTwoResource));
      maxTypeId.put(CardType.BUILDING, maxTypeIdInList(building));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends Card> List<T> loadCard(Class<T> clazz, String filename) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    InputStream inputStream = CardData.class.getResourceAsStream(filename);
    return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
  }

  private static int maxTypeIdInList(List<? extends Card> cards) {
    //noinspection OptionalGetWithoutIsPresent
    return cards.stream().map(Card::typeId).max(Integer::compare).get();
  }
}
