package org.magcube.card;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import org.magcube.exception.CardQuantityException;
import org.magcube.exception.NumOfPlayersException;

@ToString
@Getter
public class CardQuantity {

  public static final List<CardQuantity> basicResource;
  public static final List<CardQuantity> level1Resource;
  public static final List<CardQuantity> level2Resource;
  public static final List<CardQuantity> building;

  private CardType cardType;
  private int typeId;
  private int twoPlayers;
  private int threePlayers;
  private int fourPlayers;

  static {
    try {
      basicResource = loadCard("/type0-quantity.json");
      level1Resource = loadCard("/type1-quantity.json");
      level2Resource = loadCard("/type2-quantity.json");
      building = loadCard("/type3-quantity.json");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<CardQuantity> loadCard(String filename)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    InputStream inputStream = CardData.class.getResourceAsStream(filename);
    return objectMapper.readValue(inputStream, new TypeReference<List<CardQuantity>>() {
    });
  }

  public static int getQuantity(CardType cardType, int typeId, int numOfPlayers)
      throws CardQuantityException, NumOfPlayersException {
    List<CardQuantity> cardQuantities = switch (cardType) {
      case BASIC_RESOURCE -> basicResource;
      case LEVEL_1_RESOURCE -> level1Resource;
      case LEVEL_2_RESOURCE -> level2Resource;
      case BUILDING -> building;
      default -> null;
    };

    Optional<CardQuantity> cardQuantity = cardQuantities.stream()
        .filter(x -> x.getCardType() == cardType && x.getTypeId() == typeId)
        .findFirst();

    if (cardQuantity.isEmpty()) {
      String message = String.format("Card not found: cardType=%s, typeId=%d", cardType, typeId);
      throw new CardQuantityException(message);
    }

    return cardQuantity.get().getQuantityForNumOfPlayers(numOfPlayers);
  }

  public CardQuantity() {
  }

  public CardQuantity(CardType cardType, int typeId, int twoPlayers, int threePlayers,
      int fourPlayers) {
    this.cardType = cardType;
    this.typeId = typeId;
    this.twoPlayers = twoPlayers;
    this.threePlayers = threePlayers;
    this.fourPlayers = fourPlayers;
  }

  public int getQuantityForNumOfPlayers(int numOfPlayers)
      throws NumOfPlayersException {
    return switch (numOfPlayers) {
      case 2 -> twoPlayers;
      case 3 -> threePlayers;
      case 4 -> fourPlayers;
      default -> throw new NumOfPlayersException(numOfPlayers);
    };
  }
}
