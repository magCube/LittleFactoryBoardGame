package org.magcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.magcube.card.DisplayingPile;
import org.magcube.card.Factory;
import org.magcube.card.FirstTierResource;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.user.User;

public class Startup {

  private final DisplayingPile<FirstTierResource> firstTierResourcesPile;
  private final DisplayingPile<Factory> factoriesPile;
  private List<User> users;

  public Startup() throws DisplayPileException {
    this.users = new ArrayList<>();
    this.firstTierResourcesPile = new DisplayingPile<>(Main.firstTierResources);
    this.factoriesPile = new DisplayingPile<>(Main.factories);
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(int numberOfUsers) {
    var _users = new ArrayList<User>();
    for (var i = 1; i <= numberOfUsers; i++) {
      var tempUser = new User();
      tempUser.setName("User" + i);
      _users.add(tempUser);
    }
    this.users = _users;
  }

  public void startGame() throws GameStartupException {
    if (users == null || users.isEmpty() || !users.stream()
        .allMatch(user -> user.getCards().isEmpty() && user.getPoints() == 0)) {
      throw new GameStartupException();
    }
    Collections.shuffle(users);
    users = Collections.unmodifiableList(users);
    distributeCoin();
    prepareFirstTierResources();
    prepareFactories();

//        REPEAT for Each user (in order)
//        currentuser = reference of the user in action
//                availabeFactories = new ArrayList(currentuser.getFactories());
//        isTraded = false
//        wait for actions = actions
//        SWITCH(actions)
//        case trade:
//        check isTraded == false
//        according to the target Card type , call the correct trade function
//        1. if trade FirstTier resources, call tradeFirstTierResource(payment, target);
//        2. if trade factory, call tradeFactory(payment, target);
//        isTraded = true
//        case manufactoring:
//        check the Factory's produces Card, produce accordingly
//        this.produceCard(card)
//        availabeFactories.remove(factory)
//
//        case endTurn:
//        check if the current user have enough points to win, if
//        yes: break
//                no: continue
//                END REPEAT
//        display winner
  }

  private void distributeCoin() {
    var coins = 3;
    for (var user : users) {
      user.giveCoin(coins++);
    }
  }

  private void prepareFirstTierResources() {
    //TODO: initialize pile of first tier resources
  }

  private void prepareFactories() {
    //TODO: initialize pile of factories
  }

  public List<ArrayList<FirstTierResource>> getDisplayingFirstTierResources() {
    return firstTierResourcesPile.getDisplaying();
  }

  public List<ArrayList<Factory>> getDisplayingFactories() {
    return factoriesPile.getDisplaying();
  }
}
