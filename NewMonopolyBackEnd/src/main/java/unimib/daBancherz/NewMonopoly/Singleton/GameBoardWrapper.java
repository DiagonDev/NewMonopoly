package unimib.daBancherz.NewMonopoly.Singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unimib.daBancherz.NewMonopoly.model.Game;

import java.util.Map;

@Component
public class GameBoardWrapper {

    GameBoardSingleton gameBoardSingleton = GameBoardSingleton.getInstance();

    public void createGame(String gameId) {
        gameBoardSingleton.createGame(gameId);
    }

    public Game getGame(String gameId) {
        return gameBoardSingleton.getGame(gameId);
    }

    public void setPlayerPosition(String gameId, String playerName, int position) {
        gameBoardSingleton.setPlayerPosition(gameId, playerName, position);
    }

    public Integer getPlayerPosition(String gameId, String playerName) {
        return gameBoardSingleton.getPlayerPosition(gameId, playerName);
    }

    public Map<String, Integer> getPlayerPositions(String gameId) {
        return gameBoardSingleton.getPlayerPositions(gameId);
    }

    public void removeGame(String gameId) {
        gameBoardSingleton.removeGame(gameId);
    }

    public void removePlayerFromGame(String gameId, String playerName) {
        gameBoardSingleton.removePlayerFromGame(gameId, playerName);
    }

    public boolean isPlayerInPrison(String gameId, String playerName) {
        return gameBoardSingleton.isPlayerInPrison(gameId, playerName);
    }

    public void setPlayerPrison(String gameId, String playerName, boolean isInPrison) {
        gameBoardSingleton.setPlayerPrison(gameId, playerName, isInPrison);
    }

    // Puoi aggiungere altri metodi delegati in base alle necessità
}
