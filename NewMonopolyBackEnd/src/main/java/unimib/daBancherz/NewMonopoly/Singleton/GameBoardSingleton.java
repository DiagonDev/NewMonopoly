package unimib.daBancherz.NewMonopoly.Singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameBoardSingleton {

    // Variabile statica per memorizzare l'istanza del Singleton
    private static GameBoardSingleton instance;

    // Mappa per memorizzare tutte le partite. La chiave è l'ID della partita, il valore è l'oggetto Game che rappresenta la partita
    private Map<String, Game> games = new HashMap<>();

    // Costruttore privato per impedire la creazione di nuove istanze (tipico pattern Singleton)
    private GameBoardSingleton() {
    }

    // Metodo per ottenere l'istanza del Singleton. È sincronizzato per garantire che l'istanza sia creata una sola volta
    public static synchronized GameBoardSingleton getInstance() {
        if (instance == null) {
            instance = new GameBoardSingleton();
        }
        return instance;
    }

    // Metodo per creare una nuova partita (aggiunge un oggetto Game alla mappa)
    public void createGame(String gameId) {
        games.put(gameId, new Game(gameId));  // Crea una nuova partita con l'ID specificato
    }

    // Metodo per ottenere una partita per ID
    public Game getGame(String gameId) {
        return games.get(gameId);  // Restituisce l'oggetto Game associato all'ID della partita
    }

    // Metodo per aggiornare la posizione di un giocatore in una specifica partita
    public void setPlayerPosition(String gameId, String playerName, int position) {
        Game game = games.get(gameId);
        if (game != null) {
            game.setPlayerPosition(playerName, position);  // Modifica la posizione del giocatore nella partita
        }
    }

    // Metodo per ottenere le posizioni del giocatore in una partita
    public Integer getPlayerPosition(String gameId, String playerName) {
        Game game = games.get(gameId);

        // Restituisce la posizione del giocatore specifico o null se il gioco o il giocatore non esiste
        return (game != null) ? game.getPlayerPositions().get(playerName) : null;
    }

}

