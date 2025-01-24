package unimib.daBancherz.NewMonopoly.WebSocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.daBancherz.NewMonopoly.Handler.ChatHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.PropertyHandler;
import unimib.daBancherz.NewMonopoly.Handler.TurnHandler;
import unimib.daBancherz.NewMonopoly.Handler.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionHandler implements WebSocketHandler {

    public final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final GameHandler gameHandler;
    private final ChatHandler chatHandler;
    private final TurnHandler turnHandler;
    private final WebSocketReconnect reconnect;
    private final PropertyHandler propertyHandler;
    private final MessageHandler messageHandler;
    ObjectMapper objectMapper = new ObjectMapper();

    // Iniezione di GameHandler e ChatHandler tramite il costruttore
    @Autowired
    public WebSocketConnectionHandler(GameHandler gameHandler, ChatHandler chatHandler, TurnHandler turnHandler,
                                      WebSocketReconnect reconnect, PropertyHandler propertyHandler, MessageHandler messageHandler) {
        this.gameHandler = gameHandler;
        this.chatHandler = chatHandler;
        this.turnHandler = turnHandler;
        this.reconnect = reconnect;
        this.propertyHandler = propertyHandler;
        this.messageHandler = messageHandler;
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
                    String pongMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "pong",
                            "content", "pong"
                    ));
                    session.sendMessage(new TextMessage(pongMessage));
                    break;
                case "LanciaDadi":
                    turnHandler.spostaPedina(session);
                    break;
                case "FineTurno":
                    turnHandler.endTurn(session);
                    break;
                case "InizioPartita":
                    turnHandler.startTurn(session);
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
                case "PingScambiaProprieta":
                    propertyHandler.gestisciProprieta(session);
                    break;
                case "PagaUscitaPrigione":
                    turnHandler.payPrisonExit(session);
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
            PlayerProperties property1;
            PlayerProperties property2;
            Integer offertaMonetaria;

            switch (type) {
                case "!EffettuaScambio":
                    property1 = objectMapper.convertValue(data.get("property1"), PlayerProperties.class);
                    property2 = objectMapper.convertValue(data.get("property2"), PlayerProperties.class);

                    // Gestione sicura di offertaMonetaria
                    Object offertaMonetariaObj = data.get("offertaMonetaria");
                    if (offertaMonetariaObj instanceof String) {
                        offertaMonetaria = Integer.parseInt((String) offertaMonetariaObj); // Converti da stringa
                    } else if (offertaMonetariaObj instanceof Integer) {
                        offertaMonetaria = (Integer) offertaMonetariaObj; // Già un Integer, usa direttamente
                    } else {
                        throw new IllegalArgumentException("Tipo non valido per offertaMonetaria: " + offertaMonetariaObj.getClass());
                    }

                    propertyHandler.effettuaScambio(property1, property2, offertaMonetaria, session);
                    break;
                case "!RispostaScambio":
                    property1 = objectMapper.convertValue(data.get("property1"), PlayerProperties.class);
                    property2 = objectMapper.convertValue(data.get("property2"), PlayerProperties.class);

                    // Gestione sicura di offertaMonetaria
                    offertaMonetariaObj = data.get("offertaMonetaria");
                    if (offertaMonetariaObj instanceof String) {
                        offertaMonetaria = Integer.parseInt((String) offertaMonetariaObj); // Converti da stringa
                    } else if (offertaMonetariaObj instanceof Integer) {
                        offertaMonetaria = (Integer) offertaMonetariaObj; // Già un Integer, usa direttamente
                    } else {
                        throw new IllegalArgumentException("Tipo non valido per offertaMonetaria: " + offertaMonetariaObj.getClass());
                    }

                    boolean flag = (boolean) data.get("exchangeAccepted");
                    propertyHandler.rispostaScambio(property1, property2, offertaMonetaria, flag, session);
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
        List<WebSocketSession> gameSession = gameHandler.getGameSessions().get(gameId);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        if (gameId != null) {
            // Rimuove il giocatore dalla partita
            gameHandler.removePlayerFromGame(gameId, session);
            if(!(gameSession.isEmpty())){
                messageHandler.notifyPlayerDisconnected(gameId, playerName, gameSessions);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
