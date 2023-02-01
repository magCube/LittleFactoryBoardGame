package org.magcube;

import java.util.ArrayList;
import java.util.List;
import org.magcube.card.Card;
import org.magcube.card.Factory;
import org.magcube.card.FirstTierResource;

public class Main {

  public static List<FirstTierResource> firstTierResources = new ArrayList<>();
  public static List<Factory> factories = new ArrayList<>();

  static {
    System.out.println("Static block called!");
    for (var i = 0; i < 20; i++) {
      firstTierResources.add(
          FirstTierResource.firstTierBuilder().value(1).cost(new Card[1]).name("wood").typeId(i)
              .build()
      );
      factories.add(
          Factory.factoryBuilder().value(4).cost(new Card[1]).name("factory " + i).typeId(100 + i)
              .build()
      );
    }
  }

  public static void main(String[] args) {
    System.out.println("Hello world!");
  }
}