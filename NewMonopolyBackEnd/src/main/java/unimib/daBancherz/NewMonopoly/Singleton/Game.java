package unimib.daBancherz.NewMonopoly.Singleton;

import java.util.HashMap;
import java.util.Map;

class Game {
    private String gameId;  // ID univoco per la partita
    private Map<String, Integer> playerPositions = new HashMap<>();  // Mappa che associa il nome del giocatore alla sua posizione sulla board

    // Costruttore della classe Game che accetta un gameId
    public Game(String gameId) {
        this.gameId = gameId;
    }

    // Metodo per aggiornare la posizione di un giocatore
    public void setPlayerPosition(String playerName, int position) {
        playerPositions.put(playerName, position);  // Inserisce o aggiorna la posizione del giocatore
    }

    // Metodo per ottenere le posizioni di tutti i giocatori nella partita
    public Map<String, Integer> getPlayerPositions() {
        return playerPositions;  // Restituisce la mappa delle posizioni dei giocatori
    }

    public void removePlayer(String playerName) {
        playerPositions.remove(playerName);
    }

    public boolean isEmpty() {
        return playerPositions.isEmpty();
    }
}

