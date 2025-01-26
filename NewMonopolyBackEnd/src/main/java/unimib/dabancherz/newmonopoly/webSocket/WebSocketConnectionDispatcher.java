package unimib.dabancherz.newmonopoly.webSocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.dabancherz.newmonopoly.handler.ChatHandler;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.manager.TurnManager;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionDispatcher implements WebSocketHandler {

    public final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final GameHandler gameHandler;
    private final ChatHandler chatHandler;
    private final TurnManager turnManager;
    private final WebSocketReconnect reconnect;
    private final PropertyHandler propertyHandler;
    private final MessageService messageService;
    ObjectMapper objectMapper = new ObjectMapper();

    // Iniezione di GameHandler e ChatHandler tramite il costruttore
    @Autowired
    public WebSocketConnectionDispatcher(GameHandler gameHandler, ChatHandler chatHandler, TurnManager turnManager,
                                         WebSocketReconnect reconnect, PropertyHandler propertyHandler, MessageService messageService) {
        this.gameHandler = gameHandler;
        this.chatHandler = chatHandler;
        this.turnManager = turnManager;
        this.reconnect = reconnect;
        this.propertyHandler = propertyHandler;
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nuova connessione stabilita. ID sessione: " + session.getId());
        playerSessions.put(session.getId(), session); // Aggiunge la sessione alla mappa
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        if(!message.getPayload().toString().contains("!") ) {
            String[] parts = (message.getPayload().toString()).split(":");
            switch (parts[0]) {
                case "Ping":
                    messageService.sendPongMessage(session);
                    break;
                case "LanciaDadi":
                    turnManager.spostaPedina(session);
                    break;
                case "FineTurno":
                    turnManager.endTurn(session);
                    break;
                case "InizioPartita":
                    turnManager.startTurn(session);
                    break;
                case "Create":
                    gameHandler.createGame(parts, session);
                    break;
                case "Partecipa":
                    gameHandler.joinGame(parts, session);
                    break;
                case "MessaggioUtente":
                    chatHandler.chatHandler(parts, session);
                    break;
                case "SceltaPedina":
                    gameHandler.choosePedina(parts, session);
                    break;
                case "AcquistaProprieta":
                    propertyHandler.acquistaProprieta(parts, session);
                    break;
                case "AcquistaProprietaPunti":
                    propertyHandler.acquistaProprietaPunti(parts, session);
                    break;
                case "PingScambiaProprieta":
                    propertyHandler.gestisciProprieta(session);
                    break;
                case "PagaUscitaPrigione":
                    turnManager.payPrisonExit(session);
                    break;
                case "RichiestaUpdateProperties":
                    propertyHandler.updateProperties(session);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo di messaggio non supportato: " + parts[0]);
            }
        } else {

            String payload = message.getPayload().toString();
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            String type = (String) data.get("type");

            switch (type) {
                case "!EffettuaScambio":
                    propertyHandler.effettuaScambio(data, session);
                    break;
                case "!RispostaScambio":
                    boolean flag = (boolean) data.get("exchangeAccepted");
                    propertyHandler.rispostaScambio(data, flag, session);
                    break;
                case "!CostruisciCasa":
                    PlayerProperties property = objectMapper.convertValue(data.get("property"), PlayerProperties.class);
                    Integer casine = (Integer) data.get("casine");
                    propertyHandler.gestisciCase(property, casine, session);
                    break;
                case "!IpotecaProprieta":
                    PlayerProperties propertyIpotecata = objectMapper.convertValue(data.get("property"), PlayerProperties.class);
                    propertyHandler.ipotecaProprieta(propertyIpotecata, session);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo di messaggio non supportato: " + type);
            }

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
            if (gameSession != null && !gameSession.isEmpty()) {
                messageService.notifyPlayerDisconnected(gameId, playerName, gameSessions);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
