package org.magcube.card;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.magcube.enums.CardType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class Card {

  protected CardIdentity cardIdentity;
  protected String name;
  protected int value;

  // don't use getCardType as name as Jackson will parse it as a field
  public CardType cardType() {
    return cardIdentity.cardType();
  }

  // don't use getTypeId as name as Jackson will parse it as a field
  public int typeId() {
    return cardIdentity.typeId();
  }

  public boolean isIdentical(CardIdentity cardIdentity) {
    return this.cardIdentity.equals(cardIdentity);
  }

  public boolean isIdentical(Card card) {
    return cardIdentity.equals(card.cardIdentity);
  }
}
