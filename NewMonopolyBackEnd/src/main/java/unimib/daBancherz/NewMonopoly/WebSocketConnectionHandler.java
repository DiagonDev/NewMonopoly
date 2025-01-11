package unimib.daBancherz.NewMonopoly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    @Autowired
    private GameHandler gameHandler = new GameHandler();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nuova connessione stabilita. ID sessione: " + session.getId());
        playerSessions.put(session.getId(), session); // Aggiunge la sessione alla mappa
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Non gestisce i messaggi qui, solo connessioni
        //System.out.println("Messaggio ricevuto nella gestione connessioni: " + message.getPayload());
        String payload = message.getPayload().toString();
        if(payload.startsWith("Partecipa:") || payload.startsWith("Create:")) {
            gameHandler.handleGameMessage(payload, session);
        }
        else if(payload.startsWith("MessaggioUtente:")) {
            gameHandler.chatHandler(payload, session);
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
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public WebSocketSession getSession(String sessionId) {
        return playerSessions.get(sessionId);
    }
}
