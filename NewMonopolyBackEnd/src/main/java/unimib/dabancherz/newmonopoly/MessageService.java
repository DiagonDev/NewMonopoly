package unimib.dabancherz.newmonopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
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
    private static final String CONTENTKEY = "content";
    private static final String TYPEKEY = "type";
    private static final String PLAYERNAMEKEY = "playerName";
    private static final String BALANCEKEY = "balance";
    private static final String POINTSKEY = "points";
    private static final String SYSTEMKEY = "system";


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
            sendToGame(Map.of(TYPEKEY, SYSTEMKEY, CONTENTKEY, content), gameSessions, gameId);
        } else {
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, SYSTEMKEY, CONTENTKEY, content))));
        }
    }

    public void sendUnusedPedine(List<Integer> pedineNonUsate, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws Exception {
        sendToGame(Map.of(TYPEKEY, "pawnsAvailable", CONTENTKEY, pedineNonUsate), gameSessions, gameId);
    }

    public void sendPawnMove(Integer pawnId, String playerName, Integer offset, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        sendToGame(Map.of(TYPEKEY, "pawnMove", "pawnId", pawnId, PLAYERNAMEKEY, playerName, "offset", offset), gameSessions, gameId);
    }

    public void sendPlayerPawnPosition(Integer pawnId, String playerName, Integer offset, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "pawnMove", "pawnId", pawnId, PLAYERNAMEKEY, playerName, "offset", offset))));
    }

    public void sendJoinMessage(String playerName, Map<String, List<WebSocketSession>> gameSessions, String role, String gameId) throws IOException {
        Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, playerName);
        int balance = giocatore.getSaldo();
        int points = giocatore.getPuntiFedelta();
        sendToGame(Map.of(TYPEKEY, "join", PLAYERNAMEKEY, playerName, "userRole", role), gameSessions, gameId);
        sendToGame(Map.of(TYPEKEY, "playersList", PLAYERNAMEKEY, playerName, BALANCEKEY, balance, POINTSKEY, points), gameSessions, gameId);
    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "gameId", CONTENTKEY, gameId))));
    }

    public void sendTypePlayer(String playerType, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "user", CONTENTKEY, playerType))));
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
            Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, player);
            int balance = giocatore.getSaldo();
            int points = giocatore.getPuntiFedelta();
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "playersList", PLAYERNAMEKEY, player, BALANCEKEY, balance, POINTSKEY, points))));
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "join", PLAYERNAMEKEY, playerName, "userRole", role))));
            Thread.sleep(100); //serve per far si che il frontend riesca a ricevere i messaggi e a visualizzarli in tempo
        }
    }

    public void updateBalance(Map<String, List<WebSocketSession>> gameSessions, String gameId, String playerName) throws IOException {
        Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, playerName);
        int balance = giocatore.getSaldo();
        int points = giocatore.getPuntiFedelta();
        sendToGame(Map.of(TYPEKEY, "playerBalance", PLAYERNAMEKEY, playerName, BALANCEKEY, balance,  POINTSKEY, points), gameSessions, gameId);
    }

    public void rispostaGestisciProprieta(String messaggioRisposta, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "rispostaGestisciProprieta", CONTENTKEY, messaggioRisposta))));
    }

    public void rispostaAggiornaProprieta(String gameId, String playerName, WebSocketSession session) throws IOException {
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "rispostaAggiornaProprieta", "properties", playerPropertiesList))));
    }

    public void sendErrorMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "errore, partita piena o nome già presente nella partita"))));
    }

    public void sendErrorGameIdMessage(WebSocketSession session, String gameId) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "errorGameId", "gameId", gameId))));
    }

    public void sendDisconnected(Map<String, List<WebSocketSession>> gameSessions, String gameId, String playerName) throws IOException {
        sendToGame(Map.of(TYPEKEY, "deletePlayer", PLAYERNAMEKEY, playerName), gameSessions, gameId);
    }

    public String createTurnMessage (boolean turn, String playerName) throws IOException {
        return createMessage(Map.of(TYPEKEY, "turn", CONTENTKEY, turn, "playername", playerName));
    }

    public void exitPrisonMessage(boolean flag, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "exitPrison", "flag", flag))));
    }

    public void sendDiceResults(WebSocketSession session, int diceR1, int diceR2) throws Exception {
        //invia il risultato dei dati al giocatore che li ha tirati
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "diceRolled", "dice1", diceR1,"dice2", diceR2))));
    }

    public void exchangeRequestMessage(String nomeRichiedente, PlayerProperties property1, PlayerProperties property2, Integer money, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "exchangeRequest", "property1", property1,"property2", property2,"money", money, PLAYERNAMEKEY, nomeRichiedente))));
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
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "pong", CONTENTKEY, "pong"))));
    }

    public void sendVictoryMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "partitaFinita","flag", "vittoria", CONTENTKEY, "Hai vinto"))));
    }

    public void sendLoseMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "partitaFinita","flag", "sconfitta", CONTENTKEY, "Il tuo saldo è negativo"))));
    }

    public void updateProperties(String gameId, String playerName, WebSocketSession session) throws Exception {
        List<PlayerProperties> giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId, playerName);
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPEKEY, "updateProperties","properties", giocatorePropertiesList))));
    }

    //serve a creare un messaggio in Json per far si che il forntend riesca a capire chè per la game chat
    public void sendChatMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions) throws Exception {
        sendToGame(Map.of(TYPEKEY, "chat", CONTENTKEY, content), gameSessions, gameId);
    }

    //serve per mandare un messaggio contenete la lista di caselle ordinate da stampare
    public void sendBoxOrderMessage(int[] caselle, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        sendToGame(Map.of(TYPEKEY, "boxOrder", CONTENTKEY, caselle), gameSessions, gameId);
    }
}