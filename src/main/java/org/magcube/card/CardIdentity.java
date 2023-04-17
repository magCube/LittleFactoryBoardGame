package org.magcube.card;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CardIdentity {

  private CardType cardType;
  private int typeId;

  public CardIdentity() {
  }

  public CardIdentity(CardType cardType, int typeId) {
    this.cardType = cardType;
    this.typeId = typeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardIdentity that = (CardIdentity) o;
    return typeId == that.typeId && cardType == that.cardType && cardType != null;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardType, typeId);
  }
}
