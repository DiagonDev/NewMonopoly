package unimib.daBancherz.NewMonopoly.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.model.Game;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game("game1");
    }

    @Test
    void testSetAndGetPlayerPosition() {
        game.setPlayerPosition("Alice", 5);
        assertEquals(5, game.getPlayerPositions().get("Alice"));
    }

    @Test
    void testRemovePlayer() {
        game.setPlayerPosition("Bob", 3);
        game.removePlayer("Bob");
        assertFalse(game.getPlayerPositions().containsKey("Bob"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(game.isEmpty());
        game.setPlayerPosition("Charlie", 7);
        assertFalse(game.isEmpty());
    }

    @Test
    void testSetAndGetPlayerPrison() {
        game.setPlayerPrison("David", true);
        assertTrue(game.isPlayerInPrison("David"));
        game.setPlayerPrison("David", false);
        assertFalse(game.isPlayerInPrison("David"));
    }

    @Test
    void testSetAndGetPlayerCountRoll() {
        game.setPlayerCountRoll("Eve", 2);
        assertEquals(2, game.getPlayerCountRoll("Eve"));
    }

    @Test
    void testSetAndGetPlayerCountRollDoubleDice() {
        game.setPlayerCountRollDoubleDice("Frank", 3);
        assertEquals(3, game.getPlayerCountRollDoubleDice("Frank"));
    }
}
