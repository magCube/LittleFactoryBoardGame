package org.magcube.gameboard;

import java.util.List;
import org.magcube.card.Card;
import org.magcube.card.CardType;

public record PileState<T extends Card>(CardType cardType,
                                        List<List<T>> displaying,
                                        List<T> deck,
                                        List<T> discardPile) {

}
