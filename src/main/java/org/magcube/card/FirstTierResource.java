package org.magcube.card;

import lombok.Builder;

/*
  5 types of first tier resources:
   1. wood
   2. stone
   3. cotton
   4. seed
   5. sand
 */
public class FirstTierResource extends Card {

  @Builder(builderMethodName = "firstTierBuilder")
  public FirstTierResource(int value, String name, Card[] cost, int typeId) {
    super(value, name, cost, typeId);
  }

  public enum FirstTierResourceType {
    WOOD, STONE, COTTON, SEED, SAND
  }
}
