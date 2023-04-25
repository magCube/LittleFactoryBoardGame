package org.magcube.card;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.magcube.exception.CardQuantityException;
import org.magcube.player.NumOfPlayers;

public record CardQuantity(CardIdentity cardIdentity, int twoPlayers, int threePlayers, int fourPlayers) {

  public static final List<CardQuantity> basicResource;
  public static final List<CardQuantity> levelOneResource;
  public static final List<CardQuantity> levelTwoResource;
  public static final List<CardQuantity> building;

  static {
    try {
      basicResource = loadCard("/type0-quantity.json");
      levelOneResource = loadCard("/type1-quantity.json");
      levelTwoResource = loadCard("/type2-quantity.json");
      building = loadCard("/type3-quantity.json");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<CardQuantity> loadCard(String filename) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    InputStream inputStream = CardIdentity.class.getResourceAsStream(filename);
    return objectMapper.readValue(inputStream, new TypeReference<>() {
    });
  }

  public static int getQuantity(CardType cardType, int typeId, NumOfPlayers numOfPlayers) throws CardQuantityException {
    List<CardQuantity> cardQuantities = switch (cardType) {
      case BASIC_RESOURCE -> basicResource;
      case LEVEL_ONE_RESOURCE -> levelOneResource;
      case LEVEL_TWO_RESOURCE -> levelTwoResource;
      case BUILDING -> building;
    };

    var cardQuantity = cardQuantities.stream()
        .filter(x -> x.cardType() == cardType && x.typeId() == typeId)
        .findFirst();

    if (cardQuantity.isEmpty()) {
      throw new CardQuantityException(cardType, typeId);
    }

    return cardQuantity.get().getQuantityForNumOfPlayers(numOfPlayers);
  }

  // don't use getCardType as name as Jackson will parse it is a field
  public CardType cardType() {
    return cardIdentity.cardType();
  }

  // don't use getTypeId as name as Jackson will parse it is a field
  public int typeId() {
    return cardIdentity.typeId();
  }

  public int getQuantityForNumOfPlayers(NumOfPlayers numOfPlayers) {
    return switch (numOfPlayers) {
      case TWO -> twoPlayers;
      case THREE -> threePlayers;
      case FOUR -> fourPlayers;
    };
  }
}
