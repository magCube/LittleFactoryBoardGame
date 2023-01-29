package org.magcube;

import org.magcube.card.DisplayingPile;
import org.magcube.card.Factory;
import org.magcube.card.FirstTierResource;
import org.magcube.exception.GameStartupException;
import org.magcube.user.User;

import java.util.ArrayList;

public class Startup {
    private ArrayList<User> users;
    private DisplayingPile<FirstTierResource> firstTierResourcesPile;
    private DisplayingPile<Factory> factoriesPile;

    public Startup() {
        this.users = new ArrayList<>();
        this.firstTierResourcesPile = new DisplayingPile<>();
        this.factoriesPile = new DisplayingPile<>();
    }

    public ArrayList<User> getUsers() {
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
        if (users == null || users.isEmpty()) {
            throw new GameStartupException();
        }
    }
}
