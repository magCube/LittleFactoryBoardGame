package org.magcube.card;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BuildingCard extends Card {

  private Card[][] cost;
  private int points;
  private Card[][] effectCost;
  private Card[] effectCapital;
  private Card effectProduct;
  private int effectPoints;
  private Boolean isStartingBuilding;
  private Boolean isExtension;
  // todo
  private String specialEffect;

  public BuildingCard() {
  }

  @Builder
  public BuildingCard(CardType cardType, int typeId, String name, int value, Card[][] cost,
      int points, Card[][] effectCost, Card[] effectCapital, Card effectProduct, int effectPoints,
      Boolean isStartingBuilding, Boolean isExtension) {
    super(cardType, typeId, name, value);
    this.cost = cost;
    this.points = points;
    this.effectCost = effectCost;
    this.effectCapital = effectCapital;
    this.effectProduct = effectProduct;
    this.effectPoints = effectPoints;
    this.isStartingBuilding = isStartingBuilding;
    this.isExtension = isExtension;
  }
}
