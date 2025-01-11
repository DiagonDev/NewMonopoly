package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameHandler {

    @Autowired
    private MessageHandler messageHandler;  // Iniezione del bean MessageSender
    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> playerNameList = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong();

    public void handleGameMessage(String[] messageParts, WebSocketSession session) throws Exception {
        if (messageParts[0].equals("Create")) {
            // Decodifica i dati per creare una partita

            String playerName = messageParts[1];
            String difficulty = messageParts[2];
            String randomization = messageParts[3];

            playerNameList.put(playerName, session);
            createGame(playerName, difficulty, randomization, session);

        } else if (messageParts[0].equals("Partecipa")) {
            // Decodifica i dati per partecipare a una partita
            String playerName = messageParts[1];
            String gameId = messageParts[2];

            playerNameList.put(playerName, session);
            joinGame(playerName, gameId, session);
        }
    }

    private void createGame(String playerName, String difficulty, String randomization, WebSocketSession session) throws Exception {

        String gameId = generateGameId();

        gameSessions.putIfAbsent(gameId, new ArrayList<>());
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (!playersInGame.contains(session)) {
            playersInGame.add(0, session); // Aggiungi la sessione all'inizio della lista
        }
        /*
        Ale - ho bisogno che il join venga mandato a tutti, non solo all'admin
        perchè tutti devono aggiornare la propria views quando entra qualcuno
        allo stesso modo ho bisogno che quando l'admin crea il game allo stesso tempo lo joini
         */
        messageHandler.sendJoinMessage(playerName, session);

        messageHandler.sendSystemMessage(gameId, "#" + gameId, gameSessions, session); //serve per inviare i messaggi da mostrare nella gameconsole
        messageHandler.sendGameId(gameId, session);//serve per mostrare all'admin il gameId da passare agli altri giocatori per connettersi
        messageHandler.sendTypePlayer("ADMIN", session);
    }


    private void joinGame(String playerName, String gameId, WebSocketSession session) throws Exception {
        if (!gameSessions.containsKey(gameId)) {
            session.sendMessage(new TextMessage("Errore: La partita con ID " + gameId + " non esiste."));
            return;
        }

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        playersInGame.add(session);

        messageHandler.notifyPlayerJoin(gameId, playerName, session, playersInGame, gameSessions);
    }

    public String getGameIdBySession(WebSocketSession session) {
        // Scorre tutte le partite nella mappa
        for (Map.Entry<String, List<WebSocketSession>> entry : gameSessions.entrySet()) {
            String gameId = entry.getKey();  // gameId
            List<WebSocketSession> playersInGame = entry.getValue();  // Lista di sessioni

            // Scorre la lista delle sessioni associate al gameId
            for (WebSocketSession playerSession : playersInGame) {
                // Se la sessione corrisponde a quella passata, restituisci il gameId
                if (playerSession.equals(session)) {
                    return gameId;
                }
            }
        }

        // Se la sessione non è trovata in nessuna partita, ritorna null
        return null;
    }

    public String getPlayerNameBySession(WebSocketSession session) {
        // Scorre la mappa playerNameList per trovare la sessione corrispondente
        for (Map.Entry<String, WebSocketSession> entry : playerNameList.entrySet()) {
            String playerName = entry.getKey();  // Nome del giocatore
            WebSocketSession playerSession = entry.getValue();  // Sessione del giocatore

            // Se la sessione corrisponde a quella passata, restituisci il nome del giocatore
            if (playerSession.equals(session)) {
                return playerName;
            }
        }

        // Se la sessione non è trovata, restituisci null
        return null;
    }

    public Map<String, List<WebSocketSession>> getGameSessions() {
        return gameSessions;
    }

    //genera il codice gameID in modo incrementale partendo da game-0
    private String generateGameId() {
        return "game-" + String.valueOf(idCounter.getAndIncrement());
    }

    public void removePlayerFromGame(String gameId, WebSocketSession session) {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (playersInGame != null) {
            playersInGame.remove(session); // Rimuove la sessione dalla lista dei giocatori

            // Se non ci sono più giocatori nella partita, rimuovi completamente la partita
            if (playersInGame.isEmpty()) {
                gameSessions.remove(gameId);
                System.out.println("Partita con ID " + gameId + " rimossa poiché non ci sono più giocatori.");
            }
        }

        // Rimuove il giocatore dalla mappa dei nomi
        playerNameList.values().removeIf(existingSession -> existingSession.equals(session));
    }

    public void notifyPlayerDisconnected(String gameId, String playerName) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (playersInGame == null) return;

        // Crea un messaggio di notifica come JSON
        String disconnectMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "system",
                "content", playerName + " si è disconnesso dalla partita."
        ));

        // Invia il messaggio a tutti i giocatori rimanenti nella partita
        for (WebSocketSession session : playersInGame) {
            session.sendMessage(new TextMessage(disconnectMessage));
        }

        System.out.println("Giocatore " + playerName + " disconnesso dalla partita con ID " + gameId + ".");
    }
}
