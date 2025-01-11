package unimib.daBancherz.NewMonopoly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.daBancherz.NewMonopoly.Handler.ChatHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final GameHandler gameHandler;
    private final ChatHandler chatHandler;

    // Iniezione di GameHandler e ChatHandler tramite il costruttore
    @Autowired
    public WebSocketConnectionHandler(GameHandler gameHandler, ChatHandler chatHandler) {
        this.gameHandler = gameHandler;
        this.chatHandler = chatHandler;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nuova connessione stabilita. ID sessione: " + session.getId());
        playerSessions.put(session.getId(), session); // Aggiunge la sessione alla mappa
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String[] parts = (message.getPayload().toString()).split(":");
        switch (parts[0]) {
            case "Create", "Partecipa":
                gameHandler.handleGameMessage(parts, session);
                break;
            case "MessaggioUtente":
                chatHandler.chatHandler(parts, session);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + parts[0]);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Errore nella connessione. ID sessione: " + session.getId());
        playerSessions.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Connessione chiusa. ID sessione: " + session.getId());
        playerSessions.remove(session.getId());

        // Determina il nome del giocatore e il gameId associato alla sessione chiusa
        String playerName = gameHandler.getPlayerNameBySession(session);
        String gameId = gameHandler.getGameIdBySession(session);

        if (gameId != null) {
            // Rimuove il giocatore dalla partita
            gameHandler.removePlayerFromGame(gameId, session);

            // Notifica agli altri giocatori della partita
            try {
                gameHandler.notifyPlayerDisconnected(gameId, playerName);
            } catch (Exception e) {
                System.err.println("Errore durante la notifica della disconnessione del giocatore: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
