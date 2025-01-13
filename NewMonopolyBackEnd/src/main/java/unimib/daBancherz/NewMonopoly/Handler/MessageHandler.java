package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandler {

    private final GameHandler gameHandler;

    public MessageHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    //serve per creare un messaggio di sistema in Json così che il forntend lo metta nella game console
    public void sendSystemMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;

        // Crea un messaggio di sistema come JSON
        String systemMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "system",
                "content", content
        ));

        //l'if serve perchè i due messaggi che iniziano con... devono essere inviati solo al giocatore che crea la partita
        if (!(content.startsWith("Ti sei unito alla partita con ID: ") || content.startsWith("#"))) {
            // Invia il messaggio a tutti i giocatori della partita
            for (WebSocketSession sessions : playersInGame) {
                sessions.sendMessage(new TextMessage(systemMessage));
            }
        } else {
            session.sendMessage(new TextMessage(systemMessage));
        }
    }

    public void sendUnusedPedine(List<Integer> pedineNonUsate, WebSocketSession session) throws Exception {
        if (session == null || !session.isOpen()) {
            return; // Non fare nulla se la sessione è null o chiusa
        }

        // Crea il messaggio con le pedine non usate in formato JSON
        String pedineMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "unusedPedine",
                "pedine", pedineNonUsate
        ));

        // Invia il messaggio alla sessione specificata
        session.sendMessage(new TextMessage(pedineMessage));
    }


    public void sendJoinMessage(String playerName, Map<String, List<WebSocketSession>> gameSessions, String role, String gameId) throws IOException {

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        //mettere il metodo che prende dal database i nomi dei giocatori che sono entrati prima di questa sessione


        //crea il messaggio di tipo join che contiene il nome del giocatore
        String joinMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "join",
                "playerName", playerName,
                "userRole", role
        ));

        for (WebSocketSession sessions : playersInGame) {
            sessions.sendMessage(new TextMessage(joinMessage));
        }
    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {

        String gameMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "gameId",
                "content", gameId
        ));

        session.sendMessage(new TextMessage(gameMessage));
    }

    public void sendTypePlayer(String paleyrType, WebSocketSession session) throws Exception {

        String typePlayerMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "user",
                "content", paleyrType
        ));

        session.sendMessage(new TextMessage(typePlayerMessage));
    }

    public void notifyPlayerJoin(String gameId, String playerName, WebSocketSession session, Map<String, List<WebSocketSession>> gameSessions, String role) throws Exception {

        // Messaggi per il giocatore che si è unito e per tutti i partecipanti
        sendSystemMessage(gameId, "Ti sei unito alla partita con ID: " + gameId + " con successo!", gameSessions, session);
        sendSystemMessage(gameId, playerName + " si è unito alla partita!", gameSessions, session);

        /*// Notifica all'admin della partita
        WebSocketSession admin = playersInGame.get(0);
        sendJoinMessage(playerName, admin);
        sendTypePlayer("giocatore", admin);*/

        /*
         Ale - Stesso discorso qua, ho bisogno che il join arrivi a tutti
         */
        sendJoinMessage(playerName, gameSessions, role, gameId);
        //sendTypePlayer("giocatore", session);

    }
}
