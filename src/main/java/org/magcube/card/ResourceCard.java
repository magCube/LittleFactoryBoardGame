package org.magcube.card;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ResourceCard extends Card {

  private Card[][] cost;
  private Card[] capital;

  public ResourceCard() {
    super();
  }

  @Builder
  public ResourceCard(CardType cardType, int typeId, String name, int value, Card[][] cost,
      Card[] capital) {
    super(cardType, typeId, name, value);
    this.cost = cost;
    this.capital = capital;
  }
}
