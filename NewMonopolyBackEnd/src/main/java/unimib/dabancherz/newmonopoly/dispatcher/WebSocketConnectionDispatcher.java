package unimib.dabancherz.newmonopoly.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.GameHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionDispatcher implements WebSocketHandler {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final MessageDispatcher messageDispatcher;
    private final SpecialMessageDispatcher specialMessageDispatcher;
    private final GameHandler gameHandler;
    private final MessageService messageService;

    @Autowired
    public WebSocketConnectionDispatcher(MessageDispatcher messageDispatcher, SpecialMessageDispatcher specialMessageDispatcher, GameHandler gameHandler, MessageService messageService) {
        this.messageDispatcher = messageDispatcher;
        this.specialMessageDispatcher = specialMessageDispatcher;
        this.gameHandler = gameHandler;
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nuova connessione stabilita. ID sessione: " + session.getId());
        playerSessions.put(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        if (!payload.contains("!")) {
            messageDispatcher.dispatchMessage(session, payload);
        } else {
            specialMessageDispatcher.dispatchSpecialMessage(session, payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Errore nella connessione. ID sessione: " + session.getId());
        playerSessions.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connessione chiusa. ID sessione: " + session.getId());
        playerSessions.remove(session.getId());

        // Determina il nome del giocatore e il gameId associato alla sessione chiusa
        String playerName = gameHandler.getPlayerNameBySession(session);
        String gameId = gameHandler.getGameIdBySession(session);
        if (gameId != null) {
            // Ottieni la lista di sessioni associate al gioco
            List<WebSocketSession> gameSession = gameHandler.getGameSessions().get(gameId);
            Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
            // Rimuove il giocatore dalla partita
            gameHandler.removePlayerFromGame(gameId, session);
            if (gameSession != null && !gameSession.isEmpty())
                messageService.notifyPlayerDisconnected(gameId, playerName, gameSessions);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}