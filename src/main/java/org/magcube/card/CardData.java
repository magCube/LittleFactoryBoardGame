package org.magcube.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CardData {

  static public final List<ResourceCard> basicResource;
  static public final List<ResourceCard> levelOneResource;
  static public final List<ResourceCard> levelTwoResource;
  static public final List<BuildingCard> building;

  static {
    try {
      basicResource = loadCard(ResourceCard.class, "/type0.json");
      levelOneResource = loadCard(ResourceCard.class, "/type1.json");
      levelTwoResource = loadCard(ResourceCard.class, "/type2.json");
      building = loadCard(BuildingCard.class, "/type3.json");
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

