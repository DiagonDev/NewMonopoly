package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Component
public class ChatHandler {

    private final GameHandler gameHandler;

    // Inietta GameHandler tramite il costruttore
    @Autowired
    public ChatHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void chatHandler (String[] messageParts, WebSocketSession session) throws Exception {

        String chatMessage = messageParts[1];
        String gameId = gameHandler.getGameIdBySession(session);
        String nameChat = gameHandler.getPlayerNameBySession(session);

        //invia il messaggio a tutti gli utenti collegati allo stesso gameID sotto forma di messaggioChat
        sendChatMessage(gameId,nameChat+ ": " + chatMessage);
        //broadcastChatMessage(gameId, session, chatMessage);
    }

    //serve a creare un messaggio in Json per far si che il forntend riesca a capire chè per la game chat
    private void sendChatMessage(String gameId, String content) throws Exception {
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        if (playersInGame == null) return;

        // Crea un messaggio di chat come JSON
        String chatMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "chat",
                "content", content
                //"timestamp", Instant.now().toString()
        ));

        // Invia il messaggio a tutti i giocatori della partita
        for (WebSocketSession session : playersInGame) {
            session.sendMessage(new TextMessage(chatMessage));
        }
    }
}
