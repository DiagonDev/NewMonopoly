package unimib.daBancherz.NewMonopoly.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.daBancherz.NewMonopoly.Handler.ChatHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.TurnHandler;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final GameHandler gameHandler;
    private final ChatHandler chatHandler;
    private final TurnHandler turnHandler;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    // Iniezione di GameHandler e ChatHandler tramite il costruttore
    @Autowired
    public WebSocketConnectionHandler(GameHandler gameHandler, ChatHandler chatHandler, TurnHandler turnHandler) {
        this.gameHandler = gameHandler;
        this.chatHandler = chatHandler;
        this.turnHandler = turnHandler;
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
            case "Ping":
                String pongMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "pong",
                        "content", "pong"

                ));
                session.sendMessage(new TextMessage(pongMessage));
                break;
            case "LanciaDadi":
                turnHandler.rollDice(session);
                break;
            case "FineTurno":
                turnHandler.endTurn(session);
                break;
            case "InizioPartita": //TODO: controllare che il messaggio che mi arriva dal frontend sia uguale
                turnHandler.startTurn(session);
                break;
            case "Create", "Partecipa":
                gameHandler.handleGameMessage(parts, session);
                break;
            case "MessaggioUtente":
                chatHandler.chatHandler(parts, session);
                break;
            case "SceltaPedina":
                gameHandler.choosePedina(parts, session);
                break;
            case "AcquistaProprieta":
                //gestire il salvataggio della proprietà acquistata dal giocatore nel database
                break;
            case  "IpotecaProprieta":
                //gestire l'ipoteca della proprietà
                break;
            case "ScambiaProprieta":
                //gestire lo scambio della proprietà
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
