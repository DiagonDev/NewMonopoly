package unimib.daBancherz.NewMonopoly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game("game123");
    }

    @Test
    void testSetPlayerPosition() {
        game.setPlayerPosition("player1", 5);
        assertEquals(5, game.getPlayerPositions().get("player1"));
    }

    @Test
    void testGetPlayerPositions() {
        game.setPlayerPosition("player1", 5);
        game.setPlayerPosition("player2", 10);
        Map<String, Integer> positions = game.getPlayerPositions();
        assertEquals(2, positions.size());
        assertEquals(5, positions.get("player1"));
        assertEquals(10, positions.get("player2"));
    }

    @Test
    void testRemovePlayer() {
        game.setPlayerPosition("player1", 5);
        game.removePlayer("player1");
        assertFalse(game.getPlayerPositions().containsKey("player1"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(game.isEmpty());
        game.setPlayerPosition("player1", 5);
        assertFalse(game.isEmpty());
    }

    @Test
    void testSetPlayerPrison() {
        game.setPlayerPrison("player1", true);
        assertTrue(game.isPlayerInPrison("player1"));
    }

    @Test
    void testIsPlayerInPrison() {
        game.setPlayerPrison("player1", true);
        assertTrue(game.isPlayerInPrison("player1"));
        game.setPlayerPrison("player1", false);
        assertFalse(game.isPlayerInPrison("player1"));
    }

    @Test
    void testSetPlayerCountRoll() {
        game.setPlayerCountRoll("player1", 3);
        assertEquals(3, game.getPlayerCountRoll("player1"));
    }

    @Test
    void testGetPlayerCountRoll() {
        game.setPlayerCountRoll("player1", 3);
        assertEquals(3, game.getPlayerCountRoll("player1"));
    }

    @Test
    void testSetPlayerCountRollDoubleDice() {
        game.setPlayerCountRollDoubleDice("player1", 2);
        assertEquals(2, game.getPlayerCountRollDoubleDice("player1"));
    }

    @Test
    void testGetPlayerCountRollDoubleDice() {
        game.setPlayerCountRollDoubleDice("player1", 2);
        assertEquals(2, game.getPlayerCountRollDoubleDice("player1"));
    }
}