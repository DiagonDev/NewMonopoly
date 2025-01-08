package unimib.daBancherz.NewMonopoly;

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

    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> playerNameList = new ConcurrentHashMap<>();
    private static AtomicLong idCounter = new AtomicLong();


    public void handleGameMessage(String payload, WebSocketSession session) throws Exception {
        if (payload.startsWith("Create:")) {
            // Decodifica i dati per creare una partita
            String[] parts = payload.split(":");
            String playerName = parts[1];
            String difficulty = parts[2];
            String randomization = parts[3];

            playerNameList.put(playerName, session);
            createGame(playerName, difficulty, randomization, session);

        } else if (payload.startsWith("Partecipa:")) {
            // Decodifica i dati per partecipare a una partita
            String[] parts = payload.split(":");
            String playerName = parts[1];
            String gameId = parts[2];

            playerNameList.put(playerName, session);
            joinGame(playerName, gameId, session);
        }
    }

    private void createGame(String playerName, String difficulty, String randomization, WebSocketSession session) throws Exception {
        String gameId = generateGameId();
        System.out.println("Partita creata. ID: " + gameId);

        gameSessions.putIfAbsent(gameId, new ArrayList<>());
        gameSessions.get(gameId).add(session);

        session.sendMessage(new TextMessage(gameId));
    }

    private void joinGame(String playerName, String gameId, WebSocketSession session) throws Exception {
        if (!gameSessions.containsKey(gameId)) {
            session.sendMessage(new TextMessage("Errore: La partita con ID " + gameId + " non esiste."));
            return;
        }

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        playersInGame.add(session);

        session.sendMessage(new TextMessage("Ti sei unito alla partita con ID " + gameId + " con successo!"));
        broadcastPlayerJoined(playerName, playersInGame);

        // Stampa tutti i partecipanti della partita
        System.out.println("Partecipanti della partita con ID " + gameId + ":");
        for (WebSocketSession playerSession : playersInGame) {
            String sessionId = playerSession.getId(); // Per ora stampiamo l'ID della sessione
            System.out.println(" - ID sessione: " + sessionId);
        }

    }

    public void chatHandler (String payload, WebSocketSession session) throws Exception {
        String[] parts = payload.split(":");
        String chatMessage = parts[1];
        String gameId = getGameIdBySession(session);

        broadcastChatMessage(gameId, session, chatMessage);
    }

    private String getGameIdBySession(WebSocketSession session) {
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

    private String getPlayerNameBySession(WebSocketSession session) {
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


    private void broadcastChatMessage(String gameId, WebSocketSession sender, String message) throws Exception {


        if (!gameSessions.containsKey(gameId)) {
            sender.sendMessage(new TextMessage("Errore: La partita con ID " + gameId + " non esiste."));
            return;
        }

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        String nameChat = getPlayerNameBySession(sender);

        for (WebSocketSession session : playersInGame) {
            if (session.isOpen()) {

                //TODO: mettere il metodo per rendere il nome
                session.sendMessage(new TextMessage("!" + nameChat + ": " + message));
            }
        }
    }

    private void broadcastPlayerJoined(String playerName, List<WebSocketSession> playersInGame) throws Exception {
        for (WebSocketSession session : playersInGame) {
            session.sendMessage(new TextMessage(playerName + " si è unito alla partita!"));
        }
    }

    private String generateGameId() {
        return "game-" + String.valueOf(idCounter.getAndIncrement());
    }

    private String extractPlayerName(WebSocketSession session) {
        // Supponiamo che il nome del giocatore sia salvato negli attributi della sessione
        //TODO: da implementare quando il database avrà salvato i nomi dei giocatori
        return (String) session.getAttributes().get("playerName");
    }

}
