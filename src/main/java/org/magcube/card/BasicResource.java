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

  @Builder
  public BasicResource(int value, String name, Card[] cost, Card[] capital, int typeId) {
    super(value, name, cost, capital, typeId);
  }

}
