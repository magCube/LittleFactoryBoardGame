package org.magcube.card;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BuildingCard extends Card {

  private final Card[][] cost;
  private final int points;
  private final Card[][] effectCost;
  private final Card[][] effectCapital;
  private final Card effectProduct;
  private final boolean isStartingBuilding;
  private final boolean isExtension;
  // todo
  private final String specialEffect = "";

  @Builder
  public BuildingCard(CardType cardType, int typeId, String name, int value, Card[][] cost,
      int points,
      Card[][] effectCost, Card[][] effectCapital, Card effectProduct, boolean isStartingBuilding,
      boolean isExtension) {
    super(cardType, typeId, name, value);
    this.cost = cost;
    this.points = points;
    this.effectCost = effectCost;
    this.effectCapital = effectCapital;
    this.effectProduct = effectProduct;
    this.isStartingBuilding = isStartingBuilding;
    this.isExtension = isExtension;
  }
}
