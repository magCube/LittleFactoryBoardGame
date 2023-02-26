package org.magcube.card;

import lombok.Builder;

/*
  5 types of first tier resources:
   1. wood
   2. stone
   3. cotton
   4. wheat
   5. mug
 */
public class BasicResource extends Card {

  @Builder(builderMethodName = "BasicResourceBuilder")
  public BasicResource(int value, String name, Card[] cost, int typeId) {
    super(value, name, cost, typeId);
  }

  public enum BasicResourceType {
    WOOD, STONE, COTTON, WHEAT, MUG
  }
}
