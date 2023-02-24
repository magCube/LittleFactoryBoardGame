package org.magcube;

import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.magcube.card.Card;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.card.FirstTierResource;
import org.magcube.exception.DisplayPileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

  public static Stream<DisplayingPile<FirstTierResource>> provideFirstTierResourcesPiles()
      throws DisplayPileException {
    return Stream.of(
        getFirstTierResourcesPile(),
        getTestingFirstTierResourcesPile()
    );
  }

  public static DisplayingPile<FirstTierResource> getFirstTierResourcesPile()
      throws DisplayPileException {
    return new DisplayingPile<>(Main.firstTierResources);
  }

  public static DisplayingPile<FirstTierResource> getTestingFirstTierResourcesPile()
      throws DisplayPileException {
    var firstTierResources = new ArrayList<FirstTierResource>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          FirstTierResource.firstTierBuilder().value(1).cost(new Card[1]).name(String.valueOf(i))
              .typeId(i % 5)
              .build()
      );
    }
    return new DisplayingPile<>(firstTierResources);
  }
}
