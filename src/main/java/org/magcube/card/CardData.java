package org.magcube.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

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

      //noinspection OptionalGetWithoutIsPresent
      maxTypeId.put(CardType.BASIC_RESOURCE, basicResource.stream().map(Card::typeId).max(Integer::compare).get());
      //noinspection OptionalGetWithoutIsPresent
      maxTypeId.put(CardType.LEVEL_ONE_RESOURCE, levelOneResource.stream().map(Card::typeId).max(Integer::compare).get());
      //noinspection OptionalGetWithoutIsPresent
      maxTypeId.put(CardType.LEVEL_TWO_RESOURCE, levelTwoResource.stream().map(Card::typeId).max(Integer::compare).get());
      //noinspection OptionalGetWithoutIsPresent
      maxTypeId.put(CardType.BUILDING, building.stream().map(Card::typeId).max(Integer::compare).get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends Card> List<T> loadCard(Class<T> clazz, String filename)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    InputStream inputStream = CardData.class.getResourceAsStream(filename);
    return objectMapper.readValue(inputStream,
        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
  }
}

