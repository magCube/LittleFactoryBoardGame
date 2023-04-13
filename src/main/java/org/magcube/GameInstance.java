package org.magcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.magcube.card.Card;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.player.Player;

public class GameInstance {

  @Getter
  private final GameBoard gameBoard;

  private List<Player> players;
  @Getter
  private Player currentPlayer; //only accessible by this object itself, need to be thread safe
  @Getter
  private List<Card> availableFactories;//need to be thread safe
  @Getter
  private boolean isTraded;

  public GameInstance() throws DisplayPileException {
    this.players = new ArrayList<>();
    this.gameBoard = new GameBoard();
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(int numberOfUsers) {
    var _users = new ArrayList<Player>();
    for (var i = 1; i <= numberOfUsers; i++) {
      var tempUser = new Player(String.valueOf(i), "User" + i);
      _users.add(tempUser);
    }
    this.players = _users;
  }

  public void startGame() throws GameStartupException {
    if (players == null || players.isEmpty() || !players.stream()
        .allMatch(player -> player.getResources().isEmpty() && player.getPoints() == 0)) {
      throw new GameStartupException();
    }
    Collections.shuffle(players);
    players = Collections.unmodifiableList(players);
    distributeCoin();
    prepareResourceCards();
    prepareFactories();
    currentPlayer = players.get(0);
    availableFactories = currentPlayer.getBuildings();
    isTraded = false;
//        REPEAT for Each user (in order)
//        currentuser = reference of the user in action
//        availabeFactories = new ArrayList(currentuser.getFactories());
//        isTraded = false
//        wait for actions = actions
//        SWITCH(actions)
//        case trade:
//        check isTraded == false
//        according to the target Card type , call the correct trade function
//        1. if trade FirstTier resources, call tradeFirstTierResource(payment, target);
//        2. if trade factory, call tradeFactory(payment, target);
//        isTraded = true
//        case manufacturing:
//        check the Factory's produces Card, produce accordingly
//        this.produceCard(card)
//        availableFactories.remove(factory)
//
//        case endTurn:
//        check if the current user have enough points to win, if
//        yes: break
//                no: continue
//                END REPEAT
//        display winner
  }

  public void tradeCard(ResourceCard payment, List<Card> targets) throws DisplayPileException {
    if (isTraded) {
      System.out.println("User already traded!");
      return;
    }
    if (!currentPlayer.ownCard(payment)) {
      System.out.println("User do not own the payment cards! Will not trade!");
      return;
    }
    if (payment.getValue() >= targets.stream().map(Card::getValue)
        .reduce(0, Integer::sum)) { //valid trade
      currentPlayer.discardCards(List.of(payment));
      gameBoard.takeCards(targets);
      isTraded = true;
      System.out.println(currentPlayer.getName() + " traded " + targets + " using " + payment);
    } else {
      System.out.println(currentPlayer.getName() + " traded " + targets + " using " + payment
          + "is not applicable");
    }
  }

  private void distributeCoin() {
    var coins = 3;
    for (var user : players) {
      user.setCoin(coins++);
    }
  }

  private void prepareResourceCards() {
    //TODO: initialize pile of first tier resources
  }

  private void prepareFactories() {
    //TODO: initialize pile of factories
  }

  public void endTurn() {
    currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    availableFactories = currentPlayer.getBuildings();
    isTraded = false;
  }
}
