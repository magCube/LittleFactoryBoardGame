package org.magcube;

import java.util.ArrayList;
import java.util.List;
import org.magcube.card.BuildingCard;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

public class Main {

  public static final List<ResourceCard> BASIC_RESOURCES = new ArrayList<>();
  public static final List<BuildingCard> factories = new ArrayList<>();

  static {
    for (var i = 0; i < 20; i++) {
      BASIC_RESOURCES.add(
          ResourceCard.builder().value(1).name("wood").typeId(i).cardType(CardType.BASIC_RESOURCE)
              .build()
      );
      factories.add(
          BuildingCard.builder().value(4).name("factory " + i).typeId(100 + i)
              .build()
      );
    }
  }

  public static void main(String[] args) {
    System.out.println("Hello world!");
  }
}