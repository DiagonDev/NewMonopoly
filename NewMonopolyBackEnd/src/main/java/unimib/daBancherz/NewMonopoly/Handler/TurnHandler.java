package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TurnHandler {

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;

    @Autowired
    public TurnHandler(GameHandler gameHandler, MessageHandler messageHandler) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
    }

    public void endTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        messageHandler.sendSystemMessage(gameId, playerName + " ha concluso il turno", gameHandler.getGameSessions(), session);
        int currentIndex = playersInGame.indexOf(session);

        // Calcola l'indice della prossima sessione in modo circolare
        int nextIndex = (currentIndex + 1) % playersInGame.size();

        // Assegna la sessione successiva
        WebSocketSession nextPlayer = playersInGame.get(nextIndex);

        // Avvia il turno per la prossima sessione
        startTurn(nextPlayer);
    }

    public void startTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);

        String yourTurnMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "turn",
                "content", true,
                "playername", playerName // non so se serve al giocatore che sta facendo il turno, si può anche togliere
        ));

        String notYourTurnMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "turn",
                "content", false,
                "playername", playerName
        ));

        messageHandler.sendSystemMessage(gameId, "È il turno di: " + playerName, gameHandler.getGameSessions(), session);

        for (WebSocketSession playerSession : playersInGame) {
            if (!playerSession.equals(session)) {
                playerSession.sendMessage(new TextMessage(notYourTurnMessage));
            }
            else{
                session.sendMessage(new TextMessage(yourTurnMessage));
            }
        }

    }

    public void rollDice(WebSocketSession session) throws Exception {
        int diceR1 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il primo dado
        int diceR2 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il secondo dado

        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));

        session.sendMessage(new TextMessage(diceRolled));
    }
}
