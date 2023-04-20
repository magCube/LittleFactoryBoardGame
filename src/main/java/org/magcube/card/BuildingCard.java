package org.magcube.card;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class BuildingCard extends Card {

  private CardIdentity[][] cost;
  private int points;
  private CardIdentity[][] effectCost;
  private CardIdentity[] effectCapital;
  private CardIdentity effectProduct;
  private int effectPoints;
  private Boolean isStartingBuilding;
  private Boolean isExtension;
  // todo
  private String specialEffect;

  public BuildingCard() {
  }

  @Builder
  public BuildingCard(CardIdentity cardIdentity, String name, int value, CardIdentity[][] cost, int points, CardIdentity[][] effectCost,
      CardIdentity[] effectCapital, CardIdentity effectProduct, int effectPoints, Boolean isStartingBuilding, Boolean isExtension,
      String specialEffect) {
    super(cardIdentity, name, value);
    this.cost = cost;
    this.points = points;
    this.effectCost = effectCost;
    this.effectCapital = effectCapital;
    this.effectProduct = effectProduct;
    this.effectPoints = effectPoints;
    this.isStartingBuilding = isStartingBuilding;
    this.isExtension = isExtension;
    this.specialEffect = specialEffect;
  }
}
