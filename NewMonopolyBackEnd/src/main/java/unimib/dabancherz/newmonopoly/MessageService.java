package unimib.dabancherz.newmonopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.database.entity.Casella;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.database.service.GameService;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageService {

    private final GameService gameService;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private static final String CONTENT_KEY = "content";
    private static final String TYPE_KEY = "type";
    private static final String PLAYERNAME_KEY = "playerName";
    private static final String BALANCE_KEY = "balance";
    private static final String SYSTEM_KEY = "system";


    public MessageService(GameService gameService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository) {
        this.gameService = gameService;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
    }
    //crea il messaggio Json con il parametro che gli viene inviato
    public String createMessage(Map<String, Object> data) throws IOException {
        return new ObjectMapper().writeValueAsString(data);
    }

    //invia i messaggi a tutti
    private void sendToGame(Map<String, Object> data, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        String message = createMessage(data);
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;
        for (WebSocketSession playerSession : playersInGame) {
            playerSession.sendMessage(new TextMessage(message));
        }
    }

    //serve per creare un messaggio di sistema in Json così che il front end lo metta nella game console
    public void sendSystemMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        //If serve perché i due messaggi che iniziano con... Devono essere inviati solo al giocatore che crea la partita
        if (!(content.startsWith("Ti sei unito alla partita con ID: ") || content.startsWith("#"))) {
            sendToGame(Map.of(TYPE_KEY, SYSTEM_KEY, CONTENT_KEY, content), gameSessions, gameId);
        } else {
            //sendToGame(Map.of(TYPE_KEY, "system", CONTENT_KEY, content), gameSessions, gameId, session, false);
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, SYSTEM_KEY, CONTENT_KEY, content))));
        }
    }

    public void sendUnusedPedine(List<Integer> pedineNonUsate, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws Exception {
        sendToGame(Map.of(TYPE_KEY, "pawnsAvailable", CONTENT_KEY, pedineNonUsate), gameSessions, gameId);
    }

    public void sendPawnMove(Integer pawnId, String playerName, Integer offset, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        sendToGame(Map.of(TYPE_KEY, "pawnMove", "pawnId", pawnId, PLAYERNAME_KEY, playerName, "offset", offset), gameSessions, gameId);
    }

    public void sendJoinMessage(String playerName, Map<String, List<WebSocketSession>> gameSessions, String role, String gameId) throws IOException {
        int balance = giocatoreRepository.saldoGiocatore(playerName, gameId);
        sendToGame(Map.of(TYPE_KEY, "join", PLAYERNAME_KEY, playerName, "userRole", role), gameSessions, gameId);
        sendToGame(Map.of(TYPE_KEY, "playersList", PLAYERNAME_KEY, playerName, BALANCE_KEY, balance), gameSessions, gameId);
    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "gameId", CONTENT_KEY, gameId))));
    }

    public void sendTypePlayer(String playerType, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "user", CONTENT_KEY, playerType))));
    }

    public void notifyPlayerJoin(String gameId, String playerName, WebSocketSession session, Map<String, List<WebSocketSession>> gameSessions, String role) throws Exception {
        // Messaggi per il giocatore che si è unito e per tutti i partecipanti
        sendSystemMessage(gameId, "Ti sei unito alla partita con ID: " + gameId + " con successo!", gameSessions, session);
        sendSystemMessage(gameId, playerName + " si è unito alla partita!", gameSessions, session);
        sendPlayerAndBalance(gameId, playerName, session, role);
        sendJoinMessage(playerName, gameSessions, role, gameId);
    }

    public void sendPlayerAndBalance(String gameId, String playerName, WebSocketSession session, String role) throws Exception {
        List<String> playerJoined = gameService.getPlayersWithIdLowerThan(gameId, playerName);
        for (String player : playerJoined) {
            int balance = giocatoreRepository.saldoGiocatore(player, gameId);
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "playersList", PLAYERNAME_KEY, player, BALANCE_KEY, balance))));
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "join", PLAYERNAME_KEY, playerName, "userRole", role))));
            Thread.sleep(100); //serve per far si che il frontend riesca a ricevere i messaggi e a visualizzarli in tempo
        }
    }

    public void updateBalance(Map<String, List<WebSocketSession>> gameSessions, String gameId, String playerName) throws IOException {
        int balance = giocatoreRepository.saldoGiocatore(playerName, gameId);
        sendToGame(Map.of(TYPE_KEY, "playerBalance", PLAYERNAME_KEY, playerName, BALANCE_KEY, balance), gameSessions, gameId);
    }


    public void rispostaGestisciProprieta(String messaggioRisposta, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "rispostaGestisciProprieta", CONTENT_KEY, messaggioRisposta))));
    }

    public void rispostaAggiornaProprieta(String gameId, String playerName, WebSocketSession session) throws IOException {
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "rispostaAggiornaProprieta", "properties", playerPropertiesList))));
    }

    public void sendErrorMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "errorName"))));
    }

    public void sendErrorGameIdMessage(WebSocketSession session, String gameId) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "errorGameId", "gameId", gameId))));
    }

    public void notifyPlayerDisconnected(String gameId, String playerName, Map<String, List<WebSocketSession>> gameSessions) throws Exception {
        sendToGame(Map.of(TYPE_KEY, SYSTEM_KEY, CONTENT_KEY, playerName + " si è disconnesso dalla partita."), gameSessions, gameId);
    }

    public String createTurnMessage (boolean turn, String playerName) throws IOException {
        return createMessage(Map.of(TYPE_KEY, "turn", CONTENT_KEY, turn, "playername", playerName));
    }

    public void exitPrisonMessage(boolean flag, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "exitPrison", "flag", flag))));
    }

    public void sendDiceResults(WebSocketSession session, int diceR1, int diceR2) throws Exception {
        //invia il risultato dei dati al giocatore che li ha tirati
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "diceRolled", "dice1", diceR1,"dice2", diceR2))));
    }

    public void exchangeRequestMessage(String nomeRichiedente, PlayerProperties property1, PlayerProperties property2, Integer money, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "exchangeRequest", "property1", property1,"property2", property2,"money", money,PLAYERNAME_KEY, nomeRichiedente))));
    }

    public void inviaMessaggio(WebSocketSession session, String type) throws IOException {
        String message = new ObjectMapper().writeValueAsString(Map.of("type", type));
        session.sendMessage(new TextMessage(message));
    }

    public void inviaMessaggio(WebSocketSession session, String type, String key, Object value) throws IOException {
        String message = new ObjectMapper().writeValueAsString(Map.of("type", type, key, value));
        session.sendMessage(new TextMessage(message));
    }

    public void sendPongMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "pong", CONTENT_KEY, "pong"))));
    }

    public void sendVictoryMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "partitaFinita","flag", "vittoria", CONTENT_KEY, "Hai vinto"))));
    }

    public void sendLoseMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "partitaFinita","flag", "sconfitta", CONTENT_KEY, "Il tuo saldo è negativo"))));
    }

    public void updateProperties(String gameId, String playerName, WebSocketSession session) throws Exception {
        List<PlayerProperties> giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId, playerName);
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "updateProperties","properties", giocatorePropertiesList))));
    }

    //serve a creare un messaggio in Json per far si che il forntend riesca a capire chè per la game chat
    public void sendChatMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions) throws Exception {
        sendToGame(Map.of(TYPE_KEY, "chat", CONTENT_KEY, content), gameSessions, gameId);
    }

    //serve per mandare un messaggio contenete la lista di caselle ordinate da stampare
    public void sendBoxOrderMessage(List<Casella> caselle, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        sendToGame(Map.of(TYPE_KEY, "boxOrder", CONTENT_KEY, caselle), gameSessions, gameId);
    }
}