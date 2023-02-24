package org.magcube.card.displayingpile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.magcube.card.Card;
import org.magcube.exception.DisplayPileException;

public class SingleRowDisplayingPile<T extends Card> extends DisplayingPile<T>{

  private final ArrayList<T> displaying;
  public SingleRowDisplayingPile(List<T> deck)
      throws DisplayPileException {
    super(deck);
    this.displaying = new ArrayList<>();
  }

  public List<T> getSingleRowDisplaying() {
    return Collections.unmodifiableList(this.displaying);
  }

  @Override
  public List<ArrayList<T>> getDisplaying() throws DisplayPileException {
    throw new DisplayPileException("This is a single row display pile, which means each column will only have one card, please use method getSingleRowDisplaying() instead");
  }
}
