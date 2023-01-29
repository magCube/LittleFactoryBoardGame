package org.magcube;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StartUpTest {

    @Test
    void setUsersTest() {
        var startup = new Startup();
        startup.setUsers(5);
        var users = startup.getUsers();
        assertEquals(5, users.size());
    }

    @Test
    void startGameNormally() {
        var startup = new Startup();
        startup.setUsers(6);
        assertDoesNotThrow(startup::startGame);
    }
}
