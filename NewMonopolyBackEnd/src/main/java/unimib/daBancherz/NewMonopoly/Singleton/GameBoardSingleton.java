package unimib.daBancherz.NewMonopoly.Singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameBoardSingleton {
    private static GameBoardSingleton instance;
    private Map<String, Game> games = new HashMap<>();

    private GameBoardSingleton() {}

    public static synchronized GameBoardSingleton getInstance() {
        if (instance == null) {
            instance = new GameBoardSingleton();
        }
        return instance;
    }

    public void createGame(String gameId) {
        games.put(gameId, new Game(gameId));
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public void setPlayerPosition(String gameId, String playerName, int position) {
        Game game = games.get(gameId);
        if (game != null) {
            game.setPlayerPosition(playerName, position);
        }
    }

    public Integer getPlayerPosition(String gameId, String playerName) {
        Game game = games.get(gameId);
        return (game != null) ? game.getPlayerPositions().get(playerName) : null;
    }

    public Map<String, Integer> getPlayerPositions(String gameId) {
        Game game = games.get(gameId);
        return game != null ? game.getPlayerPositions() : Collections.emptyMap();
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    public void removePlayerFromGame(String gameId, String playerName) {
        Game game = games.get(gameId);
        if (game != null) {
            game.removePlayer(playerName);
        }
    }

    public void removeGameIfEmpty(String gameId) {
        Game game = games.get(gameId);
        if (game != null && game.isEmpty()) {
            games.remove(gameId);
        }
    }

    public void setPlayerPrison(String gameId, String playerName, boolean isInPrison) {
        Game game = games.get(gameId);
        if (game != null) {
            game.setPlayerPrison(playerName, isInPrison);
        }
    }

    public boolean isPlayerInPrison(String gameId, String playerName) {
        Game game = games.get(gameId);
        return game != null && game.isPlayerInPrison(playerName);
    }

    public void setPlayerCountRoll(String gameId, String playerName, int countRoll) {
        Game game = games.get(gameId);
        if (game != null) {
            game.setPlayerCountRoll(playerName, countRoll);
        }
    }

    public int getPlayerCountRoll(String gameId, String playerName) {
        Game game = games.get(gameId);
        return game != null ? game.getPlayerCountRoll(playerName) : 0;
    }

    public void setPlayerCountRollDoubleDice(String gameId, String playerName, int countRollDoubleDice) {
        Game game = games.get(gameId);
        if(game != null) {
            game.setPlayerCountRollDoubleDice(playerName,countRollDoubleDice);
        }
    }

    public int getPlayerCountRollDoubleDice(String gameId, String playerName) {
        Game game = games.get(gameId);
        return game != null ? game.getPlayerCountRollDoubleDice(playerName) : 0;
    }
}
