package org.magcube.gameboard;

import org.magcube.card.BuildingCard;
import org.magcube.card.ResourceCard;

public record GameBoardState(PileState<ResourceCard> basicResource,
                             PileState<ResourceCard> levelOneResource,
                             PileState<ResourceCard> levelTwoResource,
                             PileState<BuildingCard> building) {

}
