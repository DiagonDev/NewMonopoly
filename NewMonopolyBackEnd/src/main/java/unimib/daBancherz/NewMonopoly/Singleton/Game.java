package unimib.daBancherz.NewMonopoly.Singleton;

import java.util.HashMap;
import java.util.Map;

class Game {
    private String gameId;  // ID univoco per la partita
    private Map<String, Player> players = new HashMap<>();  // Mappa che associa il nome del giocatore all'oggetto Player

    // Costruttore della classe Game che accetta un gameId
    public Game(String gameId) {
        this.gameId = gameId;
    }

    // Metodo per aggiornare la posizione di un giocatore
    public void setPlayerPosition(String playerName, int position) {
        players.computeIfAbsent(playerName, k -> new Player()).setPosition(position);
    }

    // Metodo per ottenere le posizioni di tutti i giocatori nella partita
    public Map<String, Integer> getPlayerPositions() {
        Map<String, Integer> positions = new HashMap<>();
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            positions.put(entry.getKey(), entry.getValue().getPosition());
        }
        return positions;
    }

    public void removePlayer(String playerName) {
        players.remove(playerName);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    // Metodo per impostare lo stato di prigione di un giocatore
    public void setPlayerPrison(String playerName, boolean isInPrison) {
        players.computeIfAbsent(playerName, k -> new Player()).setPrison(isInPrison);
    }

    // Metodo per ottenere lo stato di prigione di un giocatore
    public boolean isPlayerInPrison(String playerName) {
        return players.getOrDefault(playerName, new Player()).isInPrison();
    }
}

class Player {
    private int position = 0;
    private boolean prison = false;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isInPrison() {
        return prison;
    }

    public void setPrison(boolean prison) {
        this.prison = prison;
    }
}