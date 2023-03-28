package org.magcube;

import java.util.ArrayList;
import java.util.List;
import org.magcube.card.Card;
import org.magcube.card.Building;
import org.magcube.card.BasicResource;

public class Main {

  public static final List<BasicResource> BASIC_RESOURCES = new ArrayList<>();
  public static final List<Building> factories = new ArrayList<>();

  static {
    for (var i = 0; i < 20; i++) {
      BASIC_RESOURCES.add(
          BasicResource.builder().value(1).cost(new Card[1]).name("wood").typeId(i)
              .build()
      );
      factories.add(
          Building.builder().value(4).cost(new Card[1]).name("factory " + i).typeId(100 + i)
              .build()
      );
    }
  }

  public static void main(String[] args) {
    System.out.println("Hello world!");
  }
}