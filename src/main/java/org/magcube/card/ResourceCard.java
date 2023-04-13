package org.magcube.card;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResourceCard extends Card {

  private final Card[][] cost;
  private final Card[][] capital;

  @Builder
  public ResourceCard(CardType cardType, int typeId, String name, int value, Card[][] cost,
      Card[][] capital) {
    super(cardType, typeId, name, value);
    this.cost = cost;
    this.capital = capital;
  }
}
