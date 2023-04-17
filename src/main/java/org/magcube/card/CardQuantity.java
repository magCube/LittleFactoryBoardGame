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
import org.magcube.exception.CardQuantityException;
import org.magcube.player.NumOfPlayers;

@ToString
@Getter
public class CardQuantity {

  public static final List<CardQuantity> basicResource;
  public static final List<CardQuantity> level1Resource;
  public static final List<CardQuantity> level2Resource;
  public static final List<CardQuantity> building;

  private CardIdentity cardIdentity;
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

  public static int getQuantity(CardType cardType, int typeId, NumOfPlayers numOfPlayers) throws CardQuantityException {
    List<CardQuantity> cardQuantities = switch (cardType) {
      case BASIC_RESOURCE -> basicResource;
      case LEVEL_1_RESOURCE -> level1Resource;
      case LEVEL_2_RESOURCE -> level2Resource;
      case BUILDING -> building;
    };

    Optional<CardQuantity> cardQuantity = cardQuantities.stream()
        .filter(x -> x.cardType() == cardType && x.typeId() == typeId)
        .findFirst();

    if (cardQuantity.isEmpty()) {
      String message = String.format("Card not found: cardType=%s, typeId=%d", cardType, typeId);
      throw new CardQuantityException(message);
    }

    return cardQuantity.get().getQuantityForNumOfPlayers(numOfPlayers);
  }

  public CardQuantity() {
  }

  public CardQuantity(CardIdentity cardIdentity, int twoPlayers, int threePlayers,
      int fourPlayers) {
    this.cardIdentity = cardIdentity;
    this.twoPlayers = twoPlayers;
    this.threePlayers = threePlayers;
    this.fourPlayers = fourPlayers;
  }

  // don't use getCardType as name as Jackson will parse it is a field
  public CardType cardType() {
    return cardIdentity.getCardType();
  }

  // don't use getTypeId as name as Jackson will parse it is a field
  public int typeId() {
    return cardIdentity.getTypeId();
  }

  public int getQuantityForNumOfPlayers(NumOfPlayers numOfPlayers) {
    return switch (numOfPlayers) {
      case TWO -> twoPlayers;
      case THREE -> threePlayers;
      case FOUR -> fourPlayers;
    };
  }
}
